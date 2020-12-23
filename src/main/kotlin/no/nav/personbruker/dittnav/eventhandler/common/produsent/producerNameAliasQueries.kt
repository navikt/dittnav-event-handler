package no.nav.personbruker.dittnav.eventhandler.common.produsent

import no.nav.personbruker.dittnav.common.util.database.fetching.mapList
import java.sql.Connection
import java.sql.ResultSet

fun Connection.getProdusent(): List<Produsent> =
        prepareStatement("""SELECT * FROM systembrukere""")
                .use {
                    it.executeQuery().mapList {
                        toProdusent()
                    }
                }

private fun ResultSet.toProdusent(): Produsent {
    return Produsent(
            systembruker = getString("systembruker"),
            produsentnavn = getString("produsentnavn")
    )
}
