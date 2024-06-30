plugins {
    kotlin("jvm") version "1.9.23"
}

group = "world.anhgelus.architectsland.bedwars"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

tasks.withType<ProcessResources> {
    val props = mapOf("version" to version)
    filesMatching("plugin.yml") {
        expand(props)
    }
}