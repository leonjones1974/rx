package uk.camsw.rx.test.kafka;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;

public class EmbeddedKafka implements Kafka {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedKafka.class);

    private final Object lock = new Object();

    public Kafka start(InputStream kafkaConfigInput, InputStream zookeeperConfigInput) {
        synchronized (lock) {
            logger.info("Starting Embedded Kafka/ Zookeeper");
            try {
                Properties zookeeperProperties = new Properties();
                zookeeperProperties.load(Class.class.getResourceAsStream("/embedded-kafka/zookeeper.properties"));
                logger.info("Starting zookeeper: [{}]", zookeeperProperties);
                QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
                quorumConfiguration.parseProperties(zookeeperProperties);
                ZooKeeperServerMain zookeeperServerMain = new ZooKeeperServerMain();
                final ServerConfig configuration = new ServerConfig();
                configuration.readFrom(quorumConfiguration);

                Subscription zookeeperSubscription = Schedulers.from(Executors.newSingleThreadExecutor()).createWorker().schedule(() -> {
                    try {
                        zookeeperServerMain.runFromConfig(configuration);
                    } catch (IOException e) {
                        logger.error("Failed to start zookeeper");
                        throw new RuntimeException(e);
                    }
                });

                Properties kafkaProperties = new Properties();
                kafkaProperties.load(Class.class.getResourceAsStream("/embedded-kafka/kafka.properties"));
                logger.info("Starting kafka: [{}]", kafkaProperties);
                KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
                KafkaServerStartable kafka = new KafkaServerStartable(kafkaConfig);
                kafka.startup();

                Thread shutdown = new Thread(() -> {
                    synchronized (lock) {
                        logger.info("Stopping embedded kafka instance");
                        kafka.shutdown();
                        zookeeperSubscription.unsubscribe();
                    }
                });
                Runtime.getRuntime().addShutdownHook(shutdown);

                logger.info("Started kafka/ zookeeper");
            } catch (IOException | QuorumPeerConfig.ConfigException e) {
                logger.error("Failed to start kafka/ zookeeper", e);
                throw new RuntimeException(e);
            }
        }
        return this;
    }


}
