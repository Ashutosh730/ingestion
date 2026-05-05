package com.logAnalyzer.ingestion.utils;

public class LogUploadUtils {
    public static String generateUniqueFileName(String originalFileName, String uploadSessionId) {
        String[] parts = originalFileName.split("\\.(?=[^.]+$)");
        return parts[0] + "_" + uploadSessionId + "." + parts[1];
    }
}
