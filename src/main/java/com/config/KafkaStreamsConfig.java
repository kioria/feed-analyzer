package com.config;

import com.model.Entry;
import com.model.Notification;
import com.transformer.Deduplicator;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.List;
import java.util.Properties;

@Configuration
public class KafkaStreamsConfig {

    @Value("${tpd.topic-deduplicator}")
    private String topicFrom;
    @Value("${tpd.topic-analyzer}")
    private String topicTo;
    @Value("${tpd.topic-wellness}")
    private String topicWellness;
    @Value("${tpd.state-store}")
    private String stateStore;
    @Value("${tpd.maintain-duration-ms}")
    private long purgeInterval;

    @Bean
    public StoreBuilder storeBuilder() {
        return Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(stateStore),
                Serdes.String(),
                Serdes.Long());
    }

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
    public Transformer deduplicator() {
        return new Deduplicator<>(applyDeduplicatorFunction(), stateStore, purgeInterval);
    }

    @Bean
    public Topology deduplicatorTopology() {
        final StreamsBuilder streamsBuilder = new StreamsBuilder().addStateStore(storeBuilder());
       //KStream instances on the StreamsBuilder have to be declared before the application context is refreshed.
        KStream<String, Notification> stream = streamsBuilder
                .stream(topicFrom, Consumed.with(Serdes.String(), new JsonSerde<>(Notification.class)));

        new KafkaStreamBrancher<String, Notification>()
                .branch((key, value) -> "Wellness and Healthcare".equalsIgnoreCase(value.getObject()), (ks) -> ks.to(topicWellness))
                //de-duplication will be done for all except wellness and healthcare
                .branch((key, value) -> true, (ks) -> ks.transform(() -> deduplicator(), stateStore).to(topicTo))
                .onTopOf(stream);

        return streamsBuilder.build();
    }

    private KeyValueMapper<String, Notification, String> applyDeduplicatorFunction() {
        return (key, value) -> {
            List<Entry> entry = value.getEntry();
            String changedField = entry.stream().map(e -> e.getChanged_fields()).flatMap(List::stream).findFirst().orElse(null);
            return changedField;
        };
    }
}
