package rocks.process.easyqr.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import rocks.process.easyqr.exception.FileStorageException;
import rocks.process.easyqr.exception.MyFileNotFoundException;
import rocks.process.easyqr.middleware.PathCleaner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final String path = "./easy-qr-web/upload";
    private final Path fileStorageLocation = Paths.get(path);
    private final String userUploads = "userUploads/";

    @Autowired
    private EasyQrUserService easyQrUserService;

    @Autowired
    public FileStorageService() {
        //      this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
        //            .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String[] storeFile(MultipartFile file) {

        String[] answer = new String[2];

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Clean the filename with PathCleaner
            fileName = PathCleaner.cleanPathName(fileName);
            // Copy file to the target location (Replacing existing file with the same name)

            // If the image is added via the api, it is saved in the directory '/testing'
            String currentUserName;
            try {
                currentUserName = easyQrUserService.getCurrentEasyQrUser().getName();
            } catch (Exception e) {
                currentUserName = "testingPath";
            }
            Path targetLocation = fileStorageLocation.resolve(userUploads + currentUserName + "/" + fileName);

            if (!Files.isDirectory(fileStorageLocation.resolve(userUploads + currentUserName))) {
                try {
                    Files.createDirectories(targetLocation);
                } catch (Exception ex) {
                    throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
                }
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            answer[0] = fileName;
            answer[1] = targetLocation.toString();

            return answer;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResourceWithFileName(String userName, String fileName) {
        try {

            String currentUserName;
            try {
                currentUserName = easyQrUserService.getCurrentEasyQrUser().getName();
            } catch (Exception e) {
                currentUserName = userName;
            }

            Path filePath = this.fileStorageLocation.resolve(userUploads + currentUserName + "/" + fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

}