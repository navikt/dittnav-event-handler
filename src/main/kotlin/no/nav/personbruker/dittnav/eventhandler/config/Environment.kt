package no.nav.personbruker.dittnav.eventhandler.config

import java.net.URL

data class Environment(val bootstrapServers: String = getEnvVar("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
                       val schemaRegistryUrl: String = getEnvVar("KAFKA_SCHEMAREGISTRY_SERVERS", "http://localhost:8081"),
                       val username: String = getEnvVar("FSS_SYSTEMUSER_USERNAME", "username"),
                       val password: String = getEnvVar("FSS_SYSTEMUSER_PASSWORD", "password"),
                       val groupId: String = getEnvVar("GROUP_ID", "dittnav_events"),
                       val dbHost: String = getEnvVar("DB_HOST", "localhost:5432"),
                       val dbName: String = getEnvVar("DB_NAME", "dittnav-event-cache-preprod"),
                       val dbUser: String = getEnvVar("DB_NAME", "test") + "-user",
                       val dbReadOnlyUser: String = getEnvVar("DB_NAME", "test") + "-readonly",
                       val dbUrl: String = "jdbc:postgresql://$dbHost/$dbName",
                       val dbPassword: String = getEnvVar("DB_PASSWORD", "testpassword"),
                       val dbMountPath: String = getEnvVar("DB_MOUNT_PATH", "notUsedOnLocalhost"),
                       val securityAudience: String = getEnvVar("AUDIENCE", "dummyAudience"),
                       val securityJwksIssuer: String = getEnvVar("JWKS_ISSUER", "dummyIssuer"),
                       val securityJwksUri: URL = URL(getEnvVar("JWKS_URI", "https://dummyUrl.com"))
)

fun getEnvVar(varName: String, defaultValue: String? = null): String {
    return System.getenv(varName) ?: defaultValue
    ?: throw IllegalArgumentException("Variable $varName cannot be empty")
}
