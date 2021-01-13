package rocks.process.easyqr.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rocks.process.easyqr.business.service.EasyQrCodeService;
import rocks.process.easyqr.business.service.EasyQrUserService;
import rocks.process.easyqr.business.service.FileStorageService;
import rocks.process.easyqr.data.domain.EasyQrCode;
import rocks.process.easyqr.data.domain.EasyQrUser;
import rocks.process.easyqr.payload.UploadFileResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private EasyQrCodeService easyQrCodeService;
    @Autowired
    private EasyQrUserService easyQrUserService;

    @PostMapping("/uploadQrCode")
    @ResponseBody
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("content") String qrCodeContent, @RequestParam("qrURLContent") String qrURLContent, @RequestParam("type") String qrCodeType) {
        boolean qrCodeAlreadyExisting = false;
        try {

            EasyQrUser currentUser = easyQrUserService.getCurrentEasyQrUser();

            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
            if (fileExtension.equals(".png") && file.getContentType().equals("image/png")) {

                // Store the file. Returns Array with the filename and the correct path.
                String[] answer = fileStorageService.storeFile(file);
                String fileName = answer[0];
                String filePath = answer[1];

                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/downloadQrCode/")
                        .path(currentUser.getName())
                        .path("/")
                        .path(fileName)
                        .toUriString();
                try {
                    // Check if there is already a code existing with the same name
                    for (EasyQrCode ec : currentUser.getEasyQrCodes())
                        if (ec.getContent().equals(qrCodeContent) && ec.getQrType().equals(qrCodeType)) {
                            qrCodeAlreadyExisting = true;
                        }

                    if (!qrCodeAlreadyExisting) {
                        EasyQrCode easyQrCode = new EasyQrCode(qrCodeType, qrCodeContent, qrURLContent, filePath, currentUser);
                        easyQrCodeService.saveEasyQrCode(easyQrCode);
                        easyQrUserService.getCurrentEasyQrUser().addEasyQrCode(easyQrCode);

                        return new UploadFileResponse(fileName, fileDownloadUri,
                                file.getContentType(), file.getSize(), true, "QR-Code with the content: \""
                                + qrCodeContent + "\" successfully created");
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                    System.out.println("No code created.");
                }
            }
        } catch (NullPointerException e) {
        }
        return new UploadFileResponse("na", "na",
                "na", 0, false, (qrCodeAlreadyExisting ? "This exact QR-Code already exists." : "na"));
    }

    @GetMapping("/downloadQrCode/{userName}/{fileName}")
    public ResponseEntity<Resource> downloadSingleFile(@PathVariable String userName,
                                                       @PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResourceWithFileName(userName, fileName);
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Return a list with all path names and further information about the qr codes.
     * - first: Name (resolves later in path - calculated in frontend)
     * - second: Content of the qr code
     * - third: Type of the qr code
     *
     * @return List<String>
     */
    @GetMapping(value = "/getQrCodePath", produces = "application/json")
    @ResponseBody
    public List<String> getQrCodePath() {
        List<String> allQrCodeInformation = new ArrayList<>();

        EasyQrUser equ = easyQrUserService.getCurrentEasyQrUser();
        List<EasyQrCode> eqCodes = equ.getEasyQrCodes();

        allQrCodeInformation.add(equ.getName());

        for (EasyQrCode ecq : eqCodes) {
            allQrCodeInformation.add(ecq.getName());
            allQrCodeInformation.add(ecq.getContent());
            allQrCodeInformation.add(ecq.getQrType());
            allQrCodeInformation.add(ecq.getQrURLContent());
            allQrCodeInformation.add(ecq.getId().toString());
        }
        return allQrCodeInformation;
    }

    @PutMapping(path = "/updateQrCode")
    @ResponseBody
    public ResponseEntity<Void> putEasyQrCode(@RequestParam("oldContent") String qrCodeOldContent,
                                              @RequestParam("newContent") String qrCodeNewContent,
                                              @RequestParam("qrURLContent") String qrURLContent) {

        String qrCodeType = "url-redirect";
        try {
            EasyQrUser currentUser = easyQrUserService.getCurrentEasyQrUser();
            try {
                // Check if there is already a code existing with the same name and same type
                for (EasyQrCode ec : currentUser.getEasyQrCodes()) {
                    if (ec.getContent().equals(qrCodeOldContent) && ec.getQrType().equals(qrCodeType)) {
                        ec.setContent(qrCodeNewContent);
                        easyQrCodeService.editQrCode(currentUser, ec);
                        return ResponseEntity.accepted().build();
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                System.out.println("No code updated.");
            }

        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping(path = "/deleteQrCode/{qrCodeId}")
    public ResponseEntity<Void> deleteEasyQrCode(@PathVariable(value = "qrCodeId") String qrCodeIdAsString) {

        try {
            easyQrCodeService.deleteQrCode(Long.parseLong(qrCodeIdAsString));
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }
}