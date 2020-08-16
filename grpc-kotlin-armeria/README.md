# grpc-kotlin-armeria

```
../gradlew run
../gradlew run --args="co"
```

If `--args="co"` is specified, [GreeterImplUsingKtDispatcher](./src/main/kotlin/example/kt/armeria/GreeterImplUsingKtDispatcher.kt) is
used as an implementation of the Greeter service.
Otherwise, [GreeterImplUsingEventLoop](./src/main/kotlin/example/kt/armeria/GreeterImplUsingEventLoop.kt) is used.
