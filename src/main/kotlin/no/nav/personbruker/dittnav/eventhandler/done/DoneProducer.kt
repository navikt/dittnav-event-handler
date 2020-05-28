package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper

class DoneProducer(private val kafkaProducerWrapper: KafkaProducerWrapper<Done>) {

    fun produceDoneEventForSuppliedEventId(fodselsnummer: String, eventId: String, beskjed: Beskjed) {
        val doneKey = createKeyForEvent(eventId, beskjed.systembruker)
        val doneEvent = createDoneEvent(fodselsnummer, beskjed.grupperingsId)

        kafkaProducerWrapper.sendDoneEvent(doneKey, doneEvent)
    }

    fun produceDoneEventsFromList(events: List<Beskjed>) {
        events.forEach { event ->
            val doneKey = createKeyForEvent(event.eventId, event.systembruker)
            val doneEvent = createDoneEvent(event.fodselsnummer, event.grupperingsId)

            kafkaProducerWrapper.sendDoneEvent(doneKey, doneEvent)
        }


    }
}
