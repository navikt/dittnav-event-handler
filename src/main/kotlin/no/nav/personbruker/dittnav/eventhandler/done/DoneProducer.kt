package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthCheck
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthStatus
import no.nav.personbruker.dittnav.eventhandler.common.health.Status
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.doneTopicName
import no.nav.personbruker.dittnav.eventhandler.health.ProduceAttemptCounter
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.TimeoutException
import org.slf4j.LoggerFactory

class DoneProducer(private val env: Environment): HealthCheck {

    private val log = LoggerFactory.getLogger(DoneProducer::class.java)
    private val produceAttemptCounter: ProduceAttemptCounter = ProduceAttemptCounter(10, 10)

    private val kafkaProducer = KafkaProducer<Nokkel, Done>(Kafka.producerProps(env))

    fun produceDoneEventForSuppliedEventId(fodselsnummer: String, eventId: String, beskjed: Beskjed) {
        val doneKey = createKeyForEvent(eventId, beskjed.produsent)
        val doneEvent = createDoneEvent(fodselsnummer, beskjed.grupperingsId)
        kafkaProducer.send(ProducerRecord(doneTopicName, doneKey, doneEvent), Callback { metadata, exception ->
            if(exception != null) {
                produceAttemptCounter.failure()
                when(exception) {
                    TimeoutException::class.java -> log.warn("Fikk timeout ved produsering av Done-event for Beskjed-event med eventId $eventId.", exception)
                    else -> log.error("Klarte ikke produsere Done-event for Beskjed-event med eventId $eventId.", exception)
                }
            } else {
                produceAttemptCounter.success()
            }
        })
    }

    fun flushAndClose() {
        try {
            kafkaProducer.flush()
            kafkaProducer.close()
            log.info("Produsent for Done-eventer er flushet og lukket.")
        } catch(e: Exception) {
            log.warn("Klarte ikke å flushe og lukke produsent for Done-eventer. Det kan være eventer som ikke ble produsert.")
        }
    }

    override fun status(): HealthStatus {
        val serviceName = "Done-producer"
        return if(produceAttemptCounter.isUnhealthy()) {
            HealthStatus(serviceName, Status.OK, "200 OK")
        } else {
            HealthStatus(serviceName, Status.ERROR, "Noe er galt med å produsere Done-eventer. Sjekk logger for mer info.")
        }
    }
}
