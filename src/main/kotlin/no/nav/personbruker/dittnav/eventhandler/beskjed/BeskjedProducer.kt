package no.nav.personbruker.dittnav.eventhandler.beskjed


import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.createKeyForEvent

class BeskjedProducer(private val kafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Beskjed>) {

    fun produceAllBeskjedEvents(events: List<Beskjed>) {
        events.forEach { event ->
            val key = createKeyForEvent(event.eventId, event.systembruker)
            val beskjedEvent = createBeskjedEvent(event)
            kafkaProducer.sendEvent(key, beskjedEvent)
        }
    }
}