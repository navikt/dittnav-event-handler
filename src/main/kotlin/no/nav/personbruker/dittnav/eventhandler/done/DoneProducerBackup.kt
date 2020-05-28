package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave

class DoneProducerBackup(private val kafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>) {

    fun produceDoneEventForSuppliedBeskjedEventList(events: List<Beskjed>) {
        events.forEach { event ->
            val key = createKeyForEvent(event.eventId, event.systembruker)
            val beskjedEvent = createDoneEvent(event.fodselsnummer, event.grupperingsId)
            kafkaProducer.sendEvent(key, beskjedEvent)
        }
    }

    fun produceDoneEventForSuppliedOppgaveEventList(events: List<Oppgave>) {

    }
}