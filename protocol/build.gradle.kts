import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    `java-library`
    kotlin("jvm") apply false
    id("com.google.protobuf") version Versions.PROTOBUF_PLUGIN
}

dependencies {
    api("com.google.protobuf:protobuf-java")
    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-kotlin-stub")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

// See https://github.com/grpc/grpc-kotlin/blob/master/examples/build.gradle.kts
protobuf {
    generatedFilesBaseDir = "$projectDir/src"
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.PROTOBUF}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${Versions.GRPC}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${Versions.GRPC_KT}"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach { task ->
            // To see descriptions at Armeria Doc Service, export descriptors.
            // https://line.github.io/armeria/server-docservice.html#adding-docstrings
            task.generateDescriptorSet = true
            task.descriptorSetOptions.apply {
                includeSourceInfo = true
                includeImports = true
                path = "${project.buildDir}/resources/main/META-INF/armeria/grpc/${project.name}.dsc"
            }

            task.plugins {
                id("grpc") {
                    outputSubDir = "java"
                }
                id("grpckt") {
                    outputSubDir = "java"
                }
            }
            task.dependsOn("clean")
        }
    }
}

val generatedSrcDirs = listOf(
    "${protobuf.protobuf.generatedFilesBaseDir}/main/java"
)

tasks["clean"].doFirst {
    generatedSrcDirs.forEach { delete(it) }
}

idea.module {
    // resourceDirs.add(file) does not work
    resourceDirs = resourceDirs + file("build/resources/main/")
}
