package no.nav.personbruker.dittnav.eventhandler.config

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.informasjon.InformasjonEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService

class ApplicationContext {

    val portNumber = 8091

    val environment = Environment()
    val database = Database(environment)
    val informasjonEventService = InformasjonEventService(database)
    val oppgaveEventService = OppgaveEventService(database)

}