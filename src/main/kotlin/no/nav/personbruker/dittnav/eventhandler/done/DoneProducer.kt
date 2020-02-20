package no.nav.personbruker.dittnav.eventhandler.done

import org.slf4j.LoggerFactory
import java.time.Instant
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.doneTopicName

object DoneProducer {

    private val log = LoggerFactory.getLogger(DoneProducer::class.java)

    fun produceDoneEventForSuppliedEventId(fodselsnummer: String, eventId: String, produser: String, grupperingsId: String) {
        val doneEvent = createDoneEvent(fodselsnummer, grupperingsId)
        val doneKey = createKeyForEvent(eventId, produser)
        produceDoneEvent(doneEvent, doneKey)
        log.info("Har produsert et done-event for identen: $fodselsnummer sitt event med eventId: $eventId")
    }

    private fun produceDoneEvent(doneEvent : Done, doneKey: Nokkel) {
        KafkaProducer<Nokkel, Done>(Kafka.producerProps(Environment())).use { producer ->
            producer.send(ProducerRecord(doneTopicName, doneKey, doneEvent))
        }
    }

    private fun createDoneEvent(fodselsnummer: String, grupperingsId: String): Done {
        val nowInMs = Instant.now().toEpochMilli()
        val build = Done.newBuilder()
                .setFodselsnummer(fodselsnummer)
                .setTidspunkt(nowInMs)
                .setGrupperingsId(grupperingsId)
        return build.build()
    }
}