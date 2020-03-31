package no.nav.personbruker.dittnav.eventhandler.health

import java.util.*

class ProduceAttemptCounter(private var attemptsToKeep: Int, private var maxFailedPercentage: Int) {

    private val attempts: Queue<Boolean> = LinkedList()

    fun success() {
        attempts.add(true)
        removeIfNecessary()
    }

    fun failure() {
        attempts.add(false)
        removeIfNecessary()
    }

    fun isUnhealthy(): Boolean {
        val failedAttempts =  attempts.count { a -> !a }
        val failedPercentage = failedAttempts.toDouble() / attemptsToKeep * 100
        return failedPercentage > maxFailedPercentage
    }

    private fun removeIfNecessary() {
        if(attempts.size == attemptsToKeep) {
            attempts.remove()
        }
    }
}
