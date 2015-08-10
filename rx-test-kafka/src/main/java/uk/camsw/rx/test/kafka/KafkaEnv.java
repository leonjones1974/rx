package uk.camsw.rx.test.kafka;

import com.google.common.base.MoreObjects;
import kafka.consumer.ConsumerConfig;
import rx.functions.Action1;
import uk.camsw.rx.common.SystemPropertyOverrideMap;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

public final class KafkaEnv {

    private final SystemPropertyOverrideMap properties;
    private static final String KEY_ZOOKEEPER_SERVERS = "zookeeper.servers";
    private static final String KEY_KAFKA_BROKERS = "kafka.brokers";
    private static final String KEY_SESSION_TIMEOUT = "session.timeout";
    private static final String KEY_CONNECTION_TIMEOUT = "connection.timeout";
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
        }};
    }

    public String zookeeperServers() {
        return properties.get(KEY_ZOOKEEPER_SERVERS);
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

    public kafka.consumer.ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("group.id", UUID.randomUUID().toString());
        props.put("zookeeper.connect", zookeeperServers());
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "1");
        props.put("auto.commit.interval.ms", "1");
        props.put("retry.backoff.ms", "400");
        props.put("rebalance.max.retries", "1000");
        props.put("rebalance.backoff.ms", "10");

        return new ConsumerConfig(props);
    }

    public kafka.consumer.ConsumerConfig createConsumerConfig(Action1<Properties> f) {
        Properties props = new Properties();
        props.put("group.id", UUID.randomUUID().toString());
        props.put("zookeeper.connect", zookeeperServers());
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "1");
        props.put("auto.commit.interval.ms", "1");
        props.put("retry.backoff.ms", "400");
        props.put("rebalance.max.retries", "1000");
        props.put("rebalance.backoff.ms", "10");
        if (f != null) f.call(props);

        return new ConsumerConfig(props);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
        ALL_KEYS.forEach(k -> helper.add(k, properties.get(k)));
        return helper.toString();
    }
}
