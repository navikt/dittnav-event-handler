package no.nav.personbruker.dittnav.eventhandler.config

import no.nav.personbruker.dittnav.common.util.config.BooleanEnvVar.getEnvVarAsBoolean
import no.nav.personbruker.dittnav.common.util.config.IntEnvVar.getEnvVarAsInt
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar
import no.nav.personbruker.dittnav.eventhandler.config.ConfigUtil.isCurrentlyRunningOnNais

data class Environment(val kafkaBrokers: String = getEnvVar("KAFKA_BROKERS"),
                       val kafkaSchemaRegistry: String = getEnvVar("KAFKA_SCHEMA_REGISTRY"),
                       val groupId: String = getEnvVar("GROUP_ID"),
                       val dbHost: String = getEnvVar("DB_EVENTHANDLER_HOST"),
                       val dbName: String = getEnvVar("DB_EVENTHANDLER_DATABASE"),
                       val dbUser: String = getEnvVar("DB_EVENTHANDLER_USERNAME"),
                       val dbPassword: String = getEnvVar("DB_EVENTHANDLER_PASSWORD"),
                       val dbPort: String = getEnvVar("DB_EVENTHANDLER_PORT"),
                       val dbUrl: String = getDbUrl(dbHost, dbPort, dbName),
                       val doneInputTopicName: String = getEnvVar("OPEN_INPUT_DONE_TOPIC"),
                       val securityConfig: SecurityConfig = SecurityConfig(isCurrentlyRunningOnNais())
)

fun getDbUrl(host: String, port: String, name: String): String {
    return if (host.endsWith(":$port")) {
        "jdbc:postgresql://${host}/$name"
    } else {
        "jdbc:postgresql://${host}:${port}/${name}"
    }
}

data class SecurityConfig(
    val enabled: Boolean,

    val variables: SecurityVars? = if (enabled) {
        SecurityVars()
    } else {
        null
    }
)

data class SecurityVars(
    val kafkaTruststorePath: String = getEnvVar("KAFKA_TRUSTSTORE_PATH"),
    val kafkaKeystorePath: String = getEnvVar("KAFKA_KEYSTORE_PATH"),
    val kafkaCredstorePassword: String = getEnvVar("KAFKA_CREDSTORE_PASSWORD"),
    val kafkaSchemaRegistryUser: String = getEnvVar("KAFKA_SCHEMA_REGISTRY_USER"),
    val kafkaSchemaRegistryPassword: String = getEnvVar("KAFKA_SCHEMA_REGISTRY_PASSWORD")
)
