package no.nav.personbruker.dittnav.eventhandler.common.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import javax.sql.DataSource

interface Database {

    val dataSource: DataSource

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

}
