package com.logAnalyzer.ingestion;

import com.logAnalyzer.ingestion.model.LogMessageModel;
import com.logAnalyzer.ingestion.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class IngestionApplicationTests {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaTemplate<String, LogMessageModel> kafkaTemplate;

    @Test
    void contextLoads() {
        assertNotNull(kafkaProducerService);
        assertNotNull(kafkaTemplate);
    }

}
