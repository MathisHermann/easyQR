/*
 * Copyright (c) 2020. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package rocks.process.easyqr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rocks.process.easyqr.business.service.EasyQrUserService;
import rocks.process.easyqr.data.domain.EasyQrUser;

@Controller
public class EasyQrUserController {

    @Autowired
    private EasyQrUserService easyQrUserService;

    @GetMapping("/login")
    public String getLoginView() {
        return "login.html";
    }

    @GetMapping("/register")
    public String getRegisterView() {
        return "register.html";
    }

    @PostMapping("/register")
    public ResponseEntity<Void> postRegister(@RequestBody EasyQrUser easyQrUser)  {
        try {
            easyQrUserService.saveEasyQrUser(easyQrUser);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public String getDashboardView() throws Exception {
        return "dashboard.html";
    }

    @GetMapping("/faq")
    public String getFAQView() throws Exception {
        return "faq.html";
    }

    @GetMapping("/user/settings")
    public String getProfileView() {
        return "../user/settings.html";
    }

    @GetMapping("/profile")
    public @ResponseBody
    EasyQrUser getProfile() {
        return easyQrUserService.getCurrentEasyQrUser();
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> putProfile(@RequestBody EasyQrUser easyQrUser) {
        try {
            easyQrUser.setId(easyQrUserService.getCurrentEasyQrUser().getId());
            easyQrUserService.saveEasyQrUser(easyQrUser);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/validate", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<Void> init() {
        return ResponseEntity.ok().build();
    }
}