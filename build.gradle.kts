plugins {
    kotlin("jvm") version "2.4.0"
    `maven-publish`
}

group = "io.github.oofman124"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Add JUnit 5 for unit testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Tells Gradle to use the JUnit Platform for running tests
tasks.test {
    useJUnitPlatform()
}


kotlin {
    jvmToolchain(25)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}