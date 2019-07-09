package no.nav.personbruker.dittnav.eventhandler.database.repository

import no.nav.personbruker.dittnav.eventhandler.config.DatabaseConnectionFactory.dbQuery
import no.nav.personbruker.dittnav.eventhandler.database.entity.Informasjon
import no.nav.personbruker.dittnav.eventhandler.database.tables.InformasjonTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.joda.time.DateTime
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


class InformasjonRepository {

    suspend fun getInformasjonByIdent(ident: String): List<Informasjon> = dbQuery {
        InformasjonTable.select {
            (InformasjonTable.aktorId eq ident)
        }.mapNotNull {
            toInformasjon(it)
        }.toMutableList()
    }

    private fun toInformasjon(row: ResultRow): Informasjon {
        val eventTidspunktJoda = row[InformasjonTable.eventTidspunkt]
        val eventTidspunkt = convertToZonedDateTime(eventTidspunktJoda)

        val sistOppdatertJoda = row[InformasjonTable.sistOppdatert]
        val sistOppdatert = convertToZonedDateTime(sistOppdatertJoda)

        return Informasjon(
                id = row[InformasjonTable.id].value,
                produsent = row[InformasjonTable.produsent],
                eventTidspunkt = eventTidspunkt,
                aktorId = row[InformasjonTable.aktorId],
                eventId = row[InformasjonTable.eventId],
                dokumentId = row[InformasjonTable.dokumentId],
                tekst = row[InformasjonTable.tekst],
                link = row[InformasjonTable.link],
                sikkerhetsnivaa = row[InformasjonTable.sikkerhetsnivaa],
                sistOppdatert = sistOppdatert,
                aktiv = row[InformasjonTable.aktiv]
        )
    }
}

fun convertToZonedDateTime(dateTime: DateTime): ZonedDateTime {
    val milliSeconds = dateTime.toInstant().millis
    val javaInstant = Instant.ofEpochMilli(milliSeconds)
    return javaInstant.atZone(ZoneId.of(dateTime.zone.id))
}
