package no.nav.personbruker.dittnav.eventhandler.varsel

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.VarselType
import no.nav.personbruker.dittnav.eventhandler.common.VarselType.BESKJED
import no.nav.personbruker.dittnav.eventhandler.common.VarselType.INNBOKS
import no.nav.personbruker.dittnav.eventhandler.common.VarselType.OPPGAVE
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createDoknotStatusBeskjed
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createDoknotStatusInnboks
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createDoknotStatusOppgave
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
            filter { it.aktiv }.subList(0, 2).forEach { createDoknotifikasjon(it.eventId, BESKJED) }
            filter { !it.aktiv }.subList(0, 3).forEach { createDoknotifikasjon(it.eventId, BESKJED) }
        }
        createOppgaver(1, 4).apply {
            filter { !it.aktiv }.subList(0, 3).forEach { createDoknotifikasjon(it.eventId, OPPGAVE) }
        }
        createInnboks(2, 1).apply {
            first { it.aktiv }.apply { createDoknotifikasjon(eventId, INNBOKS, "SMS") }
        }
    }

    @Test
    fun `henter aktive varsler`() = runBlocking {
        val aktiveVarsel = eventRepository.getActiveVarsel(fodselsnummer).map { it.toVarselDTO() }
        aktiveVarsel.size shouldBe 6
        aktiveVarsel.map { it.type }.toSet() shouldContainExactly alleVarselType
        aktiveVarsel.filter { it.eksternVarslingSendt }.size shouldBe 3
        aktiveVarsel.filter { it.eksternVarslingKanaler == listOf("SMS", "EPOST") }.size shouldBe 2
        aktiveVarsel.filter { it.eksternVarslingKanaler == listOf("SMS") }.size shouldBe 1
    }

    @Test
    fun `henter inaktive varsel`() = runBlocking {
        val inaktiveVarsel = eventRepository.getInactiveVarsel(fodselsnummer).map { it.toVarselDTO() }
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

    private fun createDoknotifikasjon(eventId: String, type: VarselType, kanaler: String = "SMS,EPOST") {
        runBlocking {
            database.dbQuery {
                when (type) {
                    OPPGAVE -> createDoknotStatusOppgave(
                        status = DoknotifikasjonTestStatus(
                            eventId = eventId,
                            status = EksternVarslingStatus.FERDIGSTILT.name,
                            melding = "Ekstern melding",
                            distribusjonsId = null,
                            kanaler = kanaler
                        )
                    )

                    BESKJED -> createDoknotStatusBeskjed(
                        status = DoknotifikasjonTestStatus(
                            eventId = eventId,
                            status = EksternVarslingStatus.FERDIGSTILT.name,
                            melding = "Ekstern melding",
                            distribusjonsId = null,
                            kanaler = kanaler
                        )
                    )

                    INNBOKS -> createDoknotStatusInnboks(
                        status = DoknotifikasjonTestStatus(
                            eventId = eventId,
                            status = EksternVarslingStatus.FERDIGSTILT.name,
                            melding = "Ekstern melding",
                            distribusjonsId = null,
                            kanaler = kanaler
                        )
                    )

                    else -> {
                        IllegalArgumentException("Kan ikke lage doknotifikasjon for varseltype $type")
                    }
                }
            }
        }
    }

}

