package no.nav.personbruker.dittnav.eventhandler.innboks

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class InnboksTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val innboks = InnboksTestData.createInnboks()
        val innboksAsString = innboks.toString()
        innboksAsString shouldContain "fodselsnummer=***"
        innboksAsString shouldContain "tekst=***"
        innboksAsString shouldContain "link=***"
        innboksAsString shouldContain "systembruker=x-dittnav"
    }

    @Test
    fun `skal transformere til DTO`() {
        val innboks = InnboksTestData.createInnboks()
        val innboksDTO = innboks.toDTO()
        innboksDTO.fodselsnummer shouldBe innboks.fodselsnummer
        innboksDTO.eventId shouldBe innboks.eventId
        innboksDTO.aktiv shouldBe innboks.aktiv
        innboksDTO.grupperingsId shouldBe innboks.grupperingsId
        innboksDTO.link shouldBe innboks.link
        innboksDTO.produsent shouldBe innboks.appnavn
        innboksDTO.sikkerhetsnivaa shouldBe innboks.sikkerhetsnivaa
        innboksDTO.sistOppdatert shouldBe innboks.sistOppdatert
        innboksDTO.tekst shouldBe innboks.tekst
        innboksDTO.forstBehandlet shouldBe innboks.forstBehandlet
    }
}
