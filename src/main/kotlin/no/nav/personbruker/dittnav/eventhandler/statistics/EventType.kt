package no.nav.personbruker.dittnav.eventhandler.statistics

enum class EventType(val eventType: String) {
    OPPGAVE("oppgave"),
    BESKJED("beskjed"),
    INNBOKS("innboks"),
    DONE("done");
    companion object {
        fun fromOriginalType(value: String) = valueOf(value.uppercase())
    }

}
