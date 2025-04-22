plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("maven-publish")
}

group = "pt.pcdev"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                name.set(project.name)
                description.set("A simple REST API library for Kotlin")
                url.set("https://github.com/PedroCavaleiro/kotlin-rest")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                scm {
                    connection.set("scm:git:github.com/PedroCavaleiro/kotlin-rest.git")
                    url.set("https://github.com/PedroCavaleiro/kotlin-rest")
                }
            }
        }
    }

    repositories {
        maven {
            name = "jitpack"
            url = uri("https://jitpack.io")
            credentials {
                username = System.getenv("JITPACK_USERNAME") ?: ""
                password = System.getenv("JITPACK_PASSWORD") ?: ""
            }
        }
    }
}