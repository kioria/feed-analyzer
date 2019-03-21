package com.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FeedProducer {
    @Value("${tpd.topic-deduplicator}")
    private String orders;

    @Value("${tpd.topic-additional-details}")
    private String categories;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrder(String message) {
        this.kafkaTemplate.send(orders, message);

    } public void sendCategory(String message) {
        this.kafkaTemplate.send(categories, message);
    }
}
