plugins {
    kotlin("jvm") version "2.0.0"
}

group = "me.adversing"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("mcts-kt"))

    implementation("com.github.bhlangonijr:chesslib:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}