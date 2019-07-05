package no.nav.personbruker.dittnav.eventhandler.database.repository

import no.nav.personbruker.dittnav.eventhandler.config.DatabaseConnectionFactory.dbQuery
import no.nav.personbruker.dittnav.eventhandler.database.entity.Informasjon
import no.nav.personbruker.dittnav.eventhandler.database.tables.InformasjonTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

class InformasjonRepository {

    suspend fun getInformasjonByIdent(ident: String): List<Informasjon> = dbQuery {
        InformasjonTable.select {
            (InformasjonTable.aktorid eq ident)
        }.mapNotNull {
            toInformasjon(it)
        }.toMutableList()
    }

    private fun toInformasjon(row: ResultRow): Informasjon =
            Informasjon(
                    id = row[InformasjonTable.id],
                    produsent = row[InformasjonTable.produsent],
                    eventTidspunkt = row[InformasjonTable.eventTidspunkt],
                    aktorid = row[InformasjonTable.aktorid],
                    eventId = row[InformasjonTable.eventId],
                    dokumentId = row[InformasjonTable.dokumentId],
                    tekst = row[InformasjonTable.tekst],
                    link = row[InformasjonTable.link],
                    sikkerhetsnivaa = row[InformasjonTable.sikkerhetsnivaa],
                    sistOppdatert = row[InformasjonTable.sistOppdatert],
                    aktiv = row[InformasjonTable.aktiv]
            )
}
