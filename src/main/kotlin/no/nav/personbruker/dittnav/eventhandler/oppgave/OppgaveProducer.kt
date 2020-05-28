package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.createKeyForEvent

class OppgaveProducer(private val kafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Oppgave>) {

    fun produceAllOppgaveEventsFromList(events: List<Oppgave>) {
        events.forEach { event ->
            val key = createKeyForEvent(event.eventId, event.systembruker)
            val oppgaveEvent = createOppgaveEvent(event)
            kafkaProducer.sendEvent(key, oppgaveEvent)
        }
    }
}