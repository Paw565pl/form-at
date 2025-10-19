import com.diffplug.spotless.LineEnding

plugins {
    java
    idea
    id("org.springframework.boot") version "3.5.6"
    id("org.springframework.boot.aot") version "3.5.6" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("se.solrike.sonarlint") version "2.2.0"
    id("com.diffplug.spotless") version "8.0.0"
}

if (gradle.startParameter.taskNames.any { it.contains("bootJar") }) {
    apply(plugin = "org.springframework.boot.aot")
}

group = "format"
version = "0.0.1-SNAPSHOT"
description = "backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

spotless {
    lineEndings = LineEnding.UNIX

    java {
        forbidWildcardImports()
        removeUnusedImports()
        importOrder()
        cleanthat()
        palantirJavaFormat("2.80.0")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
        formatAnnotations()
    }

    val prettierVersion = "3.6.2"

    yaml {
        target("src/**/*.yaml")
        prettier(prettierVersion)
    }

    json {
        target("src/**/*.json")
        prettier(prettierVersion)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val mapstructVersion = "1.6.3"
val springdocVersion = "2.8.13"
val dataFakerVersion = "2.5.2"
val sonarlintVersion = "8.9.3.40165"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("net.datafaker:datafaker:$dataFakerVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    sonarlintPlugins("org.sonarsource.java:sonar-java-plugin:$sonarlintVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
