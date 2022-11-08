package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.kotest.assertions.throwables.shouldNotThrow

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class BeskjedSerializerTest {

    @Test
    fun `jsonSerialisering`(){
        val beskjed = BeskjedObjectMother.createBeskjed()
        shouldNotThrow<Exception> {
            Json { ignoreUnknownKeys = true }
            }

    }
}