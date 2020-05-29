package no.nav.personbruker.dittnav.eventhandler.beskjed


import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.createDoneEvent
import no.nav.personbruker.dittnav.eventhandler.done.createKeyForEvent

class BeskjedProducer(
        private val beskjedKafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Beskjed>,
        private val doneKafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>
) {

    fun produceAllBeskjedEventsFromList(events: List<Beskjed>) {
        events.forEach { event ->
            val key = createKeyForEvent(event.eventId, event.systembruker)
            val beskjedEvent = createBeskjedEvent(event)
            beskjedKafkaProducer.sendEvent(key, beskjedEvent)
        }
    }

    fun produceDoneEventFromInactiveBeskjedEvents(events: List<Beskjed>) {
        events.forEach { event ->
            val key = createKeyForEvent(event.eventId, event.systembruker)
            val doneEvent = createDoneEvent(event.fodselsnummer, event.grupperingsId)
            doneKafkaProducer.sendEvent(key, doneEvent)
        }
    }

}