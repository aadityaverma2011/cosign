package com.example.keymanager.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CoseSignRequest {
    private String id;
    private String version;
    private String requesttime;
    private Map<String, Object> metadata;
    private CoseSignInnerRequest request;
}
