package no.nav.personbruker.dittnav.eventhandler.common.database

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class resultSetUtilKtTest {

    @Test
    fun `should convert invalid dates`() {
        val invalidTimestamp: Long = 1584658800
        val rawTimestamp = Timestamp(invalidTimestamp)

        val resultat = convertFromEpochSecondsToEpochMillis(rawTimestamp)

        resultat.year `should be equal to` 2020
    }

    @Test
    fun `should not convert valid dates`() {
        val ldt = LocalDateTime.of(2020, 4, 10, 8, 0).toInstant(ZoneOffset.UTC)
        val rawTimestamp = Timestamp(ldt.toEpochMilli())

        val resultat = convertFromEpochSecondsToEpochMillis(rawTimestamp)

        resultat.year `should be equal to` 2020
    }

}
