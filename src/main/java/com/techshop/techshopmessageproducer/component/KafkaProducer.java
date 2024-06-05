package com.techshop.techshopmessageproducer.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Component
@Slf4j
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${topic}")
    private String topic;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private final List<String> products;
    private final List<String> customers;
    private final List<String> addresses;

    public KafkaProducer(
            ResourceLoader resourceLoader,
            @Value("${products.file}") String productsFile,
            @Value("${customers.file}") String customersFile,
            @Value("${addresses.file}") String addressesFile
    ) {
        this.products = loadFile(resourceLoader, productsFile);
        this.customers = loadFile(resourceLoader, customersFile);
        this.addresses = loadFile(resourceLoader, addressesFile);
    }

    @Scheduled(fixedRate = 1000)
    @Timed(value = "kafka.produce.time", description = "Time taken to produce messages")
    @Counted(value = "kafka.produce.count", description = "Number of produced messages")
    public void produce() {
        try {
            Map<String, Object> messageMap = generateRandomMessage();
            String message = convertToJson(messageMap);
            kafkaTemplate.send(topic, message);
            log.info("Produced message: {}", message);
        } catch (Exception e) {
            log.error("Error producing message: {}", e.getMessage(), e);
        }
    }

    private List<String> loadFile(ResourceLoader resourceLoader, String filePath) {
        List<String> lines = new ArrayList<>();
        try {
            Resource resource = resourceLoader.getResource("classpath:" + filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            log.error("Error loading file: {}", e.getMessage(), e);
        }
        return lines;
    }

    private Map<String, Object> generateRandomMessage() {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("orderId", UUID.randomUUID().toString());
        messageMap.put("product", getRandomElement(products));
        messageMap.put("quantity", random.nextInt(10) + 1);
        messageMap.put("price", random.nextDouble() * 2000);
        messageMap.put("customer", getRandomElement(customers));
        messageMap.put("address", getRandomElement(addresses));
        return messageMap;
    }

    private String getRandomElement(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    private String convertToJson(Map<String, Object> messageMap) {
        try {
            return objectMapper.writeValueAsString(messageMap);
        } catch (Exception e) {
            log.error("Error converting message to JSON: {}", e.getMessage(), e);
            return null;
        }
    }
}
