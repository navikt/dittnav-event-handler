package no.nav.personbruker.dittnav.eventhandler.logging

import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class MaskedLoggingEventTest {
    @Test
    fun `sjekk at fodselsnummer blir maskert`() {
        MaskedLoggingEvent.mask("-12345678901")!! `should contain` MASKED_FNR
        MaskedLoggingEvent.mask("12345678901")!! `should contain` MASKED_FNR
        MaskedLoggingEvent.mask(" 12345678901")!! `should contain` MASKED_FNR
        MaskedLoggingEvent.mask("12345678901 ")!! `should contain` MASKED_FNR
        MaskedLoggingEvent.mask(" 12345678901 ")!! `should contain` MASKED_FNR
        MaskedLoggingEvent.mask("abc 12345678901 def")!! `should contain` MASKED_FNR
        MaskedLoggingEvent.mask("callId=7b7c<12345676543>c8c32129c837808f7")!! `should contain` MASKED_FNR
    }

    @Test
    fun `sjekk at data som ikke er fodselsnummer ikke blir blir maskert`() {
        "".let { MaskedLoggingEvent.mask(it)!! `should equal` it }
        "abc".let { MaskedLoggingEvent.mask(it)!! `should equal` it }
        "1234".let { MaskedLoggingEvent.mask(it)!! `should equal` it }
        "1234567890".let { MaskedLoggingEvent.mask(it)!! `should equal` it }
        "123456789012".let { MaskedLoggingEvent.mask(it)!! `should equal` it }
        "callId=7b7c12345676543c8c32129c837808f7".let { MaskedLoggingEvent.mask(it)!! `should equal` it }
    }

    @Test
    fun `sjekk at maskering av null blir null`() {
        MaskedLoggingEvent.mask(null) `should equal` null
    }

    @Test
    fun `sjekk at maskert data formatteres riktig`() {
        MaskedLoggingEvent.mask("12345678901-12345678901 12345678901")!! `should equal` "$MASKED_FNR-$MASKED_FNR $MASKED_FNR"
        MaskedLoggingEvent.mask("12345678901,12345678901")!! `should equal` "$MASKED_FNR,$MASKED_FNR"
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MaskedLoggingEventTest::class.java)
        const val MASKED_FNR = "***********"
    }
}
