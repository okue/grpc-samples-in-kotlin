plugins {
    idea
    java
    kotlin("jvm") version Versions.KOTLIN
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

allprojects {
    group = "org.okue"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("idea")
        plugin("java")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.jvm")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            javaParameters = true
            allWarningsAsErrors = true
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
        }
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencyManagement {
        imports {
            mavenBom("com.linecorp.armeria:armeria-bom:${Versions.ARMERIA}")
            mavenBom("com.google.protobuf:protobuf-bom:${Versions.PROTOBUF}")
            mavenBom("io.grpc:grpc-bom:${Versions.GRPC}")
            mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${Versions.COROUTINE}")
        }

        dependencies {
            // https://github.com/MicroUtils/kotlin-logging
            dependency("io.github.microutils:kotlin-logging:1.7.10")
            dependency("io.grpc:grpc-kotlin-stub:${Versions.GRPC_KT}")
        }
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("io.github.microutils:kotlin-logging")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    }
}
