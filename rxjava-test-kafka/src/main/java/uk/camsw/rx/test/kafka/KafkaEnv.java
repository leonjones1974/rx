package uk.camsw.rx.test.kafka;

import com.google.common.base.MoreObjects;
import kafka.consumer.ConsumerConfig;
import kafka.producer.ProducerConfig;
import kafka.serializer.Encoder;
import kafka.serializer.StringEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class KafkaEnv {

    private final SystemPropertyOverrideMap properties;
    private static final String KEY_ZOOKEEPER_SERVERS = "zookeeper.servers";
    private static final String KEY_KAFKA_BROKERS = "kafka.brokers";
    private static final String KEY_SESSION_TIMEOUT = "session.timeout";
    private static final String KEY_CONNECTION_TIMEOUT = "connection.timeout";
    private static final String KEY_KAFKA_SERVER_PROPERTIES = "kafka.server.properties";
    private static final String KEY_ZOOKEEPER_SERVER_PROPERTIES = "zookeeper.server.properties";
    private static final List<String> ALL_KEYS;

    static {
        ALL_KEYS = Arrays.asList(KafkaEnv.class.getDeclaredFields())
                .stream()
                .filter(f -> f.getName().startsWith("KEY_"))
                .<String>map(f -> {
                    try {
                        return (String) f.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    public KafkaEnv() {
        properties = new SystemPropertyOverrideMap() {{
            put(KEY_ZOOKEEPER_SERVERS, "localhost:2181");
            put(KEY_KAFKA_BROKERS, "localhost:9092");
            put(KEY_SESSION_TIMEOUT, "10000");
            put(KEY_CONNECTION_TIMEOUT, "10000");
            put(KEY_KAFKA_SERVER_PROPERTIES, "embedded-kafka/kafka.properties");
            put(KEY_ZOOKEEPER_SERVER_PROPERTIES, "embedded-kafka/zookeeper.properties");
        }};
    }

    public String zookeeperServers() {
        return properties.get(KEY_ZOOKEEPER_SERVERS);
    }
    public String kafkaServerProperties() {
        return properties.get(KEY_KAFKA_SERVER_PROPERTIES);
    }
    public String zookeeperServerProperties() {
        return properties.get(KEY_ZOOKEEPER_SERVER_PROPERTIES);
    }

    public String kafkaBrokers() {
        return properties.get(KEY_KAFKA_BROKERS);
    }

    public int connectionTimeoutMs() {
        return Integer.valueOf(properties.get(KEY_CONNECTION_TIMEOUT));
    }

    public int sessionTimeoutMs() {
        return Integer.valueOf(properties.get(KEY_SESSION_TIMEOUT));
    }

    public ConsumerConfig createConsumerConfig() {
       return createConsumerConfig(null);
    }

    public ConsumerConfig createConsumerConfig(Consumer<Properties> f) {
        Properties props = defaultConsumerProperties();
        if (f != null) f.accept(props);
        return new ConsumerConfig(props);
    }

    public ProducerConfig createProducerConfig(Class<? extends Encoder> encoder) {
        return createProducerConfig(encoder, null);
    }

    public ProducerConfig createProducerConfig(Class<? extends Encoder> encoder, Consumer<Properties> f) {
        Properties properties = defaultProducerProperties(encoder);
        if (f != null) f.accept(properties);
        return new ProducerConfig(properties);
    }


    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
        ALL_KEYS.forEach(k -> helper.add(k, properties.get(k)));
        return helper.toString();
    }

    private Properties defaultConsumerProperties() {
        Properties props = new Properties();
        props.put("group.id", UUID.randomUUID().toString());
        props.put("zookeeper.connect", zookeeperServers());
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "1");
        props.put("auto.commit.interval.ms", "1");
        props.put("retry.backoff.ms", "400");
        props.put("rebalance.max.retries", "1000");
        props.put("rebalance.backoff.ms", "10");
        return props;
    }

    private Properties defaultProducerProperties(Class<? extends Encoder> encoder) {
        Properties properties = new Properties();
        properties.put("metadata.broker.list", kafkaBrokers());
        properties.put("serializer.class", encoder.getName());
        properties.put("key.serializer.class", StringEncoder.class.getName());
        return properties;
    }
}
