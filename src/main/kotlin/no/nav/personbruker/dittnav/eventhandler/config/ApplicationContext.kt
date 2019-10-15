package no.nav.personbruker.dittnav.eventhandler.config

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.database.PostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.informasjon.InformasjonEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService

class ApplicationContext {

    val environment = Environment()
    val database: Database = PostgresDatabase(environment)
    val informasjonEventService = InformasjonEventService(database)
    val oppgaveEventService = OppgaveEventService(database)
    val doneEventService = DoneEventService(database)

}
