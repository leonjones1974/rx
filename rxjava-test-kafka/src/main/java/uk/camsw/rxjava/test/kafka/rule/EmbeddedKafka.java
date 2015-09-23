package uk.camsw.rxjava.test.kafka.rule;

import org.junit.rules.ExternalResource;
import rx.Subscription;
import uk.camsw.rxjava.test.kafka.KafkaEnv;

import java.io.InputStream;

public class EmbeddedKafka extends ExternalResource {

    private final InputStream kafkaConfigInput;
    private final InputStream zookeeperConfigInput;
    private Subscription subscription;

    public EmbeddedKafka(KafkaEnv env) {
        this(env.kafkaServerProperties(), env.zookeeperServerProperties());
    }

    public EmbeddedKafka(String kafkaConfigResource, String zookeeperConfigInput) {
        this(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(kafkaConfigResource),
                Thread.currentThread().getContextClassLoader().getResourceAsStream(zookeeperConfigInput));
    }

    public EmbeddedKafka(InputStream kafkaConfigInput, InputStream zookeeperConfigInput) {
        this.kafkaConfigInput = kafkaConfigInput;
        this.zookeeperConfigInput = zookeeperConfigInput;
    }

    public EmbeddedKafka() {
        this(new KafkaEnv());
    }

    @Override
    protected void before() throws Throwable {
        subscription = EmbeddedKafkaLauncher.start(kafkaConfigInput, zookeeperConfigInput);
    }

    @Override
    protected void after() {
        if (subscription != null) subscription.unsubscribe();
    }


}
