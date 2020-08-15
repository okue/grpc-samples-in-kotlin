plugins {
    id("application")
}

dependencies {
    implementation(project(":protocol"))

    implementation("com.linecorp.armeria:armeria")
    listOf("grpc", "grpc-protocol", "logback").forEach {
        implementation("com.linecorp.armeria:armeria-$it")
    }
}

application {
    mainClassName = "example.kt.armeria.ArmeriaGrpcApplicationKt"
}
