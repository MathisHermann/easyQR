/*
 * Copyright (c) 2020. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

serviceEndpointURL = window.location.protocol + "//" + window.location.host;

function login(email, password, remember, callback) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        headers: {
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        url: serviceEndpointURL + "/login",
        data: JSON.stringify({
            "email": email,
            "password": password,
            "remember": remember
        }),
        success: function (data, textStatus, response) {
            callback(true);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR, textStatus, errorThrown);
            callback(false);
        }
    });
}

function validateLogin(callback) {
    $.ajax({
        type: "HEAD",
        url: serviceEndpointURL + "/validate",
        success: function (data, textStatus, response) {
            callback(true);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            callback(false);
        }
    });
}

function register(name, email, password, callbackSuccess, callbackError) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        headers: {
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        url: serviceEndpointURL + "/register",
        data: JSON.stringify({
            "name": name,
            "email": email,
            "password": password
        }),
        success: function (data, textStatus, response) {
            callbackSuccess(true);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR, textStatus, errorThrown);
            callbackError(jqXHR.responseJSON.message);
        }
    });
}

function getProfile(callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: serviceEndpointURL + "/profile",
        success: function (data, textStatus, response) {
            callback(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR, textStatus, errorThrown);
        }
    });
}

function putProfile(name, email, password, mobile_number, callbackSuccess, callbackError) {
    $.ajax({
        type: "PUT",
        contentType: "application/json",
        headers: {
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        url: serviceEndpointURL + "/profile",
        data: JSON.stringify({
            "name": name,
            "email": email,
            "password": password,
            "mobileNumber": mobile_number
        }),
       success: function (data, textStatus, response) {
            callbackSuccess(true);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR, textStatus, errorThrown);
            callbackError(jqXHR.responseJSON.message);
        }
    });
}

function postQrCode(qrImage, qrType, qrContent, qrURLContent, callbackSuccess, callbackError) {

    let fd = new FormData();

    // Check file selected or not
    if (qrImage != null) {
        fd.append("content", qrContent);
        fd.append("qrURLContent", qrURLContent)
        fd.append("type", qrType);

        // Convert into right data format
        var dataURL = qrImage.toDataURL();
        var bytes = atob(dataURL.split(',')[1]);
        var arr = new Uint8Array(bytes.length);

        for (var i = 0; i < bytes.length; i++) {
            arr[i] = bytes.charCodeAt(i);
        }

        var blob = new Blob([arr], {type: 'image/png'});
        var fileName = 'qrCode_' + Date.now() + '.png';    //filename
        fd.append('file', blob, fileName);

        return $.ajax({
            headers: {
                "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
            },
            url: '/api/uploadQrCode',
            type: 'post',
            data: fd,
            contentType: false,
            processData: false
        });
    }
}

function getQrCodes(callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: serviceEndpointURL + "/api/getQrCodePath",
        success: function (data, textStatus, response) {
            callback(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR, textStatus, errorThrown);
        }
    });
}

function putQrCode(userName, qrCodeOldContent, qrContent, qrURLContent, callbackSuccess, callbackError) {

    let putFd = new FormData();

    // Check file selected or not
    if (qrURLContent.length > 0) {
        putFd.append("oldContent", qrCodeOldContent);
        putFd.append("newContent", qrContent);
        putFd.append("qrURLContent", qrURLContent);


        return $.ajax({
            headers: {
                "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
            },
            url: serviceEndpointURL + "/api/updateQrCode", // '/api/uploadQrCode',
            type: 'put',
            data: putFd,
            contentType: false,
            processData: false
        });

    }

}

function deleteQrCode(qrCodeID, callback) {
    $.ajax({
        type: "DELETE",
        headers: {
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        url: serviceEndpointURL + "/api/deleteQrCode/" + qrCodeID
    });
}

function getQrCodeJSON(id, qrType, content) {
    if (id === null) {
        return JSON.stringify({
            "qrType": qrType,
            "content": content
        });
    }
    return JSON.stringify({
        "id": id,
        "qrType": qrType,
        "content": content,
        "image": image
    });
}

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [, ""])[1].replace(/\+/g, '%20')) || null;
}

function getCookie(name) {
    var match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
}

