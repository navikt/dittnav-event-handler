package no.nav.personbruker.dittnav.eventhandler.config

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedProducer
import no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon.BrukernotifikasjonService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.done.DoneProducer
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveProducer
import org.apache.kafka.clients.producer.KafkaProducer

class ApplicationContext {

    val environment = Environment()

    val kafkaProducerDone = KafkaProducerWrapper(Kafka.doneTopicName ,KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))
    val kafkaProducerDoneBackup = KafkaProducerWrapper(Kafka.doneTopicNameBackup ,KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))
    val kafkaProducerBeskjedBackup = KafkaProducerWrapper(Kafka.beskjedTopicNameBackup ,KafkaProducer<Nokkel, Beskjed>(Kafka.producerProps(environment)))
    val kafkaProducerOppgaveBackup = KafkaProducerWrapper(Kafka.oppgaveTopicNameBackup ,KafkaProducer<Nokkel, Oppgave>(Kafka.producerProps(environment)))

    val database: Database = PostgresDatabase(environment)

    val doneProducer = DoneProducer(kafkaProducerDone)
    val beskjedProducer = BeskjedProducer(kafkaProducerBeskjedBackup, kafkaProducerDoneBackup)
    val oppgaveProducer = OppgaveProducer(kafkaProducerOppgaveBackup, kafkaProducerDoneBackup)

    val beskjedEventService = BeskjedEventService(database, beskjedProducer)
    val oppgaveEventService = OppgaveEventService(database, oppgaveProducer)
    val innboksEventService = InnboksEventService(database)
    val doneEventService = DoneEventService(database, doneProducer)

    val healthService = HealthService(this)
    val brukernotifikasjonService = BrukernotifikasjonService(database)
}
