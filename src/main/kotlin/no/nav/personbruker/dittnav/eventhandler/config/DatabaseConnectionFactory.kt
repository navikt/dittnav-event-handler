package no.nav.personbruker.dittnav.eventhandler.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.eventhandler.service.InformasjonEventService
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseConnectionFactory {

    val log = LoggerFactory.getLogger(InformasjonEventService::class.java)

    fun initDatabase(env: Environment) {
        Database.connect(createCorrectDatasourceForEnvironment(env))
    }

    private fun createCorrectDatasourceForEnvironment(env: Environment): HikariDataSource {
        return when (ConfigUtil.isCurrentlyRunningOnNais()) {
            true -> createDataSourceViaVaultWithDbUser(env)
            false -> createDataSourceForLocalDbWithDbUser(env)
        }
    }

    private fun createDataSourceForLocalDbWithDbUser(env: Environment): HikariDataSource {
        return hikariFromLocalDb(env, env.dbAdmin)
    }

    private fun createDataSourceViaVaultWithDbUser(env: Environment): HikariDataSource {
        return hikariDatasourceViaVault(env, env.dbUser)
    }

    fun hikariFromLocalDb(env: Environment, dbUser: String): HikariDataSource {
        val config = hikariCommonConfig(env)
        config.username = dbUser
        config.password = env.dbPassword
        config.validate()
        return HikariDataSource(config)
    }

    fun hikariDatasourceViaVault(env: Environment, dbUser: String): HikariDataSource {
        var config = hikariCommonConfig(env)
        config.validate()
        log.info("- - - -- - - - - - *  env.dbMountPath" + env.dbMountPath)
        log.info("- - - -- - - - - - *  dbUser" + dbUser)
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, env.dbMountPath, dbUser)
    }

    private fun hikariCommonConfig(env: Environment): HikariConfig {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = env.dbUrl
        config.minimumIdle = 0
        config.maxLifetime = 30001
        config.maximumPoolSize = 2
        config.connectionTimeout = 250
        config.idleTimeout = 10001
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        return config
    }

    suspend fun <T> dbQuery(block: () -> T): T =
            withContext(Dispatchers.IO) {
                transaction { block() }
            }

}