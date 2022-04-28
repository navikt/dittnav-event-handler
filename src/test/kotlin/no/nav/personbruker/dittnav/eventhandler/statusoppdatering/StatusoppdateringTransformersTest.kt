package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class StatusoppdateringTransformersTest {

    @Test
    fun `skal transformere til DTO`() {
        val statusoppdatering = StatusoppdateringObjectMother.createStatusoppdateringWithFodselsnummer(id = 1, fodselsnummer = "12345678901")
        val statusoppdateringDTO = statusoppdatering.toDTO()
        statusoppdateringDTO.eventId shouldBe statusoppdatering.eventId
        statusoppdateringDTO.eventTidspunkt shouldBe statusoppdatering.eventTidspunkt
        statusoppdateringDTO.fodselsnummer shouldBe statusoppdatering.fodselsnummer
        statusoppdateringDTO.grupperingsId shouldBe statusoppdatering.grupperingsId
        statusoppdateringDTO.link shouldBe statusoppdatering.link
        statusoppdateringDTO.produsent shouldBe statusoppdatering.appnavn
        statusoppdateringDTO.sakstema shouldBe statusoppdatering.sakstema
        statusoppdateringDTO.statusGlobal shouldBe statusoppdatering.statusGlobal
        statusoppdateringDTO.statusIntern shouldBe statusoppdatering.statusIntern
        statusoppdateringDTO.sikkerhetsnivaa shouldBe statusoppdatering.sikkerhetsnivaa
        statusoppdateringDTO.sistOppdatert shouldBe statusoppdatering.sistOppdatert
    }
}
