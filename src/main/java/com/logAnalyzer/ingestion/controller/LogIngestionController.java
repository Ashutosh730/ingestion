package com.logAnalyzer.ingestion.controller;

import com.logAnalyzer.ingestion.model.LogUploadRequest;
import com.logAnalyzer.ingestion.model.LogUploadResponse;
import com.logAnalyzer.ingestion.service.LogIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LogIngestionController {

    private final LogIngestionService logIngestionService;

    @PostMapping(value = "/logs/upload", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LogUploadResponse> logUpload(@ModelAttribute LogUploadRequest logUploadRequest) throws IOException {
        if (logUploadRequest.getFile() == null) {
            return ResponseEntity.badRequest().body(new LogUploadResponse("No file uploaded", logUploadRequest.getFile().getOriginalFilename(), "SessionId not generated"));
        }
        if (logUploadRequest.getFile().isEmpty()) {
            return ResponseEntity.badRequest().body(new LogUploadResponse("Empty file uploaded", logUploadRequest.getFile().getOriginalFilename(), "SessionId not generated"));
        }
        LogUploadResponse response = logIngestionService.uploadLogFile(logUploadRequest);
        return ResponseEntity.ok(response);
    }
}
