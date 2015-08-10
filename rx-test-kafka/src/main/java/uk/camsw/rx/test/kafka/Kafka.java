package uk.camsw.rx.test.kafka;

import java.io.InputStream;

public interface Kafka {

    Kafka start(InputStream kafkaConfigInput, InputStream zookeeperConfigInput);
}
