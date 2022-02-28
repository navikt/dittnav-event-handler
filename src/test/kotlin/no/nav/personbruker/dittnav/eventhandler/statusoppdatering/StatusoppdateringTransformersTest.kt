package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class StatusoppdateringTransformersTest {

    @Test
    fun `skal transformere til DTO`() {
        val statusoppdatering = StatusoppdateringObjectMother.createStatusoppdateringWithFodselsnummer(id = 1, fodselsnummer = "12345678901")
        val statusoppdateringDTO = statusoppdatering.toDTO()
        statusoppdateringDTO.eventId `should be equal to` statusoppdatering.eventId
        statusoppdateringDTO.eventTidspunkt `should be equal to` statusoppdatering.eventTidspunkt
        statusoppdateringDTO.fodselsnummer `should be equal to` statusoppdatering.fodselsnummer
        statusoppdateringDTO.grupperingsId `should be equal to` statusoppdatering.grupperingsId
        statusoppdateringDTO.link `should be equal to` statusoppdatering.link
        statusoppdateringDTO.produsent `should be equal to` statusoppdatering.appnavn
        statusoppdateringDTO.sakstema `should be equal to` statusoppdatering.sakstema
        statusoppdateringDTO.statusGlobal `should be equal to` statusoppdatering.statusGlobal
        statusoppdateringDTO.statusIntern `should be equal to` statusoppdatering.statusIntern
        statusoppdateringDTO.sikkerhetsnivaa `should be equal to` statusoppdatering.sikkerhetsnivaa
        statusoppdateringDTO.sistOppdatert `should be equal to` statusoppdatering.sistOppdatert
    }
}
