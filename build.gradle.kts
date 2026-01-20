plugins {
    base
    id("org.jreleaser") version "1.20.0"
}

group = "io.alapierre.ksef-sdk"
version = "2.3.2"

jreleaser {
    configFile.set(layout.projectDirectory.file("jreleaser.yml"))
}