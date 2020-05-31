
group = "dev.mg"
version = "0.0.1"

plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("java")
    application
}

repositories {
    mavenCentral()
    jcenter()
}

val vertxVersion = "3.9.1"
fun vertx(pName:String) = "io.vertx:vertx-$pName:$vertxVersion"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    implementation(vertx("core"))
    implementation(vertx("lang-kotlin"))
    implementation(vertx("lang-kotlin-coroutines"))
    implementation(vertx("shell"))

    testImplementation("junit:junit:4.13")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}
tasks{
    shadowJar {
        manifest {
            attributes["Main-Class"] = "dev.mg.playground.vertx.main.VertxPlaygroundMainKt"
        }
    }
}

application {
    mainClassName = "dev.mg.playground.vertx.main.VertxPlaygroundMainKt"
}