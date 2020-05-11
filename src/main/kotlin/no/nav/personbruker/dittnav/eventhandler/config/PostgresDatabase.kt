package no.nav.personbruker.dittnav.eventhandler.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthStatus
import no.nav.personbruker.dittnav.eventhandler.common.health.Status
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLException

class PostgresDatabase(env: Environment) : Database {

    private val envDataSource: HikariDataSource
    private val log: Logger = LoggerFactory.getLogger(PostgresDatabase::class.java)

    init {
        envDataSource = createCorrectConnectionForEnvironment(env)
    }

    override val dataSource: HikariDataSource
        get() = envDataSource


    private fun createCorrectConnectionForEnvironment(env: Environment): HikariDataSource {
        return when (ConfigUtil.isCurrentlyRunningOnNais()) {
            true -> createConnectionViaVaultWithDbUser(env)
            false -> createConnectionForLocalDbWithDbUser(env)
        }
    }

    private fun createConnectionForLocalDbWithDbUser(env: Environment): HikariDataSource {
        return hikariFromLocalDb(env, env.dbUser)
    }

    private fun createConnectionViaVaultWithDbUser(env: Environment): HikariDataSource {
        return hikariDatasourceViaVault(env, env.dbReadOnlyUser)
    }

    companion object {

        fun hikariFromLocalDb(env: Environment, dbUser: String): HikariDataSource {
            val dbPassword: String = getEnvVar("DB_PASSWORD")
            val config = hikariCommonConfig(env).apply {
                username = dbUser
                password = dbPassword
                validate()
            }
            return HikariDataSource(config)
        }

        fun hikariDatasourceViaVault(env: Environment, dbUser: String): HikariDataSource {
            val config = hikariCommonConfig(env)
            config.validate()
            return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, env.dbMountPath, dbUser)
        }

        private fun hikariCommonConfig(env: Environment): HikariConfig {
            val config = HikariConfig()
            config.driverClassName = "org.postgresql.Driver"
            config.jdbcUrl = env.dbUrl
            config.minimumIdle = 1
            config.maxLifetime = 30001
            config.maximumPoolSize = 4
            config.connectionTimeout = 6000
            config.validationTimeout = 1000
            config.idleTimeout = 10001
            config.isAutoCommit = false
            config.isReadOnly = true
            config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            return config
        }
    }

    override suspend fun status(): HealthStatus {
        val serviceName = "Database"
        return withContext(Dispatchers.IO) {
            try {
                dbQuery { prepareStatement("""SELECT 1""").execute() }
                HealthStatus(serviceName, Status.OK, "200 OK", includeInReadiness = true)
            } catch (e: SQLException) {
                log.error("Vi har ikke tilgang til databasen.", e)
                HealthStatus(serviceName, Status.ERROR, "Feil mot DB", includeInReadiness = true)
            } catch (e: Exception) {
                log.error("Vi får en uventet feil mot databasen.", e)
                HealthStatus(serviceName, Status.ERROR, "Feil mot DB", includeInReadiness = true)
            }
        }
    }

}
