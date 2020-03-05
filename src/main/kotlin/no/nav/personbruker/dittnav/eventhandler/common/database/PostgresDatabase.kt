package no.nav.personbruker.dittnav.eventhandler.common.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.personbruker.dittnav.eventhandler.config.ConfigUtil
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil
import javax.sql.DataSource

class PostgresDatabase(env: Environment) : Database {

    private val envDataSource: DataSource

    init {
        envDataSource = createCorrectConnectionForEnvironment(env)
    }

    override val dataSource: DataSource
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
            val config = hikariCommonConfig(env).apply {
                username = dbUser
                password = env.dbPassword
                validate()
            }
            return HikariDataSource(config)
        }

        fun hikariDatasourceViaVault(env: Environment, dbUser: String): HikariDataSource {
            var config = hikariCommonConfig(env)
            config.validate()
            return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, env.dbMountPath, dbUser)
        }

        private fun hikariCommonConfig(env: Environment): HikariConfig {
            val config = HikariConfig()
            config.driverClassName = "org.postgresql.Driver"
            config.jdbcUrl = env.dbUrl
            config.minimumIdle = 0
            config.maxLifetime = 30001
            config.maximumPoolSize = 2
            config.connectionTimeout = 500
            config.validationTimeout = 250
            config.idleTimeout = 10001
            config.isAutoCommit = false
            config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            return config
        }
    }

}