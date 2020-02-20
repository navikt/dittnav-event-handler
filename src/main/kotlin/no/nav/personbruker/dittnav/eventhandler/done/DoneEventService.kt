package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class DoneEventService(private val database: Database) {

    suspend fun getBeskjedFromCacheForUser(fodselsnummer: String, uid: Int, eventId: String): List<Beskjed> {
        return database.dbQuery {
            getActiveBeskjedByIds(fodselsnummer, uid, eventId)
        }
    }

    fun markEventAsDone(fodselsnummer: String, eventId : String, produser: String, grupperingsId: String) {
        DoneProducer.produceDoneEventForSuppliedEventId(fodselsnummer, eventId, produser, grupperingsId)
    }
}