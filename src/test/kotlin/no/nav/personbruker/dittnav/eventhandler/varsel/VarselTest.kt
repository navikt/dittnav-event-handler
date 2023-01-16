package no.nav.personbruker.dittnav.eventhandler.varsel

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.assert
import no.nav.personbruker.dittnav.eventhandler.beskjed.Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.VarselType.BESKJED
import no.nav.personbruker.dittnav.eventhandler.common.VarselType.INNBOKS
import no.nav.personbruker.dittnav.eventhandler.common.VarselType.OPPGAVE
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.database.createDoknotifikasjon
import no.nav.personbruker.dittnav.eventhandler.innboks.Innboks
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VarselTest {
    private val database = LocalPostgresDatabase.cleanDb()
    private val eventRepository = VarselRepository(database)

    private val alleVarselType = setOf(
        BESKJED,
        OPPGAVE,
        INNBOKS
    )
    private val fodselsnummer = "12345678"

    @BeforeAll
    fun `populer testdata`() {
        createBeskjeder(3, 5).apply {
            filter { it.aktiv }.subList(0, 2).forEach { database.createDoknotifikasjon(it.eventId, BESKJED) }
            filter { !it.aktiv }.subList(0, 3).forEach { database.createDoknotifikasjon(it.eventId, BESKJED) }
        }
        createOppgaver(1, 4).apply {
            filter { !it.aktiv }.subList(0, 3).forEach { database.createDoknotifikasjon(it.eventId, OPPGAVE) }
        }
        createInnboks(2, 1).apply {
            first { it.aktiv }.apply { database.createDoknotifikasjon(eventId, INNBOKS, "SMS") }
        }
    }

    @Test
    fun `henter aktive varsler`() = runBlocking {
        val aktiveVarsel = eventRepository.getActiveVarsel(fodselsnummer)
        val aktiveVarselDTO = aktiveVarsel.map { it.toVarselDTO(4) }
        aktiveVarselDTO.size shouldBe 6
        aktiveVarselDTO.map { it.type }.toSet() shouldContainExactly alleVarselType
        aktiveVarselDTO.filter { it.eksternVarslingSendt }.size shouldBe 3
        aktiveVarselDTO.filter { it.eksternVarslingKanaler == listOf("SMS", "EPOST") }.size shouldBe 2
        aktiveVarselDTO.filter { it.eksternVarslingKanaler == listOf("SMS") }.size shouldBe 1

        val redactedVarsel = aktiveVarsel.map { it.toVarselDTO(3) }
        redactedVarsel.filter { it.sikkerhetsnivaa == 4 }.all { it.tekst == null } shouldBe true
        redactedVarsel.filter { it.sikkerhetsnivaa == 3 }.all { it.tekst != null } shouldBe true

    }

    @Test
    fun `henter inaktive varsel`() = runBlocking {
        val inaktiveVarsel = eventRepository.getInactiveVarsel(fodselsnummer).map { it.toVarselDTO(4) }
        inaktiveVarsel.size shouldBe 10
        inaktiveVarsel.map { it.type }.toSet() shouldContainExactly alleVarselType
        inaktiveVarsel.filter { it.eksternVarslingSendt }.size shouldBe 6
        inaktiveVarsel.filter { it.eksternVarslingKanaler == listOf("SMS", "EPOST") }.size shouldBe 6
    }


    private fun createBeskjeder(antallAktive: Int, antallInaktive: Int): List<Beskjed> {
        val beskjeder = (1..antallAktive).map {
            BeskjedObjectMother.createBeskjed(
                fodselsnummer = fodselsnummer,
                aktiv = true,
                sikkerhetsnivaa = 3
            )
        } + (1..antallInaktive).map {
            BeskjedObjectMother.createBeskjed(
                fodselsnummer = fodselsnummer,
                aktiv = false,
            )
        }

        runBlocking {
            database.dbQuery { createBeskjed(beskjeder) }
        }

        return beskjeder
    }

    private fun createOppgaver(antallAktive: Int, antallInaktive: Int): List<Oppgave> {
        val oppgaver = (1..antallAktive).map {
            OppgaveObjectMother.createOppgave(
                fodselsnummer = fodselsnummer,
                aktiv = true,
                fristUtløpt = null
            )
        } + (1..antallInaktive).map {
            OppgaveObjectMother.createOppgave(
                fodselsnummer = fodselsnummer,
                aktiv = false,
                fristUtløpt = null
            )
        }

        runBlocking {
            database.dbQuery { createOppgave(oppgaver) }
        }

        return oppgaver
    }

    private fun createInnboks(antallAktive: Int, antallInaktive: Int): List<Innboks> {
        val innboks = (1..antallAktive).map {
            InnboksObjectMother.createInnboks(
                fodselsnummer = fodselsnummer,
                aktiv = true
            )
        } + (1..antallInaktive).map {
            InnboksObjectMother.createInnboks(
                fodselsnummer = fodselsnummer,
                aktiv = false
            )
        }

        runBlocking {
            database.dbQuery { createInnboks(innboks) }
        }

        return innboks
    }
}