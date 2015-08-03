package uk.camsw.rx.test.kafka;

public class Env {

    public static final String TEST_TOPIC = "reactive.workflow.test";
    public static final String ZOOKEEPER_HOST = "localhost";
    public static final int ZOOKEEPER_PORT = 2181;
    public static final String ZOOKEEPER = ZOOKEEPER_HOST + ":" + ZOOKEEPER_PORT;
    public static final String BROKER_HOST = "localhost";
    public static final int BROKER_PORT = 9092;
    public static final String BROKERS = BROKER_HOST + ":" + BROKER_PORT;


    public static String EVENT_TOPIC = "event.stream";
    public static String WORKFLOW_ACTIONS_TOPIC = "workflow.actions.stream";
}
