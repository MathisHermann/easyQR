/*
 * Copyright (c) 2020. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package rocks.process.easyqr.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import rocks.process.easyqr.data.domain.EasyQrCode;
import rocks.process.easyqr.data.domain.EasyQrUser;
import rocks.process.easyqr.data.repository.EasyQrCodeRepository;

import javax.validation.Valid;

@Service
@Validated
public class EasyQrCodeService {

    @Autowired
    private EasyQrCodeRepository easyQrCodeRepository;

    public void saveEasyQrCode(@Valid EasyQrCode easyQrCode) {
        easyQrCodeRepository.save(easyQrCode);
    }

    public void editQrCode(EasyQrUser easyQrUser, EasyQrCode easyQrCode) {
        if (easyQrUser != null) {
            easyQrCodeRepository.save(easyQrCode);
        }
    }

    public void deleteQrCode(Long qrCodeId) {
        easyQrCodeRepository.deleteById(qrCodeId);
    }

    public String findRedirectByTimestamp(String timestamp) {
        String qrURLContent = "easyqr.herokuapp.com/code/" + timestamp;
        String redirectTarget = "/";

        try {
            EasyQrCode easyQrCode = easyQrCodeRepository.findByQrURLContent(qrURLContent);
            if (easyQrCode != null) {
                redirectTarget = "https://"+ easyQrCode.getContent();
            }
        } catch (Exception e) {
        }
        return redirectTarget;
    }

}
