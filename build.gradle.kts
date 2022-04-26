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
    kotlinOptions.jvmTarget = "17"
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    mavenCentral()
    maven("https://packages.confluent.io/maven")
    mavenLocal()
    maven("https://jitpack.io")
}

sourceSets {
    create("intTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }
}

val intTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
configurations["intTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    implementation("com.github.navikt:brukernotifikasjon-schemas:v2.5.0")
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
    implementation(Tms.KtorTokenSupport.authenticationInstaller)
    implementation(Tms.KtorTokenSupport.azureValidation)
    implementation(Tms.KtorTokenSupport.tokenXValidation)

    testImplementation(Jjwt.api)
    testImplementation(Junit.api)
    testImplementation(Kafka.Apache.kafka_2_12)
    testImplementation(Kafka.Apache.streams)
    testImplementation(Kafka.Confluent.schemaRegistry)
    testImplementation(Kluent.kluent)
    testImplementation(Mockk.mockk)
    testImplementation(NAV.kafkaEmbedded)
    testImplementation(TestContainers.postgresql)
    testImplementation(Ktor.serverTestHost)
    testImplementation(Tms.KtorTokenSupport.authenticationInstallerMock)
    testImplementation(Tms.KtorTokenSupport.tokenXValidationMock)

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.2.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.2.2")
    testImplementation("io.kotest:kotest-property-jvm:5.2.2")

    testRuntimeOnly(Bouncycastle.bcprovJdk15on)
    testRuntimeOnly(Jjwt.impl)
    testRuntimeOnly(Jjwt.orgjson)
    testRuntimeOnly(Junit.engine)
}

application {
    mainClass.set("no.nav.personbruker.dittnav.eventhandler.config.ApplicationKt")
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

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    shouldRunAfter("test")
}

tasks.check { dependsOn(integrationTest) }

// TODO: Fjern følgende work around i ny versjon av Shadow-pluginet:
// Skal være løst i denne: https://github.com/johnrengelman/shadow/pull/612
project.setProperty("mainClassName", application.mainClass.get())
apply(plugin = Shadow.pluginId)
