package uk.camsw.rx.test.kafka;

import kafka.admin.AdminUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.I0Itec.zkclient.ZkClient;

import java.util.Properties;

public class Topic<K, V> implements AutoCloseable {

    private final String name;
    private final Object lock = new Object();
    private Producer<K, V> producer;
    private ZkClient client;
    private EnvProperties envProperties;

    public Topic(String name, ZkClient client, EnvProperties envProperties) {
        this.client = client;
        this.name = name;
        this.envProperties = envProperties;
        this.producer = createProducer();
    }

    public void publish(K key, V message) {
        if (producer == null) throw new IllegalStateException("Producer has been closed");
        producer.send(new KeyedMessage<>(name, key, message));
    }

    @Override
    public void close() throws Exception {
        synchronized (lock) {
            if (client != null) {
                try {
                    System.out.println("Deleting topic: " + name);
                    AdminUtils.deleteTopic(client, name);
                } finally {
                    client.close();
                    client = null;
                }
            }

            if (producer != null) {
                try {
                    System.out.println("Closing producer: " + name);
                    producer.close();
                } finally {
                    producer = null;
                }
            }
        }
    }

    private Producer<K, V> createProducer() {
        Properties props = new Properties();
        props.put("metadata.broker.list", envProperties.kafkaBrokers());
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        ProducerConfig config = new ProducerConfig(props);
        return new kafka.javaapi.producer.Producer<>(config);
    }

}