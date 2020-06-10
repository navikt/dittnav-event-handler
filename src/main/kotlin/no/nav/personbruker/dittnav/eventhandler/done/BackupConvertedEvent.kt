package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel

data class BackupConvertedEvent(val nokkel: Nokkel, val doneEvent: Done)