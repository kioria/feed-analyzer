package com.consumer;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class AnalyzingConsumer implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private KafkaStreams consumerStream;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        consumerStream.start();
    }
}
