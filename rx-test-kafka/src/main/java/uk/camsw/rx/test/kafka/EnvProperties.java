package uk.camsw.rx.test.kafka;

public class EnvProperties {

    public String zookeeperServers() {
        return "localhost:2181";
    }

    public int sessionTimeoutMs() {
        return 10000;
    }

    public int connectionTimeoutMs() {
        return 10000;
    }

    // todo: toString
    // todo: allow property override
}
