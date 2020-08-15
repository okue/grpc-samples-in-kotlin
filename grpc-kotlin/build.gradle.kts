plugins {
    id("application")
}

dependencies {
    implementation(project(":protocol"))
    implementation("ch.qos.logback:logback-classic")
}

application {
    mainClassName = "example.kt.GrpcApplicationKt"
}
