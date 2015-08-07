package uk.camsw.rx.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class HighLevelKafkaStream {

    public static Observable<MessageAndMetadata<byte[], byte[]>> create(String topic, ConsumerConfig config) {
        return Observable.<MessageAndMetadata<byte[], byte[]>>create(subscriber -> {
            System.out.println("SUBSCRIBING: " + topic);
            ConsumerConnector connector = Consumer.createJavaConsumerConnector(config);
            Map<String, Integer> topicCountMap = new HashMap<>();
            topicCountMap.put(topic, 1);
            Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = connector.createMessageStreams(topicCountMap);
            List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
            System.out.println("Got streams: " + streams.size());
            streams.forEach(stream -> {
                Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
                ConsumerIterator<byte[], byte[]> iter = stream.iterator();
                Subscription eventReaderSubscription = scheduler.createWorker().schedule(() -> {
                    while (iter.hasNext()) {
                        MessageAndMetadata<byte[], byte[]> mamd = iter.next();
                        subscriber.onNext(mamd);
                    }
                });
                subscriber.add(eventReaderSubscription);
            });

            // TODO: Sort out the race condition between consuming and the first publish
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.add(Subscriptions.create(connector::shutdown));
        }).doOnUnsubscribe(() -> System.out.println("Unsubscribed")).publish().refCount();

    }

}
