package com.example.keymanager.controller;

import com.example.keymanager.dto.*;
import com.example.keymanager.service.CoseSignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/coseSign")
@RequiredArgsConstructor
public class CoseSignController {

    private final CoseSignService coseSignService;

    @PostMapping
    public ResponseEntity<CoseSignResponse> sign(@RequestBody CoseSignRequest request) {
        CoseSignResponse response = new CoseSignResponse();
        response.setId(request.getId());
        response.setVersion(request.getVersion());
        response.setResponsetime(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        response.setMetadata(request.getMetadata());

        try {
            CoseSignResponse.ResponseBody body = coseSignService.sign(request.getRequest());
            response.setResponse(body);
        } catch (Exception e) {
            CoseError error = new CoseError();
            error.setErrorCode("SIGNING_FAILED");
            error.setMessage(e.getMessage());
            response.setErrors(java.util.List.of(error));
        }

        return ResponseEntity.ok(response);
    }
}
