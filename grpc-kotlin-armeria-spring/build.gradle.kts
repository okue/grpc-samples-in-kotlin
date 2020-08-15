plugins {
    id("org.springframework.boot")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":protocol"))

    implementation("com.linecorp.armeria:armeria")
    listOf(
        "grpc",
        "grpc-protocol",
        "brave",
        "spring-boot2-starter",
        "spring-boot2-actuator-starter",
        "logback"
    ).forEach {
        implementation("com.linecorp.armeria:armeria-$it")
    }
    implementation("io.micrometer:micrometer-registry-prometheus")
}
