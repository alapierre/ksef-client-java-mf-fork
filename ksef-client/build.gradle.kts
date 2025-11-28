plugins {
    `java-library`
    signing
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val bouncycastleVersion = "1.82"
val jsr310Version = "2.17.1"
val junitVersion = "4.4"
val junitEngineVersion = "5.8.2"
val jsxbVarsion = "4.0.6"
val jaxbFluentApiVersion = "3.0"
val xjc by configurations.creating
val xadesVersion = "6.0.1"
val googleZxingCodeVersion = "3.5.3"
val googleZxingJavaseVersion = "3.5.3"
val lombokVersion = "1.18.42"
val commonsLangsVersion = "3.18.0"

dependencies {
    // Validation
    api("eu.europa.ec.joinup.sd-dss:dss-xades:$xadesVersion")
    api("eu.europa.ec.joinup.sd-dss:dss-token:$xadesVersion")
    api("eu.europa.ec.joinup.sd-dss:dss-utils-apache-commons:$xadesVersion")

    api("org.apache.commons:commons-lang3:$commonsLangsVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jsr310Version")

    testImplementation("junit:junit:$junitVersion")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$junitEngineVersion")

    //xsd
    xjc("org.jvnet.jaxb2_commons:jaxb2-fluent-api:$jaxbFluentApiVersion")
    xjc("com.sun.xml.bind:jaxb-xjc:$jsxbVarsion")
    xjc("com.sun.xml.bind:jaxb-impl:$jsxbVarsion")

    //bouncycastle
    implementation("org.bouncycastle:bcpkix-jdk18on:$bouncycastleVersion")
    implementation("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")

    //qr code
    implementation("com.google.zxing:core:$googleZxingCodeVersion")
    implementation("com.google.zxing:javase:$googleZxingJavaseVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("org.slf4j:slf4j-simple:2.0.13")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
    options.encoding = "UTF-8"
}


sourceSets["main"].java.srcDir("${layout.buildDirectory}/generated/src/main/java")


tasks.named("compileJava") {
    dependsOn("generateJaxb")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Automatic-Module-Name" to "io.alapierre.ksef.client"
        )
    }
}

tasks.register("generateJaxb") {
    doLast {
        ant.withGroovyBuilder {
            "taskdef"(
                "name" to "xjc",
                "classname" to "com.sun.tools.xjc.XJCTask",
                "classpath" to configurations["xjc"].asPath
            )
            "xjc"(
                "destdir" to "src/main/java",
                "package" to "pl.akmf.ksef.sdk.client.model.xml",
                "encoding" to "UTF-8"
            ) {
                "arg"("value" to "-Xfluent-api")
                "schema"(
                    "dir" to "src/main/resources/xsd"
                )
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("app")
                description.set("Fork of MF/AK Java SDK for KSeF (Krajowy System e-Faktur)")
                url.set("https://github.com/alapierre/ksef-client-java-mf-fork")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        name.set("Adrian Lapierre")
                        email.set("al@alapierre.io")
                    }
                    developer {
                        name.set("Karol Bryzgiel")
                        email.set("karol.bryzgiel@itrust.dev")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/alapierre/ksef-client-java-mf-fork.git")
                    developerConnection.set("scm:git:ssh://github.com:alapierre/ksef-client-java-mf-fork.git")
                    url.set("https://github.com/alapierre/ksef-client-java-mf-fork")
                }
            }
            repositories {
                maven {
                    name = "staging"
                    url = uri(rootProject.layout.buildDirectory.dir("staging-deploy"))
                }
            }
        }
    }

}

signing {

    val keyId = System.getenv("SIGNING_KEY_ID") ?: findProperty("signing.keyId")?.toString()
    val password = System.getenv("SIGNING_PASSWORD") ?: findProperty("signing.password")?.toString()

    if (keyId != null && password != null) {
        extra["signing.keyId"] = keyId
        extra["signing.password"] = password
    }


    useGpgCmd()
    sign(publishing.publications["maven"])
}

