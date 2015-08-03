package uk.camsw.rx.test.kafka;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import org.I0Itec.zkclient.ZkClient;

import java.util.Properties;
import java.util.UUID;

public class TopicBuilder<K, V> {

    private String topicName;
    private final EnvProperties envProperties;
    private final boolean createTopic;
    private final int partitionCount;
    private final int replicationFactor;

    public static <K, V> TopicBuilder<K, V> newBuilder(EnvProperties envProperties) {
        return new TopicBuilder<>(envProperties);
    }

    public Topic<K, V> build() {
        ZkClient client = null;
        try {
            client = new ZkClient("localhost:2181", envProperties.sessionTimeoutMs(), envProperties.connectionTimeoutMs(), ZKStringSerializer$.MODULE$);
            if (createTopic) {
                System.out.println("Creating topic: " + topicName);
                AdminUtils.createTopic(client, topicName, partitionCount, replicationFactor, new Properties());
            }
            return new Topic<>(topicName, client, envProperties);
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (client != null) client.close();
            throw e;
        }
    }

    public TopicBuilder forTopic(String topicName) {
        this.topicName = topicName;
        return this;
    }

    private TopicBuilder(EnvProperties envProperties) {
        this.topicName = "test-" + UUID.randomUUID().toString();
        this.envProperties = envProperties;
        this.createTopic = true;
        this.replicationFactor = 1;
        this.partitionCount = 1;
    }



}
