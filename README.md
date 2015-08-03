# rx-test

## Overview

Rx is great, but testing your awesome sequences can be pain

rx-test aims to provide a simple DSL and associated capture libraries to make creation and debugging of tests simpler


## Examples


### A simple stream

```
  Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                  .subjectCreated(source -> source.map(s -> Integer.parseInt(s) + 1))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("1")
                    .theSource().emits("2")
                    .theSource().completes()
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(2)
                        .event(0).isEqualTo(2)
                        .event(1).isEqualTo(3);

```


### Temporal operations
```
   Scenario1<String, List<String>> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                    .subjectCreatedWithScheduler((source, scheduler) -> source.buffer(10, TimeUnit.SECONDS, scheduler))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("1a")
                    .theSource().emits("1b")
                    .theSource().emits("1c")
                    .time().advancesBy(Duration.ofSeconds(11))
                    .theSource().emits("2a")
                    .theSource().emits("2b")
                    .theSource().completes()
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(2)
                        .event(0).isEqualTo(asList("1a", "1b", "1c"))
                        .event(1).isEqualTo(asList("2a", "2b"));
```

### Multi source, multi-typed streams
```
      Scenario2<String, Integer, String> testScenario = TestScenario.twoSources();

        testScenario
                .given()
                    .subjectCreated((s1, s2) -> s1.zipWith(s2, (z, n) -> z + n))
                .when()
                    .subscriber("s1").subscribes()
                    .source1().emits("a")
                    .source2().emits(1)
                    .source1().emits("b")
                    .source2().emits(2)
                    .source1().completes()
                    .source2().completes()
                .then()
                    .subscriber("s1")
                        .eventCount().isEqualTo(2)
                        .event(0).isEqualTo("a1")
                        .event(1).isEqualTo("b2");
```

### Asserting on errors
```
   
      Scenario1<String, Integer> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                    .subjectCreated(source -> source.map(s -> Integer.parseInt(s) + 1))
                    .errorsAreHandled()
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("1")
                    .theSource().errors(new IllegalArgumentException("oh no"))
                .then()
                    .subscriber("s1")
                        .isErrored().isTrue()
                        .errorClass().isEqualTo(IllegalArgumentException.class)
                        .errorMessage().isEqualTo("oh no");
    }

```

### Asserting using a rendering
```
        testScenario
                .given()
                    .subjectCreated(source -> source.map(n -> n == 0 ? "a" : "B"))
                    .renderer(event -> "'" + event + "'")
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits(0)
                    .theSource().emits(1)
                    .theSource().completes()
                .then()
                    .subscriber("s1")
                        .renderedStream().isEqualTo("['a']-['B']-|")
                        .completedCount().isEqualTo(1)
                        .eventCount().isEqualTo(2);


```

### Providing your own publisher 
```
        PublishSubject<String> customSource = PublishSubject.create();
        TestScenario.singleSource(customSource)
                .given()
                    .subjectCreated(_source -> customSource.map(String::toUpperCase))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("a")
                    .theSource().emits("b")
                .then()
                    .subscriber("s1")
                    .eventCount().isEqualTo(2)
                    .event(0).isEqualTo("A")
                    .event(1).isEqualTo("B");

```

### Blocking for async events 
```
        Scenario1<String, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                    .subjectCreated(source -> source.observeOn(Schedulers.computation()).delay(1, TimeUnit.SECONDS))
                    .asyncTimeout(Duration.ofSeconds(2))
                .when()
                    .subscriber("s1").subscribes()
                    .theSource().emits("a")
                    .theSource().emits("b")
                    .subscriber("s1").waitsforEvents(2)
                .then()
                    .subscriber("s1")
                        .renderedStream().isEqualTo("[a]-[b]")
                        .eventCount().isEqualTo(2);
```


### External resources
```
        AtomicBoolean closed = new AtomicBoolean(false);
        AutoCloseable resource = () -> closed.getAndSet(true);
        Scenario1<String, String> testScenario = TestScenario.singleSource();

        testScenario
                .given()
                    .theResource(() -> resource)
                    .subjectCreated(source -> Observable.just("a", "b"))
                .when()
                    .subscriber("s1").subscribes()
                .then()
                   .subscriber("s1").eventCount().isEqualTo(2);

        assertThat(closed.get()).isTrue();

```