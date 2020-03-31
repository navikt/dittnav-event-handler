package no.nav.personbruker.dittnav.eventhandler.health

import org.junit.jupiter.api.Test


class ProduceAttemptCounterTest {

    private val produceAttemptCounter: ProduceAttemptCounter = ProduceAttemptCounter(10, 10)

    @Test
    fun `skal varsle hvis antall feil er høyere enn maksimal prosentandel, 10%`() {
        produceAttemptCounter.success()
        produceAttemptCounter.failure()
        val notify = produceAttemptCounter.isUnhealthy()
        System.out.println(notify)
    }
}
