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
    applicationDefaultJvmArgs = listOf(
        "-server", "-XX:+UseG1GC",
        "-Xlog:gc*=debug,age*=debug,ergo*=debug,safepoint,class+unload:/tmp/logs/jvm.log:time,uptime,level,tags:filesize=512m,filecount=10",
        "-Xmx2g", "-Xms2g"
    )
}
