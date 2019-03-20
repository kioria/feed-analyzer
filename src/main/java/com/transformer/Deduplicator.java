package com.transformer;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Stateful discarding of duplicates records from the input stream. Neither key nor value are
 * altered.
 * <p>
 * The store remembers known event IDs in an associated state store for a configured time window.
 * <p>
 * <p>
 * To prevent the store from growing indefinitely, schedules purging job to be invoked periodically
 * with the provided context.
 *
 * @param <K>
 * @param <V>
 * @param <E>
 */
public class Deduplicator<K, V, E> implements Transformer<K, V, KeyValue<K, V>> {
    private final Logger logger = LoggerFactory.getLogger(Deduplicator.class);
    private String stateStore;
    private long purgeInterval;

    private ProcessorContext context;
    /**
     * Key: event ID in the form of the Notification changed field type; Value: timestamp
     * (event-time) of the corresponding event when the event ID was seen for the first time
     */
    private KeyValueStore<E, Long> eventIdStore;

    /**
     * @param idExtractor extracts a unique identifier from a record by which we de-duplicate input
     * records; if it returns null, the record will not be considered for de-duping but forwarded
     * as-is.
     */
    private final KeyValueMapper<K, V, E> idExtractor;

    public Deduplicator(KeyValueMapper<K, V, E> idExtractor, String stateStore, long purgeInterval ) {
        this.idExtractor = idExtractor;
        this.stateStore = stateStore;
        this.purgeInterval = purgeInterval;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        this.eventIdStore = (KeyValueStore<E, Long>) context.getStateStore(stateStore);
        this.context.schedule(Duration.of(purgeInterval, SECONDS), PunctuationType.STREAM_TIME, new Punctuator() {
            @Override
            public void punctuate(long timestamp) {
                purgeExpiredEventIds(timestamp);
            }
        });
    }

    private void purgeExpiredEventIds(final long currentStreamTimeMs) {
        try (final KeyValueIterator<E, Long> iterator = eventIdStore.all()) {
            while (iterator.hasNext()) {
                final KeyValue<E, Long> entry = iterator.next();
                final long eventTimestamp = entry.value;
                if (hasExpired(eventTimestamp, currentStreamTimeMs)) {
                    logger.info("Purging expired record from the state-store " + entry.key);
                    eventIdStore.delete(entry.key);
                }
            }
        }
    }

    private boolean hasExpired(final long eventTimestamp, final long currentStreamTimeMs) {
        return (currentStreamTimeMs - eventTimestamp) > purgeInterval;
    }


    /***
     * stateful record-by-record operation
     * @param key
     * @param value
     * @return
     */
    @Override
    public KeyValue<K, V> transform(K key, V value) {
        final E eventId = idExtractor.apply(key, value);
        if (eventId != null) {
            if (isDuplicate(eventId)) {
                logger.info("Discarding duplicate " + eventId);
                // Discard the record.
                return null;
            } else {
                remember(eventId, context.timestamp());
                // Forward the record downstream as-is.
                logger.info("Forwarding record downstream " + eventId);
                return KeyValue.pair(key, value);
            }
        }
        // Forward the record downstream as-is.
        logger.error("Forwarding record with null id " + eventId);
        return KeyValue.pair(key, value);
    }

    private boolean isDuplicate(final E eventId) {
        return eventIdStore.get(eventId) != null;
    }

    private void remember(final E eventId, final long eventTimestamp) {
        eventIdStore.put(eventId, eventTimestamp);
    }

    @Override
    public void close() {
        // The Kafka Streams API will automatically close stores when necessary.
    }
}
