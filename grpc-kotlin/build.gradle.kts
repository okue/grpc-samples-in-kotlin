plugins {
    id("application")
}

dependencies {
    implementation(project(":protocol"))
    implementation("ch.qos.logback:logback-classic")

    runtimeOnly("io.grpc:grpc-netty-shaded:${Versions.GRPC}")
}

application {
    mainClassName = "example.kt.GrpcApplicationKt"
}
