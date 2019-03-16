package com.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FeedConsumer {
    private final Logger logger = LoggerFactory.getLogger(FeedConsumer.class);

    @KafkaListener(topics =  "#{'${tpd.topic-name}'}", group = "group_id")
    public void consume(String message) throws IOException {
        logger.info(String.format("#### -> Consumed message -> %s", message));
    }
}
