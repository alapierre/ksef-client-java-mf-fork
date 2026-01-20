plugins {
    base
    id("org.jreleaser") version "1.20.0"
}

group = "io.alapierre.ksef-sdk"
version = findProperty("version")?.toString() ?: "0.0.0-SNAPSHOT"

jreleaser {
    configFile.set(layout.projectDirectory.file("jreleaser.yml"))
}