package no.nav.personbruker.dittnav.eventaggregator.database

import com.zaxxer.hikari.HikariDataSource
import no.nav.personbruker.dittnav.eventhandler.database.IDatabase
import org.flywaydb.core.Flyway
import javax.sql.DataSource

class H2Database : IDatabase {

    private val memDataSource: DataSource

    init {
        memDataSource = createDataSource()
        flyway()
    }

    override val dataSource: DataSource
        get() = memDataSource

    private fun createDataSource(): HikariDataSource {
        return HikariDataSource().apply {
            jdbcUrl = "jdbc:h2:mem:testdb"
            username = "sa"
            password = ""
            validate()
        }
    }

    private fun flyway() {
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate()
    }
}