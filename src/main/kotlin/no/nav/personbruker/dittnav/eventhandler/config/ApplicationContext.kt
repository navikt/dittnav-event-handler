package no.nav.personbruker.dittnav.eventhandler.config

import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon.BrukernotifikasjonService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.done.DoneProducer
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import org.apache.kafka.clients.producer.KafkaProducer

class ApplicationContext {

    val environment = Environment()
    val kafkaProducerWrapper = KafkaProducerWrapper(KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))
    val database: Database = PostgresDatabase(environment)
    val beskjedEventService = BeskjedEventService(database)
    val oppgaveEventService = OppgaveEventService(database)
    val innboksEventService = InnboksEventService(database)
    val doneProducer = DoneProducer(kafkaProducerWrapper)
    val doneEventService = DoneEventService(database, doneProducer)
    val healthService = HealthService(this)
    val brukernotifikasjonService = BrukernotifikasjonService(database)
}
