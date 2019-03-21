package com.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Entry;
import com.model.Notification;
import com.repository.ExtraDetailsService;
import com.repository.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static com.repository.OrderService.orderCategories;
import static com.repository.OrderService.orderItems;

/**
 * Figures out category and item and sends messages in infinite loop
 */
@Service
public class FeedSimulator implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private OrderService orderService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${tpd.base-url}")
    private String baseUrl;
    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @Autowired
    private void setRestTemplate(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        startEventsSimulatingProducer();
    }

    private void startEventsSimulatingProducer() {
        new Thread(() -> {
            Arrays.stream(ExtraDetailsService.orderCategories).forEach(category -> {
                restTemplate.exchange(getCategoryPath(),
                        HttpMethod.POST,
                        new HttpEntity(category, new HttpHeaders()),
                        new ParameterizedTypeReference<String>() {
                        }, new Object[0]);
            });
            while (true) {
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                // Select a category.
                String category = orderCategories[rng.nextInt(orderCategories.length)];
                // Select an item.
                String item = orderItems.get(category).get(rng.nextInt(orderItems.get(category).size()));

                String id = String.valueOf(rng.nextInt() & Integer.MAX_VALUE);
                Notification notification = getNotification(category, item, id);

                try {
                    restTemplate.exchange(getOrderPath(),
                            HttpMethod.POST,
                            new HttpEntity(notification, new HttpHeaders()),
                            new ParameterizedTypeReference<String>() {
                            }, new Object[0]);
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    //ignore
                } // Close try/catch on Thread.sleep.
            } // Close infinite loop.
        }).start();
    }

    private Notification getNotification(String category, String item, String id) {
        return Notification.builder()
                            .object(category)
                            .entry(Arrays.asList(Entry.builder()
                                    .changed_fields(Arrays.asList(item))
                                    .id(id)
                                    .uid(id)
                                    .time(String.valueOf(System.currentTimeMillis()))
                                    .build()))
                            .build();
    }

    private String getOrderPath() {
        return Paths.get(this.baseUrl, new String[]{"/order"}).toString().replace(":/", "://");
    }

    private String getCategoryPath() {
        return Paths.get(this.baseUrl, new String[]{"/category"}).toString().replace(":/", "://");
    }
}
