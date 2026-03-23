import com.diffplug.spotless.LineEnding

plugins {
    java
    idea
    id("org.springframework.boot") version "4.0.4"
    id("org.springframework.boot.aot") version "4.0.4" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("se.solrike.sonarlint") version "2.2.0"
    id("com.diffplug.spotless") version "8.4.0"
}

if (project.hasProperty("aot")) {
    apply(plugin = "org.springframework.boot.aot")
}

group = "format"
version = "0.0.1"
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
        palantirJavaFormat("2.89.0")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
        formatAnnotations()
    }

    val prettierVersion = "3.8.1"

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
val slugifyVersion = "3.0.7"
val minioVersion = "8.6.0"
val linguaVersion = "1.2.2"
val restAssuredVersion = "6.0.0"
val dataFakerVersion = "2.5.4"
val sonarJavaVersion = "8.9.4.40912"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("com.github.slugify:slugify:$slugifyVersion")
    implementation("io.minio:minio:$minioVersion")
    implementation("com.github.pemistahl:lingua:$linguaVersion")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-mongodb")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("net.datafaker:datafaker:$dataFakerVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    sonarlintPlugins("org.sonarsource.java:sonar-java-plugin:$sonarJavaVersion")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(
        listOf(
            "-Xlint:deprecation",
            "-Xlint:dep-ann",
            "-Xlint:removal",
            "-Xlint:overrides",
            "-Xlint:fallthrough",
            "-Xlint:try",
            "-Xlint:finally",
            "-Werror"
        )
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
}
