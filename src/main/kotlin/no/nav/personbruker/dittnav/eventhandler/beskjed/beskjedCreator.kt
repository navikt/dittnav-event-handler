package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.brukernotifikasjon.schemas.builders.BeskjedBuilder
import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil

fun createBeskjedEvent(beskjed: Beskjed): no.nav.brukernotifikasjon.schemas.Beskjed {
    val build = BeskjedBuilder()
            .withTidspunkt(beskjed.eventTidspunkt.toLocalDateTime())
            .withFodselsnummer(beskjed.fodselsnummer)
            .withGrupperingsId(beskjed.grupperingsId)
            .withTekst(beskjed.tekst)
            .withLink(ValidationUtil.validateLinkAndConvertToURL(beskjed.link))
            .withSikkerhetsnivaa(beskjed.sikkerhetsnivaa)
            .withSynligFremTil(beskjed.synligFremTil?.toLocalDateTime())
    return build.build()
}
