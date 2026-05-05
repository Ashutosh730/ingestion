package com.logAnalyzer.ingestion.service;

import com.logAnalyzer.ingestion.config.FileStorageConfig;
import com.logAnalyzer.ingestion.model.LogMessageModel;
import com.logAnalyzer.ingestion.model.LogUploadRequest;
import com.logAnalyzer.ingestion.model.LogUploadResponse;
import com.logAnalyzer.ingestion.utils.LogUploadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class LogIngestionService {

    private final FileStorageConfig fileStorageConfig;
    private final KafkaProducerService producerService;
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".log", ".txt");

    public LogUploadResponse uploadLogFile(LogUploadRequest logUploadRequest) throws IOException {
        String result = validateFile(logUploadRequest.getFile());
        if(result != null) {
            LogUploadResponse response = new LogUploadResponse();
            response.setFileName(logUploadRequest.getFile().getOriginalFilename());
            response.setMessage(result);
            return response;
        }

        String uploadSessionId = UUID.randomUUID().toString();
        MultipartFile file = logUploadRequest.getFile();
        String fileName = LogUploadUtils.generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()), uploadSessionId);

        Path uploadDir = Paths.get(System.getProperty("user.dir"), fileStorageConfig.getUploadDir());
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path uploadPath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

        LogUploadResponse response = new LogUploadResponse();
        response.setUploadSessionId(uploadSessionId);
        response.setFileName(file.getOriginalFilename());
        response.setMessage("File uploaded successfully");

        processLogFileAndSendToKafka(uploadPath, response.getUploadSessionId());
        return response;
    }

    public void processLogFileAndSendToKafka(Path file, String uploadSessionId) {

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogMessageModel logMsg = new LogMessageModel();
                logMsg.setUploadSessionId(uploadSessionId);
                logMsg.setLogLine(line);
                logMsg.setFileName(file.getFileName().toString());
                logMsg.setTimestamp(System.currentTimeMillis());

                producerService.sendMessage("log-ingestion",logMsg);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }


    private String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty";
        }

        String name = file.getOriginalFilename();
        if (name == null || !name.contains(".")) {
            return "Invalid file";
        }

        String extension = name.substring(name.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return "Only .log and .txt files are allowed";
        }
        return null;
    }
}
