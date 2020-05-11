package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper

class DoneProducer(private val kafkaProducerWrapper: KafkaProducerWrapper) {

    fun produceDoneEventForSuppliedEventId(fodselsnummer: String, eventId: String, beskjed: Beskjed) {
        val doneKey = createKeyForEvent(eventId, beskjed.systembruker)
        val doneEvent = createDoneEvent(fodselsnummer, beskjed.grupperingsId)

        kafkaProducerWrapper.sendDoneEvent(doneKey, doneEvent)
    }
}
