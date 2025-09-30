plugins {
    base
    id("org.jreleaser") version "1.20.0"
}

jreleaser {
    configFile.set(layout.projectDirectory.file("jreleaser.yml"))
}