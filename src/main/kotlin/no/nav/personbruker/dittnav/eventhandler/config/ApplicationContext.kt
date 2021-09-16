package no.nav.personbruker.dittnav.eventhandler.config

import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.common.produsent.ProducerNameAliasService
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.done.DoneProducer
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.StatusoppdateringEventService
import org.apache.kafka.clients.producer.KafkaProducer

class ApplicationContext {

    private val environment = Environment()

    val kafkaProducerDone = KafkaProducerWrapper(Kafka.doneTopicName ,KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))

    val database: Database = PostgresDatabase(environment)

    private val doneProducer = DoneProducer(kafkaProducerDone)

    val beskjedEventService = BeskjedEventService(database)
    val oppgaveEventService = OppgaveEventService(database)
    val innboksEventService = InnboksEventService(database)
    val doneEventService = DoneEventService(database, doneProducer)
    val statusoppdateringEventService = StatusoppdateringEventService(database)

    val healthService = HealthService(this)
    val producerNameAliasService = ProducerNameAliasService(database)
}
