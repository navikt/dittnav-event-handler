package no.nav.personbruker.dittnav.eventhandler.beskjed

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class BeskjedTransformersTest {

    @Test
    fun `skal transformere til DTO`() {
        val beskjed = BeskjedObjectMother.createBeskjed()
        val beskjedDTO = beskjed.toDTO()
        beskjedDTO.fodselsnummer `should be equal to` beskjed.fodselsnummer
        beskjedDTO.grupperingsId `should be equal to` beskjed.grupperingsId
        beskjedDTO.eventId `should be equal to` beskjed.eventId
        beskjedDTO.eventTidspunkt `should be equal to` beskjed.eventTidspunkt
        beskjedDTO.produsent `should be equal to` beskjed.appnavn
        beskjedDTO.sikkerhetsnivaa `should be equal to` beskjed.sikkerhetsnivaa
        beskjedDTO.sistOppdatert `should be equal to` beskjed.sistOppdatert
        beskjedDTO.tekst `should be equal to` beskjed.tekst
        beskjedDTO.link `should be equal to` beskjed.link
        beskjedDTO.aktiv `should be equal to` beskjed.aktiv
    }
}
