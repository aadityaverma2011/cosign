package com.example.keymanager.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CoseSignResponse {
    private String id;
    private String version;
    private String responsetime;
    private Map<String, Object> metadata;
    private ResponseBody response;
    private List<CoseError> errors;

    @Data
    public static class ResponseBody {
        private String coseSignedData;
        private String timestamp;
    }
}
