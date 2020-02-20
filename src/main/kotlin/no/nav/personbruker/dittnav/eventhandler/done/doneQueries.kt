package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.toBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection

fun Connection.getActiveBeskjedByIds(fodselsnummer: String, uid: String, eventId: String): List<Beskjed> =
        prepareStatement("""SELECT * FROM BESKJED WHERE fodselsnummer = ? AND uid = ? AND eventId = ? AND aktiv = true""")
                .use {
                    it.executeQuery().map {
                        toBeskjed()
                    }
                }
