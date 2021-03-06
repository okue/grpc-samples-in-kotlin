# grpc-kotlin-armeria-spring

```
gradle bootRun
```

### Logging

See

- [logback.xml](src/main/resources/logback.xml)
- [LogbackStringifiers.kt](src/main/kotlin/example/kt/armeria/spring/logging/LogbackStringifiers.kt)
  - https://armeria.dev/docs/advanced-logging

### Tracing

Just use [Armeria's Zipkin integration](https://armeria.dev/docs/advanced-zipkin).

### Error handling

See

- [error.proto](../protocol/src/main/proto/error.proto)
- [ErrorHandlingServerInterceptor.kt](src/main/kotlin/example/kt/armeria/spring/ErrorHandlingServerInterceptor.kt)
- https://grpc.io/docs/guides/error/#richer-error-model

### Spring Boot Actuator

Open http://localhost:8081/actuator

See [armeria-spring-boot2-actuator-starter](https://github.com/line/armeria/tree/armeria-1.3.0/spring/boot2-actuator-starter).

### Prometheus metrics

Open http://localhost:8081/internal/metrics

See

- [GrpcMeterIdPrefixFunction](https://javadoc.io/doc/com.linecorp.armeria/armeria-javadoc/latest/com/linecorp/armeria/common/grpc/GrpcMeterIdPrefixFunction.html)
- https://armeria.dev/docs/advanced-metrics/

### [Armeria DocService][docService]

Open http://localhost:8080/internal/docs/


[docService]: https://armeria.dev/docs/server-docservice
