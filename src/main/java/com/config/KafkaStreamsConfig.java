package com.config;

import com.model.Notification;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Properties;

@Configuration
public class KafkaStreamsConfig {

    @Value("${tpd.topic-deduplicator}")
    private String topicFrom;
    @Value("${tpd.topic-analyzer}")
    private String topicTo;
    @Value("${tpd.topic-wellness}")
    private String topicWellness;

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
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);

        return new KafkaStreams(deduplicatorTopology(), props);
    }

    @Bean
    public Topology deduplicatorTopology() {
        final StreamsBuilder streamsBuilder = new StreamsBuilder();
//KStream instances on the StreamsBuilder have to be declared before the application context is refreshed.
        KStream<String, Notification> stream = streamsBuilder
                .stream(topicFrom, Consumed.with(Serdes.String(), new JsonSerde<>(Notification.class)));
        //TODO add de-duplication here
        //TODO add grouping here
        new KafkaStreamBrancher<String, Notification>()
                .branch((key, value) -> "Wellness and Healthcare".equalsIgnoreCase(value.getObject()), (ks) -> ks.to(topicWellness))
                //deduplication will be done for all except wellness and healthcare
                .branch((key, value) -> true, (ks) -> ks.to(topicTo))
                .onTopOf(stream);

        return streamsBuilder.build();
    }
}
