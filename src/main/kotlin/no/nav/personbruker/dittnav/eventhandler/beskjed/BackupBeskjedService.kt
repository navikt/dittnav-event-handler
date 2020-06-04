package no.nav.personbruker.dittnav.eventhandler.beskjed

class BackupBeskjedService(
        private val beskjedEventService: BeskjedEventService,
        private val beskjedProducer: BeskjedProducer
) {

    suspend fun produceBeskjedEventsForAllBeskjedEventsInCach() {
        val allBeskjedEvents = beskjedEventService.getAllBeskjedEventsInCach()
        if (allBeskjedEvents.isNotEmpty()) {
            val beskjedEvents = beskjedProducer.toSchemasBeskjed(allBeskjedEvents)
            beskjedProducer.produceAllBeskjedEvents(beskjedEvents)
        }
    }

    suspend fun produceDoneEventsFromAllInactiveBeskjedEvents() {
        var allInactiveBeskjedEvents = beskjedEventService.getAllInactiveBeskjedEventsInCach()
        if (allInactiveBeskjedEvents.isNotEmpty()) {
            val doneEvents = beskjedProducer.toSchemasDone(allInactiveBeskjedEvents)
            beskjedProducer.produceDoneEvents(doneEvents)
        }
    }
}

