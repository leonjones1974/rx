package uk.camsw.rx.test.kafka;

import com.google.common.base.MoreObjects;
import uk.camsw.rx.common.SystemPropertyOverrideMap;

import java.util.Arrays;
import java.util.List;
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
        System.out.println("toString() = " + toString());
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

    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
        ALL_KEYS.forEach(k -> helper.add(k, properties.get(k)));
        return helper.toString();
    }
}
