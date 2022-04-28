package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class BeskjedTransformersTest {

    @Test
    fun `skal transformere til DTO`() {
        val beskjed = BeskjedObjectMother.createBeskjed()
        val beskjedDTO = beskjed.toDTO()
        beskjedDTO.fodselsnummer shouldBe beskjed.fodselsnummer
        beskjedDTO.grupperingsId shouldBe beskjed.grupperingsId
        beskjedDTO.eventId shouldBe beskjed.eventId
        beskjedDTO.eventTidspunkt shouldBe beskjed.eventTidspunkt
        beskjedDTO.produsent shouldBe beskjed.appnavn
        beskjedDTO.sikkerhetsnivaa shouldBe beskjed.sikkerhetsnivaa
        beskjedDTO.sistOppdatert shouldBe beskjed.sistOppdatert
        beskjedDTO.tekst shouldBe beskjed.tekst
        beskjedDTO.link shouldBe beskjed.link
        beskjedDTO.aktiv shouldBe beskjed.aktiv
    }
}
