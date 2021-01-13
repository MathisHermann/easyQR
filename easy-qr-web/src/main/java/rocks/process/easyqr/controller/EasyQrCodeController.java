/*
 * Copyright (c) 2020. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package rocks.process.easyqr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import rocks.process.easyqr.business.service.EasyQrCodeService;

@Controller
@RequestMapping(path = "/code")
public class EasyQrCodeController {

    @Autowired
    EasyQrCodeService easyQrCodeService;

    @GetMapping("/create")
    public String getCreateView() {
        return "../qrcode/create.html";
    }

    @GetMapping("/edit")
    public String getQrCodeEditView(){
        return "../qrcode/edit.html";
    }

    @GetMapping("/{timestamp}")
    public RedirectView getRedirectView(@PathVariable(value = "timestamp") String timestamp) {
        RedirectView redirectView = new RedirectView();
        String redirectTarget = easyQrCodeService.findRedirectByTimestamp(timestamp);
        redirectView.setUrl(redirectTarget);
        return redirectView;
    }

}
