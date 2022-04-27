package no.nav.personbruker.dittnav.eventhandler.config

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.brukernotifikasjon.schemas.input.DoneInput
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.done.DoneProducer
import no.nav.personbruker.dittnav.eventhandler.event.EventRepository
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.statistics.EventStatisticsService
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.StatusoppdateringEventService
import org.apache.kafka.clients.producer.KafkaProducer

fun main() {

    val environment = Environment()

    val kafkaProducerDone = KafkaProducerWrapper(environment.doneInputTopicName ,KafkaProducer<NokkelInput, DoneInput>(Kafka.producerProps(environment)))
    val doneProducer = DoneProducer(kafkaProducerDone)

    val database: Database = PostgresDatabase(environment)

    val beskjedEventService = BeskjedEventService(database)
    val oppgaveEventService = OppgaveEventService(database)
    val innboksEventService = InnboksEventService(database)
    val doneEventService = DoneEventService(database, doneProducer)
    val statusoppdateringEventService = StatusoppdateringEventService(database)
    val eventStatisticsService = EventStatisticsService(database)
    val eventRepository = EventRepository(database)

    val healthService = HealthService(database)

    embeddedServer(Netty) {
        eventHandlerApi(
            healthService,
            beskjedEventService,
            oppgaveEventService,
            innboksEventService,
            doneEventService,
            statusoppdateringEventService,
            eventRepository,
            eventStatisticsService,
            database,
            doneProducer
        )
    }.start(wait = true)
}
