package com.example.keymanager.dto;

import lombok.Data;

@Data
public class CoseSignInnerRequest {
    private String cosePayload;
    private String applicationId;
    private String referenceId;
    private String coseProtectedHeader;
    private String coseUnprotectedHeader;
}
