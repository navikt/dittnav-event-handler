package no.nav.personbruker.dittnav.eventhandler.statistics

import kotlinx.serialization.Serializable

@Serializable
data class EventFrequencyDistribution(val eventFrequencies: List<NumberOfEventsFrequency>)

@Serializable
data class NumberOfEventsFrequency(val antallEventer: Int, val antallBrukere: Int)