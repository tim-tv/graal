import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.titovart.graal"
version = "0.1.0"


buildscript {
    
    var kotlinVersion: String by extra
    kotlinVersion = "1.1.51"
    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
    }

    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.0.0.M5")
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.1")
        classpath(kotlinModule("gradle-plugin", kotlinVersion))
    }
}

apply {
    plugin("org.springframework.boot")
    plugin("org.junit.platform.gradle.plugin")
    plugin("kotlin")
}

plugins {
    val kotlinVersion = "1.1.51"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.3.RELEASE"
}


tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
}

repositories {
    mavenCentral()
    maven("http://repo.spring.io/milestone")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:Finchley.M3")
    }
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.cloud:spring-cloud-starter-netflix-zuul")

    compile("com.h2database:h2")
    compile("org.jetbrains.kotlin:kotlin-stdlib")
    compile("org.jetbrains.kotlin:kotlin-reflect")
    testCompile("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testCompile("org.junit.jupiter:junit-jupiter-api")
    testRuntime("org.junit.jupiter:junit-jupiter-engine")
}
