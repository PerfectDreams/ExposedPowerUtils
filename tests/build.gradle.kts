import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(project(":postgres-java-time"))
    testImplementation(project(":postgres-power-utils"))
    testImplementation("org.jetbrains.exposed:exposed-core:0.37.3")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:0.37.3")
    testImplementation("org.postgresql:postgresql:42.3.3")
    testImplementation("ch.qos.logback:logback-classic:1.3.0-alpha14")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.testcontainers:testcontainers:1.16.3")
    testImplementation("org.testcontainers:junit-jupiter:1.16.3")
    testImplementation("org.testcontainers:postgresql:1.16.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}