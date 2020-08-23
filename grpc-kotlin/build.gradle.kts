plugins {
    id("application")
}

dependencies {
    implementation(project(":protocol"))
    implementation("ch.qos.logback:logback-classic")

    testImplementation("io.grpc:grpc-core")

    runtimeOnly("io.grpc:grpc-netty-shaded")
}

application {
    mainClassName = "example.kt.GrpcApplicationKt"
    applicationDefaultJvmArgs = listOf(
        "-server", "-XX:+UseG1GC",
        "-Xlog:gc*=debug,age*=debug,ergo*=debug,safepoint,class+unload:/tmp/logs/jvm.log:time,uptime,level,tags:filesize=512m,filecount=10",
        "-Xmx2g", "-Xms2g"
    )
}
