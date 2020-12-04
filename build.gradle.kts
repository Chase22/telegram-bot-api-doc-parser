plugins {
    java
    kotlin("jvm") version "1.4.10"
    application
}

group = "io.github.chase22"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
    testCompile("junit", "junit", "4.12")
}


application {
    mainClassName = "io.github.chase22.docparser.ApplicationKt"
}
