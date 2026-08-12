import com.diffplug.spotless.LineEnding
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "8.9.0"
    id("name.remal.sonarlint") version "7.1.6"
    id("net.ltgt.errorprone") version "5.1.0"
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

spotless {
    encoding = Charsets.UTF_8
    lineEndings = LineEnding.UNIX

    java {
        importOrder()
        removeUnusedImports()
        forbidWildcardImports()
        cleanthat()
        palantirJavaFormat("2.97.0").formatJavadoc(true)
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
        formatAnnotations()
    }

    kotlinGradle { ktfmt().kotlinlangStyle() }

    val prettierVersion = "3.9.6"

    yaml {
        target("**/*.yaml")
        prettier(prettierVersion).npmInstallCache()
    }

    json {
        target("**/*.json")
        prettier(prettierVersion).npmInstallCache()
    }
}

sonarLint {
    logging {
        withDescription = false
    }
}

repositories {
    mavenCentral()
}

val springModulithVersion = "2.1.0"
val caffeineVersion = "3.2.4"
val otelLogbackAppenderVersion = "2.28.1-alpha"
val mapstructVersion = "1.7.0.Beta2"
val slugifyVersion = "4.0.1"
val minioSdkVersion = "9.0.3"
val restassuredVersion = "6.0.1"
val errorProneCoreVersion = "2.50.0"
val nullawayVersion = "0.13.8"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation(
        "io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:$otelLogbackAppenderVersion"
    )
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-mongodb")
    implementation("org.springframework.modulith:spring-modulith-observability-api")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("com.github.slugify:slugify:$slugifyVersion")
    implementation("io.minio:minio:$minioSdkVersion")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.springframework.modulith:spring-modulith-actuator")
    runtimeOnly("org.springframework.modulith:spring-modulith-observability-core")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation(
        "org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test"
    )
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-test")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-opentelemetry-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-mongodb")
    testImplementation("io.rest-assured:rest-assured:$restassuredVersion")
    testCompileOnly("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor("org.projectlombok:lombok")
    errorprone("com.google.errorprone:error_prone_core:$errorProneCoreVersion")
    errorprone("com.uber.nullaway:nullaway:$nullawayVersion")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:$springModulithVersion")
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(
        listOf("-Xlint:all,-processing,-serial,-classfile,-rawtypes,-unchecked", "-Werror")
    )

    options.errorprone {
        allErrorsAsWarnings = true
        disableWarningsInGeneratedCode = true
        excludedPaths = ".*/build/generated/.*"
        check("NullAway", CheckSeverity.ERROR)
        option("NullAway:AnnotatedPackages", "format.backend")
    }

    if (name.contains("test", ignoreCase = true)) {
        options.errorprone {
            disable("NullAway")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
