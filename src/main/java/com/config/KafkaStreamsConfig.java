package com.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

@Configuration
public class KafkaStreamsConfig {

    @Value("${tpd.topic-deduplicator}")
    private String topicFrom;
    @Value("${tpd.topic-analyzer}")
    private String topicTo;

    @Bean
    public KafkaStreams deduplicatorStream(
            KafkaProperties kafkaProperties,
            @Value("${spring.application.name}") String appName) {
        final Properties props = new Properties();

        props.putAll(kafkaProperties.getProperties());
        // stream config centric ones
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);


        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        return new KafkaStreams(deduplicatorTopology(), props);
    }

    @Bean
    public Topology deduplicatorTopology() {
        final StreamsBuilder streamsBuilder = new StreamsBuilder();
//KStream instances on the StreamsBuilder have to be declared before the application context is refreshed.
        KStream<String, String> stream = streamsBuilder
                .stream(topicFrom);
        stream
                //TODO add de-duplication here
                //TODO add grouping here
                .to(topicTo);

        return streamsBuilder.build();
    }
}
