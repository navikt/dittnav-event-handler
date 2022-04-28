package no.nav.personbruker.dittnav.eventhandler.common.database

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class resultSetUtilKtTest {

    @Test
    fun `should convert invalid dates`() {
        val dateIn1970AsLong: Long = 1584658800
        val timestampFor1970 = Timestamp(dateIn1970AsLong)

        val resultat = convertIfUnlikelyDate(timestampFor1970)

        resultat.year shouldBe 2020
    }

    @Test
    fun `should not convert valid dates`() {
        val dateIn2020 = LocalDateTime.of(2020, 4, 10, 8, 0).toInstant(ZoneOffset.UTC)
        val timestampIn2020 = Timestamp(dateIn2020.toEpochMilli())

        val resultat = convertIfUnlikelyDate(timestampIn2020)

        resultat.year shouldBe 2020
    }

}
