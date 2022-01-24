package no.nav.personbruker.dittnav.eventhandler.config

import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar

data class Environment(val bootstrapServers: String = getEnvVar("KAFKA_BOOTSTRAP_SERVERS"),
                       val schemaRegistryUrl: String = getEnvVar("KAFKA_SCHEMAREGISTRY_SERVERS"),
                       val username: String = getEnvVar("SERVICEUSER_USERNAME"),
                       val password: String = getEnvVar("SERVICEUSER_PASSWORD"),
                       val groupId: String = getEnvVar("GROUP_ID"),
                       val dbHost: String = getEnvVar("DB_EVENTHANDLER_HOST"),
                       val dbName: String = getEnvVar("DB_EVENTHANDLER_DATABASE"),
                       val dbUser: String = getEnvVar("DB_EVENTHANDLER_USERNAME"),
                       val dbPassword: String = getEnvVar("DB_EVENTHANDLER_PASSWORD"),
                       val dbPort: String = getEnvVar("DB_EVENTHANDLER_PORT"),
                       val dbUrl: String = getDbUrl(dbHost, dbPort, dbName),
                       val doneInputTopicName: String = getEnvVar("OPEN_INPUT_DONE_TOPIC")
)

fun getDbUrl(host: String, port: String, name: String): String {
    return if (host.endsWith(":$port")) {
        "jdbc:postgresql://${host}/$name"
    } else {
        "jdbc:postgresql://${host}:${port}/${name}"
    }
}
