package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.brukernotifikasjon.schemas.builders.BeskjedBuilder
import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil
import no.nav.personbruker.dittnav.eventhandler.common.validation.zonedDateTimeToUTCLocalDate

fun createBeskjedEvent(beskjed: Beskjed): no.nav.brukernotifikasjon.schemas.Beskjed {
    val build = BeskjedBuilder()
            .withTidspunkt(zonedDateTimeToUTCLocalDate(beskjed.eventTidspunkt))
            .withFodselsnummer(beskjed.fodselsnummer)
            .withGrupperingsId(beskjed.grupperingsId)
            .withTekst(beskjed.tekst)
            .withLink(ValidationUtil.validateLinkAndConvertToURL(beskjed.link))
            .withSikkerhetsnivaa(beskjed.sikkerhetsnivaa)
            .withSynligFremTil(zonedDateTimeToUTCLocalDate(beskjed.synligFremTil))
    return build.build()
}
