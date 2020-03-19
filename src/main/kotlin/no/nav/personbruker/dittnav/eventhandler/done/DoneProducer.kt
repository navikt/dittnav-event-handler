package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import org.slf4j.LoggerFactory
import java.time.Instant
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.doneTopicName

class DoneProducer(private val env: Environment) {

    private val log = LoggerFactory.getLogger(DoneProducer::class.java)

    private val kafkaProducer = KafkaProducer<Nokkel, Done>(Kafka.producerProps(env))

    fun produceDoneEventForSuppliedEventId(fodselsnummer: String, eventId: String, beskjed: Beskjed) {
        val doneKey = createKeyForEvent(eventId, beskjed.produsent)
        val doneEvent = createDoneEvent(fodselsnummer, beskjed.grupperingsId)
        try {
            kafkaProducer.send(ProducerRecord(doneTopicName, doneKey, doneEvent))
            log.info("Har produsert et done-event for Beskjed-event med eventId $eventId")
        } catch(e: Exception) {
            log.error("Klarte ikke å produsere Done-event for Beskjed-event med eventId $eventId", e)
        }
    }

    fun close() {
        try {
            kafkaProducer.close()
            log.info("Produsent for Done-eventer er lukket.")
        } catch(e: Exception) {
            log.warn("Klarte ikke å lukke produsent for Done-eventer. Det kan være eventer som ikke ble produsert.")
        }
    }
}
