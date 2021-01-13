/*
 * Copyright (c) 2020. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package rocks.process.easyqr.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import rocks.process.easyqr.data.domain.EasyQrUser;
import rocks.process.easyqr.data.repository.EasyQrUserRepository;

import javax.validation.Valid;
import javax.validation.Validator;

@Service
@Validated
public class EasyQrUserService {

    @Autowired
    private EasyQrUserRepository easyQrUserRepository;
    @Autowired
    Validator validator;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveEasyQrUser(@Valid EasyQrUser easyQrUser) throws Exception {
        if (easyQrUser.getId() == null) {
            if (easyQrUserRepository.findByEmail(easyQrUser.getEmail()) != null) {
                throw new Exception("Email address " + easyQrUser.getEmail() + " already assigned another agent.");
            }
        } else if (easyQrUserRepository.findByEmailAndIdNot(easyQrUser.getEmail(), easyQrUser.getId()) != null) {
            throw new Exception("Email address " + easyQrUser.getEmail() + " already assigned another agent.");
        }
        easyQrUser.setPassword(passwordEncoder.encode(easyQrUser.getPassword()));
        easyQrUserRepository.save(easyQrUser);
    }

    public EasyQrUser getCurrentEasyQrUser() {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return easyQrUserRepository.findByEmail(userEmail);
    }
}
