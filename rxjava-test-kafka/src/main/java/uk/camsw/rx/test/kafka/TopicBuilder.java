package uk.camsw.rx.test.kafka;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

public class TopicBuilder<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(TopicBuilder.class);

    private final KafkaEnv envProperties;
    private final boolean createTopic;
    private int partitionCount;
    private final int replicationFactor;
    private String topicName;

    public static <K, V> TopicBuilder<K, V> newBuilder(KafkaEnv envProperties) {
        return new TopicBuilder<>(envProperties);
    }

    public Topic<K, V> build() {
        ZkClient client = null;
        try {
            client = new ZkClient(envProperties.zookeeperServers(), envProperties.sessionTimeoutMs(), envProperties.connectionTimeoutMs(), ZKStringSerializer$.MODULE$);
            if (createTopic) {
                logger.info("Creating topic: [{}]", topicName);
                AdminUtils.createTopic(client, topicName, partitionCount, replicationFactor, new Properties());
            }
            return new Topic<>(topicName, client, envProperties);
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (client != null) client.close();
            throw e;
        }
    }

    public TopicBuilder<K, V> forTopic(String topicName) {
        this.topicName = topicName;
        return this;
    }

    private TopicBuilder(KafkaEnv envProperties) {
        this.topicName = "test-" + UUID.randomUUID().toString();
        this.envProperties = envProperties;
        this.createTopic = true;
        this.replicationFactor = 1;
        this.partitionCount = 1;
    }

    public TopicBuilder withPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
        return this;
    }
}
