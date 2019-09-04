package no.nav.personbruker.dittnav.eventhandler.service

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.database.Database
import no.nav.personbruker.dittnav.eventhandler.database.entity.Informasjon
import no.nav.personbruker.dittnav.eventhandler.database.entity.getInformasjonByAktorid

class InformasjonEventService(
        val database: Database = Database(Environment())
) {

    fun getEventsFromCacheForUser(aktorid: String): List<Informasjon> {
        var fetchedRows = emptyList<Informasjon>()

        runBlocking {
            fetchedRows = database.dbQuery {getInformasjonByAktorid(aktorid)}
        }

        return fetchedRows
    }

}
