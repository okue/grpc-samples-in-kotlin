# grpc-kotlin-armeria-spring

```
gradle bootRun
```

### Logging
See
- [logback.xml](src/main/resources/logback.xml)
- [LogbackStringifiers.kt](src/main/kotlin/example/kt/armeria/spring/logging/LogbackStringifiers.kt)
  - https://armeria.dev/docs/advanced-logging

### Error handling
[ErrorHandlingServerInterceptor.kt](src/main/kotlin/example/kt/armeria/spring/ErrorHandlingServerInterceptor.kt)

### Spring Boot Actuator
http://localhost:8081/actuator

### Prometheus metrics
http://localhost:8081/internal/metrics

### [Armeria DocService][docService]
http://localhost:8080/internal/docs/



[docService]: https://armeria.dev/docs/server-docservice
