package com.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Entry;
import com.model.Notification;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FeedSimulator implements ApplicationListener<ContextRefreshedEvent> {
    static final Map<String, List<String>> orderItems = new HashMap() {{
        put("Household and Pets", Arrays.asList(
                "HydroSilk",
                "Tide",
                "MilkBone",//pet food
                "Hefty",//disposable plastic cups
                "Cascade",//dishwasher detergent
                "Ziploc",//sandwich bags
                "AirHeads"));
        put("Food", Arrays.asList(
                "Milano",//cookie
                "Puffs",//round cookie
                "GoldFish",//crackers
                "Skippy"//peanut butter
        ));
        put("Beverages", Arrays.asList(
                "RedBull",
                "CocaCola",
                "Folgers",//colombian coffee
                "JackDaniels"
        ));
        put("Beauty and Grooming", Arrays.asList(
                "Probiotic",//acidophilus tables welness
                "SlimFast snack bites",//advanced nutrition snack welness
                "Now apple cider",//vinegar
                "FlintStones",//children supplement
                "SlimFast smoothie"
        ));

        put("Wellness and Healthcare", Arrays.asList(
                "Qtips",//cotton swaps grooming
                "Dove",//soap
                "Pantene",
                "Dove fresh",//deodorant
                "Venus",//razors
                "Glide",//floss
                "Crest"//toothpaste
        ));
    }};

    static final String[] orderCategories = {"Food", "Beverages", "Household and Pets", "Beauty and Grooming", "Wellness and Healthcare"};

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${tpd.topic-name}")
    private String topicName;
    @Value("${tpd.base-url}")
    private String baseUrl;
    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @Autowired
    private void setRestTemplate(RestTemplateBuilder builder){
        this.restTemplate = builder.build();
    }

    /**
     * Figures out category and item and sends messages in infinite loop
     */
    private void startEventsSimulatingProducer() {
        // Start producing logon messages.
        new Thread(() -> {
            while (true) {
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                // Select a category.
                String category = orderCategories[rng.nextInt(orderCategories.length)];
                // Select an item.
                String item = orderItems.get(category).get(rng.nextInt(orderItems.get(category).size()));

                String id = String.valueOf(rng.nextInt());
                Notification notification = Notification.builder()
                        .object(category)
                        .entry(Arrays.asList(Entry.builder()
                                .changed_fields(Arrays.asList(item))
                                .id(id)
                                .uid(id)
                                .time(String.valueOf(System.currentTimeMillis()))
                                .build()))
                        .build();

                try {
                    restTemplate.exchange(Paths.get(this.baseUrl, new String[]{"/order"}).toString().replace(":/", "://"), HttpMethod.POST, new HttpEntity(notification, new HttpHeaders()),
                            new ParameterizedTypeReference<String>() {
                            }, new Object[0]);


                    Thread.sleep(500L);
                } catch (InterruptedException e /*| JsonProcessingException e*/) {
                    //ignore
                } // Close try/catch on Thread.sleep.
            } // Close infinite loop.
        }).start();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        startEventsSimulatingProducer();
    }
}
