package rocks.process.easyqr.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class EasyQrCode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String qrType;

    @NotEmpty(message = "No content in QR-Code needed!")
    private String content;

    private String mobile;

    @ManyToOne
    @JsonIgnore
    private EasyQrUser easyQrUser;

    private Long easyQrUserID;

    @NotEmpty
    private String imagePath;

    private String qrURLContent;

    private boolean isRedirect;



    public EasyQrCode(String qrType, String content, String qrURLContent, String imagePath, EasyQrUser easyQrUser) {
        this.qrType = qrType;
        this.content = content;
        this.qrURLContent = qrURLContent;
        this.imagePath = imagePath;
        this.easyQrUser = easyQrUser;
        this.easyQrUserID = easyQrUser.getId();

        if (qrURLContent.equals("url-redirect"))
            isRedirect = true;

        String[] imagePathParts = imagePath.split("/");
        this.name = imagePathParts[imagePathParts.length - 1];
    }

    public EasyQrCode() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public EasyQrUser getEasyQrUser() {
        return easyQrUser;
    }

    public void setEasyQrUser(EasyQrUser easyQrUser) {
        this.easyQrUser = easyQrUser;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getEasyQrUserID() {
        return easyQrUserID;
    }

    public void setEasyQrUserID(Long easyQrUserID) {
        this.easyQrUserID = easyQrUserID;
    }

    public String toString() {
        return  content + ", " + qrType + ", " + easyQrUserID;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        if (imagePath == null || id == null) return null;

        return "/user-photos/" + id + "/" + imagePath;
    }

    public String getQrURLContent() {
        return qrURLContent;
    }

    public void setQrURLContent(String qrURLContent) {
        this.qrURLContent = qrURLContent;
    }

    public boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(boolean redirect) {
        isRedirect = redirect;
    }
}
