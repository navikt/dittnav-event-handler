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

dependencies {
    implementation(DittNAVCommonLib.utils)
    implementation(Hikari.cp)
    implementation(KotlinLogging.logging)
    implementation(NAV.vaultJdbc)
    implementation(Postgresql.postgresql)
    implementation(Prometheus.common)
    implementation(Prometheus.hotspot)
    implementation(Prometheus.logback)
    implementation(Prometheus.simpleClient)
    implementation(Prometheus.httpServer)
    implementation(Ktor2.Server.core)
    implementation(Ktor2.Server.netty)
    implementation(Ktor2.Server.metricsMicrometer)
    implementation(Ktor2.Server.defaultHeaders)
    implementation(Ktor2.Server.auth)
    implementation(Ktor2.Server.contentNegotiation)
    implementation(Ktor2.Server.statusPages)
    implementation(Ktor2.Client.core)
    implementation(Ktor2.Client.apache)
    implementation(Ktor2.Client.contentNegotiation)
    implementation(TmsKtorTokenSupport.tokenXValidation)
    implementation(TmsKtorTokenSupport.authenticationInstaller)
    implementation(TmsKtorTokenSupport.azureExchange)
    implementation(TmsKtorTokenSupport.azureValidation)
    implementation(Ktor2.Serialization.kotlinX)
    implementation(Ktor2.Server.authJwt)
    implementation(Ktor2.Server.htmlDsl)
    implementation(Micrometer.registryPrometheus)
    implementation(NAV.vaultJdbc)
    implementation(Postgresql.postgresql)

    testImplementation(Jjwt.api)
    testImplementation(Junit.api)
    testImplementation(Junit.params)
    testImplementation(Mockk.mockk)
    testImplementation(TestContainers.postgresql)
    testImplementation(Ktor2.Serialization.jackson)
    testImplementation(Ktor2.Test.clientMock)
    testImplementation(Ktor2.Test.serverTestHost)
    testImplementation(TmsKtorTokenSupport.authenticationInstallerMock)
    testImplementation(TmsKtorTokenSupport.tokenXValidationMock)
    testImplementation(TmsKtorTokenSupport.azureValidationMock)
    testImplementation(Kotest.runnerJunit5)
    testImplementation(Kotest.assertionsCore)

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
}

// TODO: Fjern følgende work around i ny versjon av Shadow-pluginet:
// Skal være løst i denne: https://github.com/johnrengelman/shadow/pull/612
project.setProperty("mainClassName", application.mainClass.get())
apply(plugin = Shadow.pluginId)
