package no.nav.personbruker.dittnav.eventhandler.config

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.personbruker.dittnav.eventhandler.backup.BackupBeskjedService
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.backup.BackupBeskjedTransformer
import no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon.BrukernotifikasjonService
import no.nav.personbruker.dittnav.eventhandler.common.produsent.ProducerNameAliasService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.backup.BackupDoneTranformer
import no.nav.personbruker.dittnav.eventhandler.backup.BackupDoneService
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.done.DoneProducer
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.backup.BackupOppgaveService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.backup.BackupOppgaveTransformer
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.StatusoppdateringEventService
import org.apache.kafka.clients.producer.KafkaProducer

class ApplicationContext {

    private val environment = Environment()

    val kafkaProducerDone = KafkaProducerWrapper(Kafka.doneTopicName ,KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))
    val kafkaProducerDoneBackup = KafkaProducerWrapper(Kafka.doneTopicNameBackup ,KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))
    val kafkaProducerTableDoneBackup = KafkaProducerWrapper(Kafka.cachedDoneTopicNameBackup ,KafkaProducer<Nokkel, Done>(Kafka.producerProps(environment)))
    val kafkaProducerBeskjedBackup = KafkaProducerWrapper(Kafka.beskjedTopicNameBackup ,KafkaProducer<Nokkel, Beskjed>(Kafka.producerProps(environment)))
    val kafkaProducerOppgaveBackup = KafkaProducerWrapper(Kafka.oppgaveTopicNameBackup ,KafkaProducer<Nokkel, Oppgave>(Kafka.producerProps(environment)))

    val database: Database = PostgresDatabase(environment)

    private val doneProducer = DoneProducer(kafkaProducerDone)
    private val backupBeskjedProducer = BackupBeskjedTransformer()
    private val backupOppgaveProducer = BackupOppgaveTransformer()
    private val backupDoneProducer = BackupDoneTranformer()

    val beskjedEventService = BeskjedEventService(database)
    val oppgaveEventService = OppgaveEventService(database)
    val innboksEventService = InnboksEventService(database)
    val doneEventService = DoneEventService(database, doneProducer)
    val statusoppdateringEventService = StatusoppdateringEventService(database)

    val backupBeskjedEventService = BackupBeskjedService(database, kafkaProducerBeskjedBackup, kafkaProducerDoneBackup, backupBeskjedProducer)
    val backupOppgaveService = BackupOppgaveService(database, kafkaProducerOppgaveBackup, kafkaProducerDoneBackup, backupOppgaveProducer)
    val backupDoneService = BackupDoneService(database, kafkaProducerDoneBackup, backupDoneProducer)

    val healthService = HealthService(this)
    val brukernotifikasjonService = BrukernotifikasjonService(database)
    val producerNameAliasService = ProducerNameAliasService(database)
}
