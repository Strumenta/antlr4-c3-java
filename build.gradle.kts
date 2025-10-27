plugins {
    `java-library`
    alias(libs.plugins.vanniktech.publish)
    signing
    id("antlr")
    id("com.diffplug.spotless") version "5.1.0"
    id("net.researchgate.release") version "3.0.2"
}

group = "com.strumenta.antlr4-c3"
description = "A code completion core implementation for ANTLR4 based parsers"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}

repositories {
    mavenCentral()
}

val junitVersion = "5.14.0"
val antlrVersion = "4.13.2"

dependencies {
    implementation("org.antlr:antlr4-runtime:$antlrVersion")
    antlr("org.antlr:antlr4:$antlrVersion")
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    named("test") {
        java.srcDir("$buildDir/generated-test-sources/antlr4")
    }
}

tasks.named<AntlrTask>("generateTestGrammarSource") {
    arguments = arguments + listOf("-visitor", "-listener", "-package", "com.strumenta.antlr4c3")
    outputDirectory = file("$buildDir/generated-test-sources/antlr4")
}
tasks.named("compileTestJava").configure {
    dependsOn(tasks.named("generateTestGrammarSource"))
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("antlr4-c3")
        description.set(project.description)
        url.set("https://github.com/Strumenta/antlr4-c3-java")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }

        scm {
            connection.set("scm:git:strumenta/antlr4-c3-java.git")
            developerConnection.set("scm:git:git@github.com:strumenta/antlr4-c3-java.git")
            url.set("https://github.com/strumenta/antlr4-c3-java.git")
            tag.set("HEAD")
        }

        developers {
            developer {
                id.set("nicks")
                name.set("Nick Stephen")
                email.set("nicks _at_ vmware.com")
                organization.set("VMware")
                timezone.set("Europe/Paris")
            }
            developer {
                id.set("tiagobstr")
                name.set("Tiago Baptista")
                email.set("tiago _at_ strumenta.com")
                organization.set("Strumenta")
                timezone.set("Europe/Lisbon")
            }
            developer {
                id.set("ftomassetti")
                name.set("Federico Tomassetti")
                email.set("federico _at_ strumenta.com")
                organization.set("Strumenta")
                timezone.set("Europe/Rome")
            }
        }
    }
}

signing {
    val pub = publishing.publications.findByName("mavenJava")
    if (pub != null && (findProperty("signing.keyId") != null || System.getenv("SIGNING_KEY_ID") != null)) {
        sign(pub)
    }
}

release {
    buildTasks = listOf("publish")
    versionPropertyFile = "./gradle.properties"
    git {
        requireBranch.set("main")
        pushToRemote.set("origin")
    }
}

tasks.named("sourcesJar").configure {
    dependsOn(tasks.named("generateGrammarSource"))
}
//tasks.named("generateMetadataFileForMavenPublication").configure {
//    dependsOn(tasks.named("plainJavadocJar"))
//}