package no.nav.personbruker.dittnav.eventhandler.common.database

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.postgresql.util.PSQLException
import java.lang.Thread.sleep

class LocalPostgresDatabase : Database {

    private val memDataSource: HikariDataSource
    private val container = TestPostgresqlContainer()

    init {
        container.start()
        memDataSource = createDataSource()
        createTablesAndViews()
    }

    override val dataSource: HikariDataSource
        get() = memDataSource

    private fun createDataSource(): HikariDataSource {
        return HikariDataSource().apply {
            jdbcUrl = container.jdbcUrl
            username = container.username
            password = container.password
            isAutoCommit = false
            validate()
        }
    }

    private fun createTablesAndViews() {
        runBlocking {
            withTimeout(3000) {
                while (true) {
                    try {
                        val fileContent = this::class.java.getResource("/db/createTablesAndViews.sql").readText()
                        dbQuery { prepareStatement(fileContent).execute() }
                        return@withTimeout
                    } catch (_: PSQLException) {
                        delay(100)
                    }
                }
            }

        }
    }
}
