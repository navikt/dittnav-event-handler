package no.nav.personbruker.dittnav.eventhandler.common.database

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KLogger
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthCheck
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthStatus
import no.nav.personbruker.dittnav.eventhandler.common.health.Status
import no.nav.personbruker.dittnav.eventhandler.config.RetriableDatabaseException
import no.nav.personbruker.dittnav.eventhandler.config.UnretriableDatabaseException
import org.postgresql.util.PSQLException
import java.sql.Connection
import java.sql.SQLException
import java.sql.SQLRecoverableException
import java.sql.SQLTransientException

interface Database : HealthCheck {

    val log: KLogger

    val dataSource: HikariDataSource

    fun close() {
        dataSource.close()
    }

    suspend fun <T> dbQuery(operationToExecute: Connection.() -> T): T = withContext(Dispatchers.IO) {
        dataSource.connection.use { openConnection ->
            try {
                openConnection.operationToExecute().apply {
                    openConnection.commit()
                }
            } catch (e: Exception) {
                try {
                    openConnection.rollback()
                } catch (rollbackException: Exception) {
                    e.addSuppressed(rollbackException)
                }
                throw e
            }
        }
    }

    suspend fun <T> queryWithExceptionTranslation(ident: String? = null, operationToExecute: Connection.() -> T): T {
        return translateExternalExceptionsToInternalOnes {
            dbQuery {
                operationToExecute()
            }
        }
    }

    override suspend fun status(): HealthStatus {
        val serviceName = "Database"
        return withContext(Dispatchers.IO) {
            try {
                dbQuery { prepareStatement("""SELECT 1""").execute() }
                HealthStatus(serviceName, Status.OK, "200 OK", includeInReadiness = false)
            } catch (e: Exception) {
                log.error("Selftest mot databasen feilet.", e)
                HealthStatus(serviceName, Status.ERROR, "Feil mot DB", includeInReadiness = false)
            }
        }
    }
}

inline fun <T> translateExternalExceptionsToInternalOnes(ident: String? = null, databaseActions: () -> T): T {
    return try {
        databaseActions()
    } catch (te: SQLTransientException) {
        val message = "Lesing fra databasen feilet grunnet en periodisk feil."
        throw RetriableDatabaseException(message, te,ident)
    } catch (re: SQLRecoverableException) {
        val message = "Lesing fra databasen feilet grunnet en periodisk feil."
        throw RetriableDatabaseException(message, re,ident)
    } catch (pe: PSQLException) {
        val message = "Det skjedde en SQL relatert feil ved lesing fra databasen."
        val ure = UnretriableDatabaseException(message, pe,ident)
        pe.sqlState?.map { sqlState -> ure.addContext("sqlState", sqlState) }
        throw ure
    } catch (se: SQLException) {
        val message = "Det skjedde en SQL relatert feil ved lesing fra databasen."
        val ure = UnretriableDatabaseException(message, se,ident)
        se.sqlState?.map { sqlState -> ure.addContext("sqlState", sqlState) }
        throw ure
    } catch (e: Exception) {
        val message = "Det skjedde en ukjent feil ved lesing fra databasen."
        throw UnretriableDatabaseException(message, e,ident)
    }
}
