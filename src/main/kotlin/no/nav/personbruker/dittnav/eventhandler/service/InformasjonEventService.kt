package no.nav.personbruker.dittnav.eventhandler.service

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.config.ConfigUtil
import no.nav.personbruker.dittnav.eventhandler.database.entity.Informasjon
import no.nav.personbruker.dittnav.eventhandler.database.repository.InformasjonRepository
import org.slf4j.LoggerFactory

class InformasjonEventService(
        val repository: InformasjonRepository = InformasjonRepository()
) {

    val log = LoggerFactory.getLogger(InformasjonEventService::class.java)

    fun getEventFromCache(id: String): String {
        var personInfo: String = ""

        runBlocking {
            val fetchedRow = repository.getInformasjonById(1)
            personInfo = personInfo + " ," + fetchedRow
            log.info("- - - - - - - - - * personInfo: Ny rad hentet fra databasen: $personInfo * - - - - - - -")
        }
        return personInfo
    }

}