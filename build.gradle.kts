plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.cashngun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.cashngun.ApplicationKt")
} 