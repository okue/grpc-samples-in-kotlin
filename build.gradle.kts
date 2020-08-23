plugins {
    idea
    java
    `project-report`
    kotlin("jvm") version Versions.KOTLIN apply false
    kotlin("plugin.spring") version Versions.KOTLIN apply false
    id("org.springframework.boot") version Versions.SPRING_BOOT apply false
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("net.rdrei.android.buildtimetracker") version "0.11.0"
}

allprojects {
    group = "example.kt"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    // Override spring-boot-dependencies bom properties
    // https://docs.spring.io/dependency-management-plugin/docs/current/reference/html/#dependency-management-configuration-bom-import-override-property
    ext["kotlin.version"] = Versions.KOTLIN
    ext["kotlin-coroutines.version"] = Versions.COROUTINE
}

subprojects {
    apply {
        plugin("idea")
        plugin("java")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.gradle.project-report")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            javaParameters = true
            allWarningsAsErrors = true
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
        }
    }
    tasks.withType<Test> {
        systemProperty("spring.profiles.active", "test")
        useJUnitPlatform {
            includeEngines("junit-jupiter")
        }
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            mavenBom("com.linecorp.armeria:armeria-bom:${Versions.ARMERIA}")
            mavenBom("com.google.protobuf:protobuf-bom:${Versions.PROTOBUF}")
            mavenBom("io.grpc:grpc-bom:${Versions.GRPC}")
        }

        dependencies {
            // https://github.com/MicroUtils/kotlin-logging
            dependency("io.github.microutils:kotlin-logging:1.8.3")
            dependency("io.grpc:grpc-kotlin-stub:${Versions.GRPC_KT}")
        }
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("io.github.microutils:kotlin-logging")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        implementation("org.springframework.boot:spring-boot-starter-test")
    }
    configurations.all {
        exclude(module = "junit")
        exclude(module = "junit-vintage-engine")
    }
}

buildtimetracker {
    reporters {
        register("summary") {
            options["shortenTaskNames"] = "false"
        }
    }
}
