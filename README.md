# feed-analyzer
Runs Kafka consumer and producer for the sample Spring Boot WEB app. Levarages Kafka Processor API to perform windowed de-duplication of feed.
Models situation when clients do send data directly to our public API. Say, from mobile apps.
Thus handling failures gracefully using retries, message re-delivery, locking, and two-phase commits
is not an option.
