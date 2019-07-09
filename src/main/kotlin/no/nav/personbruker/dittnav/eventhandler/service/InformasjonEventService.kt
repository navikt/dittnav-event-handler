package no.nav.personbruker.dittnav.eventhandler.service

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.database.entity.Informasjon
import no.nav.personbruker.dittnav.eventhandler.database.repository.InformasjonRepository

class InformasjonEventService(
        val repository: InformasjonRepository = InformasjonRepository()
) {

    fun getEventsFromCacheForUser(ident: String): List<Informasjon> {
        var fetchedRows = emptyList<Informasjon>()

        runBlocking {
            fetchedRows = repository.getInformasjonByIdent(ident)
        }

        return fetchedRows
    }

}