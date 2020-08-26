import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm").version(Kotlin.version)
    kotlin("plugin.allopen").version(Kotlin.version)

    // Apply the application plugin to add support for building a CLI application.
    application
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    maven("http://packages.confluent.io/maven")
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(NAV.tokenValidatorKtor)
    implementation(NAV.vaultJdbc)
    implementation(Hikari.cp)
    implementation(Postgresql.postgresql)
    implementation(Logback.classic)
    implementation(Logstash.logbackEncoder)
    implementation(Prometheus.common)
    implementation(Prometheus.hotspot)
    implementation(Prometheus.logback)
    implementation(Prometheus.simpleClient)
    implementation(Prometheus.simpleClientHttpServer)
    implementation(Ktor.auth)
    implementation(Ktor.authJwt)
    implementation(Ktor.htmlBuilder)
    implementation(Ktor.jackson)
    implementation(Ktor.serverNetty)

    implementation(Jackson.dataTypeJsr310)
    implementation(Kafka.Apache.clients)
    implementation(Kafka.Confluent.avroSerializer)
    implementation(Brukernotifikasjon.schemas)

    testImplementation(Junit.api)
    testImplementation(kotlin("test-junit5"))
    testImplementation(NAV.kafkaEmbedded)
    testImplementation(Kafka.Apache.kafka_2_12)
    testImplementation(Kafka.Apache.streams)
    testImplementation(Kafka.Confluent.schemaRegistry)
    testImplementation(H2Database.h2)
    testImplementation(Kluent.kluent)
    testImplementation(Mockk.mockk)
    testRuntimeOnly(Junit.engine)
    testImplementation(Jjwt.api)
    testRuntimeOnly(Jjwt.impl)
    testRuntimeOnly(Jjwt.jackson)
    testRuntimeOnly(Bouncycastle.bcprovJdk15on)
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks {
    withType<Jar> {
        manifest {
            attributes["Main-Class"] = application.mainClassName
        }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    register("runServer", JavaExec::class) {
        environment("OIDC_ISSUER", "http://localhost:9000")
        environment("OIDC_DISCOVERY_URL", "http://localhost:9000/.well-known/openid-configuration")
        environment("OIDC_ACCEPTED_AUDIENCE", "stubOidcClient")
        environment("OIDC_CLAIM_CONTAINING_THE_IDENTITY", "pid")

        environment("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
        environment("KAFKA_SCHEMAREGISTRY_SERVERS", "http://localhost:8081")
        environment("SERVICEUSER_USERNAME", "username")
        environment("SERVICEUSER_PASSWORD", "password")
        environment("GROUP_ID", "dittnav_events")

        environment("DB_HOST", "localhost:5432")
        environment("DB_NAME", "dittnav-event-cache-preprod")
        environment("DB_PASSWORD", "testpassword")
        environment("DB_MOUNT_PATH", "notUsedOnLocalhost")

        main = application.mainClassName
        classpath = sourceSets["main"].runtimeClasspath
    }
}
