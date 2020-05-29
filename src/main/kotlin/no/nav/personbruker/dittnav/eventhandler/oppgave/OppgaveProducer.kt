package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.createDoneEvent
import no.nav.personbruker.dittnav.eventhandler.done.createKeyForEvent

class OppgaveProducer(
        private val oppgaveKafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Oppgave>,
        private val doneKafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>
) {

    fun produceAllOppgaveEventsFromList(events: List<Oppgave>) {
        events.forEach { event ->
            val key = createKeyForEvent(event.eventId, event.systembruker)
            val oppgaveEvent = createOppgaveEvent(event)
            oppgaveKafkaProducer.sendEvent(key, oppgaveEvent)
        }
    }

    fun produceDoneEventFromInactiveOppgaveEvents(events: List<Oppgave>) {
        events.forEach { event ->
            val key = createKeyForEvent(event.eventId, event.systembruker)
            val doneEvent = createDoneEvent(event.fodselsnummer, event.grupperingsId)
            doneKafkaProducer.sendEvent(key, doneEvent)
        }
    }
}