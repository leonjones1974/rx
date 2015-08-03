package uk.camsw.rx.test.kafka;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class KafkaStream {

    public enum StartFrom {
        EARLIEST,
        LAST_READ
    }

    public static <T> Builder<T> newBuilder(String topic, String groupId, Func1<byte[], Try<T>> deserializer) {
        return new Builder<>(topic, groupId, deserializer);
    }

    public static Builder<String> newBuilder(String topic, String groupId) {
        Func1<byte[], Try<String>> stringDeserializer = bytes -> {
            try {
                return Try.success(new String(bytes, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return Try.<String>failure(e);
            }
        };
        return new Builder(topic, groupId, stringDeserializer);
    }

    public static class Builder<T> {
        private final String topic;
        private final String groupId;
        private final Func1<byte[], Try<T>> deserializer;
        private int consumerCount = 1;
        private StartFrom startFrom = StartFrom.LAST_READ;

        private Builder(String topic, String groupId, Func1<byte[], Try<T>> deserializer) {
            this.topic = topic;
            this.groupId = groupId;
            this.deserializer = deserializer;
        }

        public Builder(Builder<T> other) {
            this.topic = other.topic;
            this.groupId = other.groupId;
            this.deserializer = other.deserializer;
            this.consumerCount = other.consumerCount;
            this.startFrom = other.startFrom;
        }

        /**
         * TODO: LJ - If we are to continue down this road we need to be able to select individual partitions
         */
        public Builder<T> withConsumerCount(int consumerCount) {
            this.consumerCount = consumerCount;
            return this;
        }

        public Builder<T> startingFrom(StartFrom startFrom) {
            this.startFrom = startFrom;
            return this;
        }

        public Observable<Event<Try<T>>> newMergedStream() {
            return KafkaStream.newStream(this).flatMap(s -> s);
        }

        public Observable<Observable<Event<Try<T>>>> newStreams() {
            return KafkaStream.newStream(this);
        }

        public Builder<T> newBuilder() {
            return new Builder(this);
        }
    }

    public static <T> Observable<Observable<Event<Try<T>>>> newStream(Builder<T> builder) {
        return Observable.create(subscriber -> {

            System.out.println("Subscribing to kafka streams");
            System.out.println("\tTopic: " + builder.topic);
            System.out.println("\tGroup: " + builder.groupId);
            System.out.println("\tConsumers: " + builder.consumerCount);
            System.out.println("\tStart: " + builder.startFrom);

            try {
                for (int partitionId = 0; partitionId < builder.consumerCount; partitionId++) {
                    PartitionMetadata leader = findLeader(Arrays.asList(Env.BROKER_HOST), Env.BROKER_PORT, builder.topic, partitionId);
                    SimpleConsumer consumer = new SimpleConsumer(leader.leader().host(), leader.leader().port(), 100000, 64 * 1024, builder.groupId);
                    long latestOffset = getLastOffset(consumer, builder.topic, partitionId, kafka.api.OffsetRequest.LatestTime(), builder.groupId);
                    long earliestOffset = getLastOffset(consumer, builder.topic, partitionId, kafka.api.OffsetRequest.EarliestTime(), builder.groupId);
                    long initialOffset = builder.startFrom == StartFrom.EARLIEST ? earliestOffset : latestOffset;

                    Observable<Event<Try<T>>> eventStream = KafkaStream.newEventStream(consumer, builder, partitionId, initialOffset, latestOffset);
                    subscriber.onNext(eventStream);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    private static <T> Observable<Event<Try<T>>> newEventStream(SimpleConsumer consumer, Builder<T> builder, int partitionId, long initialOffset, long latestOffset) {

        return Observable.<Event<Try<T>>>create(subscriber -> {
            System.out.println("\tPartition: " + partitionId);
            System.out.println("\t\tStarting from: " + initialOffset);
            System.out.println("\t\tLatest: " + latestOffset);

            AtomicLong currentOffset = new AtomicLong(initialOffset);
            Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
            Subscription pollingSubscription = scheduler.createWorker().schedulePeriodically(() -> {
                try {
                    FetchRequest req = new FetchRequestBuilder()
                            .clientId(builder.groupId)
                            .addFetch(builder.topic, partitionId, currentOffset.get(), 100000)
                            .build();
                    FetchResponse fetchResponse = consumer.fetch(req);
                    if (fetchResponse.hasError())
                        subscriber.onError(new RuntimeException("Fetch error: " + fetchResponse.errorCode(builder.topic, partitionId)));
                    for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(builder.topic, partitionId)) {
                        if (currentOffset.get() < initialOffset)
                            continue;        // Not sure why we need this, perhaps block reading?
                        currentOffset.set(messageAndOffset.nextOffset());
                        ByteBuffer payload = messageAndOffset.message().payload();
                        byte[] bytes = new byte[payload.limit()];
                        payload.get(bytes);
                        Try<T> eventValueOrError = builder.deserializer.call(bytes);
                        boolean catchUp = currentOffset.get() < latestOffset;
                        subscriber.onNext(new Event<>(eventValueOrError, catchUp));
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }, -1, -1, TimeUnit.NANOSECONDS);
            subscriber.add(pollingSubscription);
            subscriber.add(Subscriptions.create(() -> {
                System.out.println("Closing consumer of partition: " + partitionId);
                consumer.close();
            }));      // A little naughty to close in here, but don't want to create 2 just to get offsets (don't copy this in real code - rethink)
        }).onBackpressureBuffer();
    }

    private static PartitionMetadata findLeader(List<String> brokers, int port, String topic, int partition) {
        for (String broker : brokers) {
            SimpleConsumer consumer = null;
            try {
                consumer = new SimpleConsumer(broker, port, 100000, 64 * 1024, "leaderLookup");
                List<String> topics = Collections.singletonList(topic);
                TopicMetadataRequest req = new TopicMetadataRequest(topics);
                for (TopicMetadata item : consumer.send(req).topicsMetadata()) {
                    for (PartitionMetadata metadata : item.partitionsMetadata()) {
                        if (metadata.partitionId() == partition && metadata.leader() != null) return metadata;
                    }
                }
            } finally {
                if (consumer != null) consumer.close();
            }
        }
        throw new IllegalStateException("Unable to find leader in " + brokers);
    }

    public static long getLastOffset(SimpleConsumer consumer, String topic, int partition, long whichTime, String clientName) {
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<>();
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
        OffsetRequest request = new OffsetRequest(
                requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);
        OffsetResponse response = consumer.getOffsetsBefore(request);
        if (response.hasError()) {
            throw new RuntimeException("Error: " + response.errorCode(topic, partition));
        }
        return response.offsets(topic, partition)[0];
    }

}
