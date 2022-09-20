package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class BeskjedTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val beskjed = BeskjedObjectMother.createBeskjed(eventId = "dummyEventId1", fodselsnummer = "dummmyFnr1")
        val beskjedAsString = beskjed.toString()
        beskjedAsString shouldContain "fodselsnummer=***"
        beskjedAsString shouldContain "tekst=***"
        beskjedAsString shouldContain "link=***"
        beskjedAsString shouldContain "systembruker=x-dittnav"
    }

    @Test
    fun `skal transformere til DTO`() {
        val beskjed = BeskjedObjectMother.createBeskjed()
        val beskjedDTO = beskjed.toDTO()
        beskjedDTO.fodselsnummer shouldBe beskjed.fodselsnummer
        beskjedDTO.grupperingsId shouldBe beskjed.grupperingsId
        beskjedDTO.eventId shouldBe beskjed.eventId
        beskjedDTO.produsent shouldBe beskjed.appnavn
        beskjedDTO.sikkerhetsnivaa shouldBe beskjed.sikkerhetsnivaa
        beskjedDTO.sistOppdatert shouldBe beskjed.sistOppdatert
        beskjedDTO.tekst shouldBe beskjed.tekst
        beskjedDTO.link shouldBe beskjed.link
        beskjedDTO.aktiv shouldBe beskjed.aktiv
        beskjedDTO.forstBehandlet shouldBe beskjed.forstBehandlet
    }
}
