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

    fun produceDoneEventForSuppliedEventId(ident: String, eventId: String) {
        val doneEvent = createDoneEvent(ident)
        val doneKey = createKeyForEvent(eventId)
        produceDoneEvent(doneEvent, doneKey)
        log.info("Har produsert et done-event for identen: $ident sitt event med eventId: $eventId")
    }

    private fun produceDoneEvent(doneEvent : Done, doneKey: Nokkel) {
        KafkaProducer<Nokkel, Done>(Kafka.producerProps(Environment())).use { producer ->
            producer.send(ProducerRecord(doneTopicName, doneKey, doneEvent))
        }
    }

    private fun createDoneEvent(ident: String): Done {
        val nowInMs = Instant.now().toEpochMilli()
        val build = Done.newBuilder()
                .setFodselsnummer(ident)
                .setTidspunkt(nowInMs)
                .setGrupperingsId("100$nowInMs")
        return build.build()
    }
}