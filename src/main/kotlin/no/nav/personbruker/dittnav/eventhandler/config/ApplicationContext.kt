package no.nav.personbruker.dittnav.eventhandler.config

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.personbruker.dittnav.eventhandler.beskjed.BackupBeskjedService
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedProducer
import no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon.BrukernotifikasjonService
import no.nav.personbruker.dittnav.eventhandler.common.produsent.ProducerNameAliasService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.BackupDoneProducer
import no.nav.personbruker.dittnav.eventhandler.done.BackupDoneService
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.done.DoneProducer
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.BackupOppgaveService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveProducer
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.StatusoppdateringEventService
import org.apache.kafka.clients.producer.KafkaProducer

class ApplicationContext {

    val environment = Environment()

    val kafkaProducerDone = KafkaProducerWrapper(Kafka.doneTopicName ,KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))
    val kafkaProducerDoneBackup = KafkaProducerWrapper(Kafka.doneTopicNameBackup ,KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))
    val kafkaProducerTableDoneBackup = KafkaProducerWrapper(Kafka.cachedDoneTopicNameBackup ,KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))
    val kafkaProducerBeskjedBackup = KafkaProducerWrapper(Kafka.beskjedTopicNameBackup ,KafkaProducer<Nokkel, Beskjed>(Kafka.producerProps(environment)))
    val kafkaProducerOppgaveBackup = KafkaProducerWrapper(Kafka.oppgaveTopicNameBackup ,KafkaProducer<Nokkel, Oppgave>(Kafka.producerProps(environment)))

    val database: Database = PostgresDatabase(environment)

    val doneProducer = DoneProducer(kafkaProducerDone)
    val beskjedProducer = BeskjedProducer(kafkaProducerBeskjedBackup, kafkaProducerDoneBackup)
    val oppgaveProducer = OppgaveProducer(kafkaProducerOppgaveBackup, kafkaProducerDoneBackup)
    val backupDoneProducer = BackupDoneProducer(kafkaProducerTableDoneBackup)

    val beskjedEventService = BeskjedEventService(database)
    val oppgaveEventService = OppgaveEventService(database)
    val innboksEventService = InnboksEventService(database)
    val doneEventService = DoneEventService(database, doneProducer)
    val backupDoneService = BackupDoneService(backupDoneProducer, database)
    val statusoppdateringEventService = StatusoppdateringEventService(database)

    val backupBeskjedEventService = BackupBeskjedService(beskjedEventService, beskjedProducer)
    val backupOppgaveService = BackupOppgaveService(oppgaveEventService, oppgaveProducer)

    val healthService = HealthService(this)
    val brukernotifikasjonService = BrukernotifikasjonService(database)
    val producerNameAliasService = ProducerNameAliasService(database)
}
