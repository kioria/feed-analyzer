package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * <p>Processes asynchronous event streams from Kafka using Spring Kafka template,
 * <p>which has very little additional operational complexity beyond the normal Kafka producer and consumer APIs.
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
