package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import java.sql.Connection

abstract class BackupService<T>(private val database: Database) {

    suspend fun getEvents(operationToExecute: Connection.() -> List<T>): List<T> {
        return database.queryWithExceptionTranslation {
            operationToExecute()
        }
    }
}
