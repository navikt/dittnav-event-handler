package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.brukernotifikasjon.schemas.input.DoneInput
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.config.Environment

class DoneProducer(private val kafkaProducerWrapper: KafkaProducerWrapper<DoneInput>) {

    fun produceDoneEventForSuppliedEventId(fodselsnummer: String, eventId: String, beskjed: Beskjed) {
        val doneKey = createKeyForEvent(eventId, beskjed.grupperingsId, fodselsnummer, beskjed.namespace, beskjed.appnavn)
        val doneEvent = createDoneEvent(beskjed.sistOppdatert)

        kafkaProducerWrapper.sendEvent(doneKey, doneEvent)
    }
}
