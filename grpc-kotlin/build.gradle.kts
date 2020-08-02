plugins {
    id("application")
}

dependencies {
    implementation(project(":protocol"))
}

application {
    mainClassName = "org.okue.ApplicationKt"
}
