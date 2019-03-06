package com;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

/**
 * processes asynchronous event streams from Kafka has very little additional operational complexity
 * beyond the normal Kafka producer and consumer APIs.
 */
@SpringBootApplication
public class Main {

    private static final String LOGONS_TOPIC = "logons";
    private static final String TICKS_TOPIC = "ticks";
    private static final String NOTIFICATIONS_TOPIC = "notifications";

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

     /*   startFacebookEventsSimulatingProducer();
        startPushNotificatorProducer();

        new KafkaStreams(getStreamTopology(), getStreamsConfig())
                .start();*/
    }

    private static void startPushNotificatorProducer() {
        Properties producerConfig = getProducerConfig();
        KafkaProducer producer = new KafkaProducer<String, String>(producerConfig);
        String message = "Tick";
        // Start producing logon messages.
        new Thread(() -> {
            while (true) {

                for (String user : getSubscribedUsers()) {

                    ProducerRecord<String, String> record =
                            new ProducerRecord<>(TICKS_TOPIC, user, message);

                    producer.send(record);
                } // Close for loop over users (user).

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {

                } // Close try/catch on Thread.sleep.
            } // Close data generating while loop.
        }).start();
    }

    private static void startFacebookEventsSimulatingProducer() {
        String[] users = getSubscribedUsers();
        String[] events = {"LOGON", "LOGOFF"};
        HashMap<String, String> loggedOn = new HashMap<>();
        Properties producerConfig = getProducerConfig();
        KafkaProducer producer = new KafkaProducer<String, String>(producerConfig);
        // Start producing logon messages.
        new Thread(() -> {
            while (true) {
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                // Select a user.
                String user = users[rng.nextInt(users.length)];

                // Select an event.
                String event = events[rng.nextInt(events.length)];

                // Check the state of the user.
                String userState = loggedOn.get(user);

                // Emit the event if it's a new state.
                if ((userState == null) || (!userState.equalsIgnoreCase( event))) {

                    // Update the state.
                    loggedOn.put(user, event);

                    ProducerRecord<String, String> record =
                            new ProducerRecord<>(LOGONS_TOPIC, user, event);

                    producer.send(record);
                } // Close if statement on userState.

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    //ignore
                } // Close try/catch on Thread.sleep.
            } // Close infinite loop.
        }).start();
    }

    private static String[] getSubscribedUsers() {
        return new String[]{"Doyin", "George", "Mark"};
    }

    private static Properties getProducerConfig() {
        Properties producerConfig = new Properties();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        return producerConfig;
    }

    private static KStreamBuilder getStreamTopology() {
        // Build the topology.
        KStreamBuilder builder = new KStreamBuilder();

        KTable<String, String> logons = builder.table(LOGONS_TOPIC);

        KStream<String, String> ticks = builder.stream(TICKS_TOPIC);
        KStream<String, String> notifications =
                ticks.leftJoin(logons, (nv, lv) -> new String[]{nv, lv})
                        // Filter out any nulls.
                        .filter((k, v) -> v[1] != null)
                        // Filter out anyone who's logged on.
                        .filter((k, v) -> !v[1] .equalsIgnoreCase( "LOGON"))
                        // Now set the message.
                        .mapValues(v -> "You are not currently viewing Facebook.");
        // Nuisance delivered. You're welcome.
        notifications.to(NOTIFICATIONS_TOPIC);
        return builder;
    }

    private static Properties getStreamsConfig() {
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "facebook-posts-analyzer");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");

        config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        return config;
    }
}
