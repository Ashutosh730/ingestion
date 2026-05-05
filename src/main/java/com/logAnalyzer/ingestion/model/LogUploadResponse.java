package com.logAnalyzer.ingestion.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogUploadResponse {
    private String message;
    private String fileName;
    private String uploadSessionId;
}
