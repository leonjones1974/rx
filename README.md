# rx-test

## Overview

Rx is great, but testing your awesome sequences can be pain

rx-test aims to provide a simple, extensible DSL to enable easier, more declarative rx testing


## A taster (see the tests for more examples)
```

   DualSourceScenario<String, Integer, String> testScenario = TestScenario.dualSources();

    testScenario
            .given()
                .theStreamUnderTest((s1, s2) -> s1.zipWith(s2, (z, n) -> z + n))
                .renderer(s -> s)
            .when()
                .theSubscriber().subscribes()
                .source1().emits("a")
                .source2().emits(1)
                .source1().emits("b")
                .source2().emits(2)
                .source1().completes()
                .source2().completes()
            .then()
                .theSubscribers()
                    .eventCount().isEqualTo(2)
                    .renderedStream().isEqualTo("[a1]-[b2]-|");
```


# rx-test-kafka

## Overview 

An extension to rx-test that allows for the incorporation of kafka publisher/ consumers within the tests

## A taster (see the tests for more examples)

```

    String group = UUID.randomUUID().toString();

    new KafkaSourceScenario<String, String, String>()
            .given()
                .aNewTopic()        // Create a randomly named topic on the fly
                .asyncTimeoutOf(Duration.ofSeconds(10))
                .theStreamUnderTest(topic -> KafkaStream.newBuilder(topic.getName(), group).newMergedStream().map(e -> e.getValue()))
            .when()
                .theSubscriber().subscribes()
                .thePublisher().publishes("1", "1")
                .thePublisher().publishes("2", "2")
                .theSubscriber().waitsForEvents(2)
            .then()
                .theSubscribers().renderedStream().isEqualTo("[1]-[2]");
```