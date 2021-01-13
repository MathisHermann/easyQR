/*
 * Copyright (c) 2020. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package rocks.process.easyqr.data.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EasyQrUser {

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty(message = "Please provide a name.")
    private String name;

    @Email(message = "Please provide a valid e-mail.")
    @NotEmpty(message = "Please provide an e-mail.")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // only create object property from JSON
    private String password;

    private String mobileNumber;

    @OneToMany(mappedBy = "easyQrUser", fetch = FetchType.EAGER)
    private List<EasyQrCode> easyQrCodes = new ArrayList<>();

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        String transientPassword = this.password;
        this.password = null;
        return transientPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<EasyQrCode> getEasyQrCodes() {
        return easyQrCodes;
    }

    public void setEasyQrCodes(List<EasyQrCode> easyQrCodes) {
        this.easyQrCodes = easyQrCodes;
    }

    public void addEasyQrCode(EasyQrCode easyQrCode) {
        easyQrCodes.add(easyQrCode);
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
