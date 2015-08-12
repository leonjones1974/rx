package uk.camsw.rx.test.kafka.rule;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;

public class EmbeddedKafkaLauncher {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedKafkaLauncher.class);

    public static Subscription start(InputStream kafkaConfigInput, InputStream zookeeperConfigInput) {
        logger.info("Starting Embedded Kafka/ Zookeeper");
        try {
            Properties zookeeperProperties = new Properties();
            zookeeperProperties.load(zookeeperConfigInput);
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
                    e.printStackTrace();
                    logger.error("Failed to start zookeeper");
                    throw new RuntimeException(e);
                }
            });

            Properties kafkaProperties = new Properties();
            kafkaProperties.load(kafkaConfigInput);
            logger.info("Starting kafka: [{}]", kafkaProperties);
            KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
            KafkaServerStartable kafka = new KafkaServerStartable(kafkaConfig);
            kafka.startup();

            Subscription kafkaSubscription = Subscriptions.create(() -> {
                kafka.shutdown();
                zookeeperSubscription.unsubscribe();
                logger.info("Stopping embedded kafka instance");
            });

            CompositeSubscription subscriptions = new CompositeSubscription();
            subscriptions.add(kafkaSubscription);
            subscriptions.add(zookeeperSubscription);

            Thread shutdown = new Thread(subscriptions::unsubscribe);
            Runtime.getRuntime().addShutdownHook(shutdown);
            logger.info("Started kafka/ zookeeper");
            return subscriptions;
        } catch (IOException | QuorumPeerConfig.ConfigException e) {
            e.printStackTrace();
            logger.error("Failed to start kafka/ zookeeper", e);
            throw new RuntimeException(e);
        }
    }


}
