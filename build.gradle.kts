import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
}

val exposedPowerUtils = "1.4.0"

allprojects {
    group = "net.perfectdreams.exposedpowerutils"
    version = exposedPowerUtils

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

subprojects {
    if (project.name != "tests") {
        apply<MavenPublishPlugin>()
        version = exposedPowerUtils

        publishing {
            repositories {
                maven {
                    name = "PerfectDreams"
                    url = uri("https://repo.perfectdreams.net/")
                    credentials(PasswordCredentials::class)
                }
            }
        }
    }
}