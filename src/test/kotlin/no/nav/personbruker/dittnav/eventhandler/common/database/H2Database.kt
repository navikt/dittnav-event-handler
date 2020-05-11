package no.nav.personbruker.dittnav.eventhandler.common.database

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthStatus
import no.nav.personbruker.dittnav.eventhandler.common.health.Status
import java.sql.SQLException

class H2Database : Database {

    private val memDataSource: HikariDataSource

    init {
        memDataSource = createDataSource()
        createTablesAndViews()
    }

    override val dataSource: HikariDataSource
        get() = memDataSource

    override suspend fun status(): HealthStatus {
        val serviceName = "Database"
        return withContext(Dispatchers.IO) {
            try {
                dbQuery { prepareStatement("""SELECT 1""").execute() }
                HealthStatus(serviceName, Status.OK, "200 OK", includeInReadiness = true)
            } catch (e: SQLException) {
                HealthStatus(serviceName, Status.ERROR, "Feil mot DB", includeInReadiness = true)
            } catch (e: Exception) {
                HealthStatus(serviceName, Status.ERROR, "Feil mot DB", includeInReadiness = true)
            }}
    }

    private fun createDataSource(): HikariDataSource {
        return HikariDataSource().apply {
            jdbcUrl = "jdbc:h2:mem:testdb"
            username = "sa"
            password = ""
            validate()
        }
    }

    private fun createTablesAndViews() {
        runBlocking {
            val fileContent = this::class.java.getResource("/db/createTablesAndViews.sql").readText()
            dbQuery { prepareStatement(fileContent).execute() }
        }
    }
}
