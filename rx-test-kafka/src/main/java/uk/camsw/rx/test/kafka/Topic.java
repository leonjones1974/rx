package uk.camsw.rx.test.kafka;

import kafka.admin.AdminUtils;
import org.I0Itec.zkclient.ZkClient;

public class Topic implements AutoCloseable {

    private ZkClient client;
    private final String name;
    private final Object lock = new Object();

    public Topic(String name, ZkClient client) {
        this.client = client;
        this.name = name;
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
        }
    }
}
