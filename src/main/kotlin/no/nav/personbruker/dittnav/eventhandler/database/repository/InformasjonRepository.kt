package no.nav.personbruker.dittnav.eventhandler.database.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.eventhandler.config.DatabaseConnectionFactory.dbQuery
import no.nav.personbruker.dittnav.eventhandler.database.entity.Informasjon
import no.nav.personbruker.dittnav.eventhandler.database.tables.InformasjonTable
import no.nav.personbruker.dittnav.eventhandler.service.InformasjonEventService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

class InformasjonRepository {

    val log = LoggerFactory.getLogger(InformasjonEventService::class.java)

    suspend fun getInformasjonById(id: Int): Informasjon? = dbQuery {
        InformasjonTable.select {
            (InformasjonTable.id eq id)
        }.mapNotNull { toInformasjon(it) }
                .singleOrNull()
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

    suspend fun createInfo(): EntityID<Int> {
        return withContext(Dispatchers.IO) {
            transaction {
                val generatedId = InformasjonTable.insertAndGetId {
                    it[produsent] = "produsent1"
                    it[eventTidspunkt] = DateTime.now()
                    it[aktorid] = "1"
                    it[eventId] = "eventId1"
                    it[dokumentId] = "dokumentId1"
                    it[tekst] = "tekst1"
                    it[link] = "link1"
                    it[sikkerhetsnivaa] = 3
                    it[sistOppdatert] = DateTime.now()
                    it[aktiv] = true
                }
                return@transaction generatedId
            }
        }
    }

}
