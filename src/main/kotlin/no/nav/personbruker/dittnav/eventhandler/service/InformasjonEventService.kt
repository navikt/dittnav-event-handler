package no.nav.personbruker.dittnav.eventhandler.service

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.database.Database
import no.nav.personbruker.dittnav.eventhandler.database.entity.Brukernotifikasjon
import no.nav.personbruker.dittnav.eventhandler.database.entity.informasjon.getInformasjonByAktorid

class InformasjonEventService(
        val database: Database = Database(Environment())
) {

    fun getEventsFromCacheForUser(aktorId: String): List<Brukernotifikasjon> {
        var fetchedRows = emptyList<Brukernotifikasjon>()

        runBlocking {
            fetchedRows = database.dbQuery {getInformasjonByAktorid(aktorId)}
        }

        return fetchedRows
    }

}
