import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm").version(Kotlin.version)
    kotlin("plugin.serialization").version(Kotlin.version)
    kotlin("plugin.allopen").version(Kotlin.version)

    id(Shadow.pluginId) version (Shadow.version)

    // Apply the application plugin to add support for building a CLI application.
    application
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    maven("https://packages.confluent.io/maven")
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    implementation(Brukernotifikasjon.schemas)
    implementation(DittNAV.Common.utils)
    implementation(Hikari.cp)
    implementation(Logback.classic)
    implementation(Logstash.logbackEncoder)
    implementation(Kafka.Apache.clients)
    implementation(Kafka.Confluent.avroSerializer)
    implementation(Ktor.auth)
    implementation(Ktor.authJwt)
    implementation(Ktor.htmlBuilder)
    implementation(Ktor.serialization)
    implementation(Ktor.serverNetty)
    implementation(NAV.vaultJdbc)
    implementation(Postgresql.postgresql)
    implementation(Prometheus.common)
    implementation(Prometheus.hotspot)
    implementation(Prometheus.logback)
    implementation(Prometheus.simpleClient)
    implementation(Prometheus.httpServer)

    implementation("com.github.navikt.tms-ktor-token-support:token-support-authentication-installer:2021.09.15-12.26-2b3dd2a9ca44")
    implementation("com.github.navikt.tms-ktor-token-support:token-support-azure-validation:2021.09.15-12.26-2b3dd2a9ca44")
    implementation("com.github.navikt.tms-ktor-token-support:token-support-tokenx-validation:2021.09.15-12.26-2b3dd2a9ca44")
    /*
    implementation(Tms.KtorTokenSupport.authenticationInstaller)
    implementation(Tms.KtorTokenSupport.azureValidation)
    implementation(Tms.KtorTokenSupport.tokenXValidation)
*/


    testImplementation(H2Database.h2)
    testImplementation(Jjwt.api)
    testImplementation(Junit.api)
    testImplementation(Kafka.Apache.kafka_2_12)
    testImplementation(Kafka.Apache.streams)
    testImplementation(Kafka.Confluent.schemaRegistry)
    testImplementation(Kluent.kluent)
    testImplementation(Mockk.mockk)
    testImplementation(NAV.kafkaEmbedded)

    testRuntimeOnly(Bouncycastle.bcprovJdk15on)
    testRuntimeOnly(Jjwt.impl)
    testRuntimeOnly(Jjwt.orgjson)
    testRuntimeOnly(Junit.engine)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    register("runServer", JavaExec::class) {
        println("Setting default environment variables for running with DittNAV docker-compose")
        DockerComposeDefaults.environomentVariables.forEach { (name, value) ->
            println("Setting the environment variable $name")
            environment(name, value)
        }

        main = application.mainClass.get()
        classpath = sourceSets["main"].runtimeClasspath
    }
}

// TODO: Fjern følgende work around i ny versjon av Shadow-pluginet:
// Skal være løst i denne: https://github.com/johnrengelman/shadow/pull/612
project.setProperty("mainClassName", application.mainClass.get())
apply(plugin = Shadow.pluginId)
