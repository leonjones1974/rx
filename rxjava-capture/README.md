# rx capture

Instruments RX to allow capture of event flow

```
test {
    def agentPath = project.configurations.testCompile.find { it.name.startsWith("capture")}
    if (agentPath == null) throw new RuntimeException("Unable to locate java agent")
    println("Using agent path: $agentPath")
    jvmArgs "-javaagent:$agentPath"
}
```

```
    testCompile 'uk.camsw.rx:capture:1.0-SNAPSHOT';
    testCompile files("${System.properties['java.home']}/../lib/tools.jar")
    testRuntime 'javassist:javassist:3.12.1.GA'

```

```
    -ea -javaagent:/home/leonjones/workspace/cam/rx/capture/build/libs/capture-1.0-SNAPSHOT.jar
```
