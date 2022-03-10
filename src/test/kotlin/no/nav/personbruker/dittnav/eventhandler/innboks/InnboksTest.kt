package no.nav.personbruker.dittnav.eventhandler.innboks

import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

class InnboksTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val innboks = InnboksObjectMother.createInnboks()
        val innboksAsString = innboks.toString()
        innboksAsString `should contain` "fodselsnummer=***"
        innboksAsString `should contain` "tekst=***"
        innboksAsString `should contain` "link=***"
        innboksAsString `should contain` "systembruker=x-dittnav"
    }
}
