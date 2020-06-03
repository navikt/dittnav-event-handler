package no.nav.personbruker.dittnav.eventhandler.beskjed

class BackupBeskjedService(
        private val beskjedEventService: BeskjedEventService,
        private val beskjedProducer: BeskjedProducer
) {

    suspend fun produceBeskjedEventsForAllBeskjedEventsInCach() {
        val allBeskjedEvents = beskjedEventService.getAllBeskjedEventsInCach()
        if (allBeskjedEvents.isNotEmpty()) {
            beskjedProducer.produceAllBeskjedEventsFromList(allBeskjedEvents)
        }
    }

    suspend fun produceDoneEventsFromAllInactiveBeskjedEvents() {
        var allInactiveBeskjedEvents = beskjedEventService.getAllInactiveBeskjedEventsInCach()
        if (allInactiveBeskjedEvents.isNotEmpty()) {
            beskjedProducer.produceDoneEventFromInactiveBeskjedEvents(allInactiveBeskjedEvents)
        }
    }
}

