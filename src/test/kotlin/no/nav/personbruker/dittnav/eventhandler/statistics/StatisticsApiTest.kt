package no.nav.personbruker.dittnav.eventhandler.statistics

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationBuilder
import io.ktor.server.testing.testApplication
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.personbruker.dittnav.eventhandler.beskjed.Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.EventType
import no.nav.personbruker.dittnav.eventhandler.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.innboks.Innboks
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.innboks.deleteInnboks
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.deleteOppgave
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.lang.AssertionError
import kotlin.math.max
import kotlin.math.min

private val objectMapper = ObjectMapper()

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatisticsApitest {
    private val baseUrl = "/dittnav-event-handler"
    private val database = LocalPostgresDatabase.cleanDb()
    private val eventStatisticsService = EventStatisticsService(database)
    private val statistickServiceMock = mockk<EventStatisticsService>()

    @BeforeAll
    fun populate() {
        database.createBeskjed(TestBruker1.beskjeder + TestBruker2.beskjeder + TestBruker3.beskjeder)
        database.createInnboks(TestBruker1.innbokser + TestBruker2.innbokser + TestBruker3.innbokser)
        database.createOppgave(TestBruker1.oppgaver + TestBruker2.oppgaver + TestBruker3.oppgaver)
    }

    @AfterAll
    fun cleanData() {
        database.deleteBeskjed(TestBruker1.beskjeder + TestBruker2.beskjeder)
        database.deleteInnboks(TestBruker1.innbokser + TestBruker2.innbokser)
        database.deleteOppgave(TestBruker1.oppgaver + TestBruker2.oppgaver)
    }

    @Test
    fun `varsel grupert på bruker og type`() {
        val groupedBrukerEndoint = "$baseUrl/stats/grouped/bruker"
        val beskjederPrBruker =
            intArrayOf(TestBruker1.beskjeder.size, TestBruker2.beskjeder.size, TestBruker3.beskjeder.size)
        val innbokserPrBruker =
            intArrayOf(TestBruker1.innbokser.size, TestBruker2.innbokser.size)
        val oppgaverPrBruker =
            intArrayOf(TestBruker1.oppgaver.size, TestBruker2.oppgaver.size)
        val totaltAntallVarslerPrBruker =
            intArrayOf(
                TestBruker1.totaltAntallVarsler,
                TestBruker2.totaltAntallVarsler,
                TestBruker3.totaltAntallVarsler
            )
        testApplication {
            mockApiWithAzureAuth()

            client.get("$groupedBrukerEndoint/beskjed").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    body = response.bodyAsText(),
                    max = maxCount(*beskjederPrBruker),
                    min = minCount(*beskjederPrBruker),
                    avg = avgCount(*beskjederPrBruker),
                    details = "beskjed"
                )
            }
            client.get("$groupedBrukerEndoint/oppgave").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    body = response.bodyAsText(),
                    max = maxCount(*oppgaverPrBruker),
                    min = minCount(*oppgaverPrBruker),
                    avg = avgCount(*oppgaverPrBruker),
                    details = "oppgave"
                )
            }
            client.get("$groupedBrukerEndoint/innboks").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    body = response.bodyAsText(),
                    max = maxCount(*innbokserPrBruker),
                    min = minCount(*innbokserPrBruker),
                    avg = avgCount(*innbokserPrBruker),
                )
            }
            client.get(groupedBrukerEndoint).also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    body = response.bodyAsText(),
                    max = maxCount(*totaltAntallVarslerPrBruker),
                    min = minCount(*totaltAntallVarslerPrBruker),
                    avg = avgCount(*totaltAntallVarslerPrBruker),
                    details = "alle"
                )
            }

        }
    }

    @Test
    fun `varsel grupert på bruker,type og aktiv status`() {
        val groupedBrukerAktivEndoint = "$baseUrl/stats/grouped/bruker/active"
        testApplication {
            mockApiWithAzureAuth()
            client.get("$groupedBrukerAktivEndoint/beskjed").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    body = response.bodyAsText(),
                    max = maxCount(
                        TestBruker1.antallAktiveBeskjeder,
                        TestBruker2.antallAktiveBeskjeder,
                        TestBruker3.antallAktiveBeskjeder
                    ),
                    min = minCount(
                        TestBruker1.antallAktiveBeskjeder,
                        TestBruker2.antallAktiveBeskjeder,
                        TestBruker3.antallAktiveBeskjeder
                    ),
                    avg = avgCount(
                        TestBruker1.antallAktiveBeskjeder,
                        TestBruker2.antallAktiveBeskjeder,
                        TestBruker3.antallAktiveBeskjeder
                    ),
                    details = "beskjed"

                )
            }
            client.get("$groupedBrukerAktivEndoint/oppgave").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    body = response.bodyAsText(),
                    max = max(TestBruker1.antallAktiveOppgaver, TestBruker2.antallAktiveOppgaver),
                    min = min(TestBruker1.antallAktiveOppgaver, TestBruker2.antallAktiveOppgaver),
                    avg = avgCount(TestBruker1.antallAktiveOppgaver, TestBruker2.antallAktiveOppgaver),
                    details = "oppgave"
                )
            }
            client.get("$groupedBrukerAktivEndoint/innboks").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    body = response.bodyAsText(),
                    max = max(TestBruker1.antallAktiveInnbokser, TestBruker2.antallAktiveInnbokser),
                    min = min(TestBruker1.antallAktiveInnbokser, TestBruker2.antallAktiveInnbokser),
                    avg = avgCount(TestBruker1.antallAktiveInnbokser, TestBruker2.antallAktiveInnbokser),
                    details = "innboks"
                )
            }
            client.get(groupedBrukerAktivEndoint).also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    body = response.bodyAsText(),
                    max = maxCount(
                        TestBruker1.totaltAntallAktive,
                        TestBruker2.totaltAntallAktive,
                        TestBruker3.totaltAntallAktive
                    ),
                    min = minCount(
                        TestBruker1.totaltAntallAktive,
                        TestBruker2.totaltAntallAktive,
                        TestBruker3.totaltAntallAktive
                    ),
                    avg = avgCount(
                        TestBruker1.totaltAntallAktive,
                        TestBruker2.totaltAntallAktive,
                        TestBruker3.totaltAntallAktive
                    ),
                    details = "alle"
                )
            }

        }
        //val expectedBeskjed =
        //stats/grouped/bruker/grupperings
        //stats/grouped/bruker/grupperings/{type}
    }

    @Test
    fun `active-rate grupert på bruker og type`() {
        val rateEndpoint = "$baseUrl/stats/grouped/bruker/active-rate"
        coEvery {
            statistickServiceMock.getActiveRateEventsStatisticsPerUser(EventType.BESKJED)
        } returns mockDecimal(2.75, 1.0, 1.5)
        coEvery {
            statistickServiceMock.getActiveRateEventsStatisticsPerUser(EventType.OPPGAVE)
        } returns mockDecimal(3.22, 1.7, 4.56)
        coEvery {
            statistickServiceMock.getActiveRateEventsStatisticsPerUser(EventType.INNBOKS)
        } returns mockDecimal(10.75, 1.11, 5.3)
        coEvery {
            statistickServiceMock.getTotalActiveRateEventsStatisticsPerUser()
        } returns mockDecimal(5.75, 1.61, 7.3)


        testApplication {
            mockServiceAndApiWithAzureAuth(statistickServiceMock)
            client.get("$rateEndpoint/beskjed").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertDecimalStat(response.bodyAsText(), max = 2.75, min = 1.0, avg = 1.5, details = "beskjed")
            }
            client.get("$rateEndpoint/oppgave").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertDecimalStat(response.bodyAsText(), max = 3.22, min = 1.7, avg = 4.56, details = "oppgave")
            }
            client.get("$rateEndpoint/innboks").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertDecimalStat(response.bodyAsText(), max = 10.75, min = 1.11, avg = 5.3, details = "innboks")
            }
            client.get(rateEndpoint).also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertDecimalStat(response.bodyAsText(), max = 5.75, min = 1.61, avg = 7.3, details = "alle")
            }
        }

        clearMocks(statistickServiceMock)
    }

    @Test
    fun `Tekstlengde pr type`() {
        testApplication {
            val beskjedTekster =
                TestBruker1.beskjedTekster() + TestBruker2.beskjedTekster() + TestBruker3.beskjedTekster()
            val oppgaveTekster =
                TestBruker1.oppgaveTekster() + TestBruker2.oppgaveTekster() + TestBruker3.oppgaveTekster()
            val innboksTekster =
                TestBruker1.innboksTeskter() + TestBruker2.innboksTeskter() + TestBruker3.innboksTeskter()
            val alleTekster = beskjedTekster + oppgaveTekster + innboksTekster

            mockApiWithAzureAuth()
            client.get("$baseUrl/stats/text-length/beskjed").also { response ->
                response.status shouldBe HttpStatusCode.OK

                assertIntegerStat(
                    response.bodyAsText(),
                    max = maxTextLength(beskjedTekster),
                    min = minTextLength(beskjedTekster),
                    avg = avgTextLenght(beskjedTekster),
                    details = "beskjed"
                )
            }
            client.get("$baseUrl/stats/text-length/oppgave").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    response.bodyAsText(),
                    max = maxTextLength(oppgaveTekster),
                    min = minTextLength(oppgaveTekster),
                    avg = avgTextLenght(oppgaveTekster),
                    details = "oppgave"
                )
            }

            client.get("$baseUrl/stats/text-length/innboks").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    response.bodyAsText(),
                    max = maxTextLength(innboksTekster),
                    min = minTextLength(innboksTekster),
                    avg = avgTextLenght(innboksTekster),
                    details = "innboks"
                )
            }

            client.get("$baseUrl/stats/text-length").also { response ->
                response.status shouldBe HttpStatusCode.OK
                assertIntegerStat(
                    response.bodyAsText(),
                    max = maxTextLength(alleTekster),
                    min = minTextLength(alleTekster),
                    avg = avgTextLenght(alleTekster),
                    details = "alle"
                )
            }
        }
        //stats/text-length/{type}
        //stats/text-length
    }

    @Test
    fun `antall brukere pr type`() {
        testApplication {
            mockApiWithAzureAuth()
            client.get("$baseUrl/stats/bruker-count/beskjed").also { response ->
                response.status shouldBe HttpStatusCode.OK
                objectMapper.readTree(response.bodyAsText())["count"].asInt() shouldBe 3

            }
            client.get("$baseUrl/stats/bruker-count/oppgave").also { response ->
                response.status shouldBe HttpStatusCode.OK
                objectMapper.readTree(response.bodyAsText())["count"].asInt() shouldBe 2
            }

            client.get("$baseUrl/stats/bruker-count/innboks").also { response ->
                response.status shouldBe HttpStatusCode.OK
                objectMapper.readTree(response.bodyAsText())["count"].asInt() shouldBe 2
            }

            client.get("$baseUrl/stats/bruker-count").also { response ->
                response.status shouldBe HttpStatusCode.OK
                objectMapper.readTree(response.bodyAsText())["count"].asInt() shouldBe 3
            }
        }

    }

    @Test
    fun `count pr type og aktiv-status`() {
        testApplication {
            mockApiWithAzureAuth()
            client.get("$baseUrl/stats/count").also { response ->
                response.status shouldBe HttpStatusCode.OK
                val totaltAntallVarsler =
                    TestBruker1.totaltAntallVarsler + TestBruker2.totaltAntallVarsler + TestBruker3.totaltAntallVarsler
                objectMapper.readTree(response.bodyAsText())["count"].asInt() shouldBe totaltAntallVarsler
            }
            client.get("$baseUrl/stats/count/active").also { response ->
                val totaltAntallAktiveVarsler =
                    TestBruker1.totaltAntallAktive + TestBruker2.totaltAntallAktive + TestBruker3.totaltAntallAktive
                response.status shouldBe HttpStatusCode.OK
                objectMapper.readTree(response.bodyAsText())["count"].asInt() shouldBe totaltAntallAktiveVarsler
            }
            client.get("$baseUrl/stats/count/active/beskjed").also { response ->
                val totaltAntallAktiveBeskjeder =
                    TestBruker1.antallAktiveBeskjeder + TestBruker2.antallAktiveBeskjeder + TestBruker3.antallAktiveBeskjeder
                response.status shouldBe HttpStatusCode.OK
                objectMapper.readTree(response.bodyAsText())["count"].asInt() shouldBe totaltAntallAktiveBeskjeder
            }
            client.get("$baseUrl/stats/count/active/oppgave").also { response ->
                val totaltAntallAktiveOppgaver =
                    TestBruker1.antallAktiveOppgaver + TestBruker2.antallAktiveOppgaver + TestBruker3.antallAktiveOppgaver
                response.status shouldBe HttpStatusCode.OK
                objectMapper.readTree(response.bodyAsText())["count"].asInt() shouldBe totaltAntallAktiveOppgaver
            }
            client.get("$baseUrl/stats/count/active/innboks").also { response ->
                val totaltAntallAktiveInnbokser =
                    TestBruker1.antallAktiveInnbokser + TestBruker2.antallAktiveInnbokser + TestBruker3.antallAktiveInnbokser
                response.status shouldBe HttpStatusCode.OK
                objectMapper.readTree(response.bodyAsText())["count"].asInt() shouldBe totaltAntallAktiveInnbokser
            }
        }
        //stats/count
        //stats/count/active/{type}
        //stats/count/active
    }

    @Test
    fun `Varsel frekvens distribusjon`() {
        coEvery {
            statistickServiceMock.getActiveEventsFrequencyDistribution(EventType.BESKJED)
        } returns EventFrequencyDistribution(
            listOf(NumberOfEventsFrequency(antallEventer = 32, antallBrukere = 3))
        )
        coEvery {
            statistickServiceMock.getActiveEventsFrequencyDistribution(EventType.OPPGAVE)
        } returns EventFrequencyDistribution(
            listOf(
                NumberOfEventsFrequency(antallEventer = 78, antallBrukere = 50),
                NumberOfEventsFrequency(antallEventer = 45, antallBrukere = 34)
            )
        )
        coEvery { statistickServiceMock.getActiveEventsFrequencyDistribution(EventType.INNBOKS) } returns EventFrequencyDistribution(
            listOf()
        )

        testApplication {
            mockServiceAndApiWithAzureAuth(statistickServiceMock)
            client.get("$baseUrl/stats/frequency-distribution/active/beskjed").also { response ->
                response.status shouldBe HttpStatusCode.OK
                val result = objectMapper.readTree(response.bodyAsText())["eventFrequencies"].toList()
                result.size shouldBe 1
                assertEventFrequenciesContains(
                    result = result,
                    antallEventer = 32,
                    antallBrukere = 3,
                    details = "beskjed"
                )
            }
            client.get("$baseUrl/stats/frequency-distribution/active/oppgave").also { response ->
                response.status shouldBe HttpStatusCode.OK
                val result = objectMapper.readTree(response.bodyAsText())["eventFrequencies"].toList()
                result.size shouldBe 2
                assertEventFrequenciesContains(
                    result = result,
                    antallEventer = 78,
                    antallBrukere = 50,
                    details = "oppgave"
                )
                assertEventFrequenciesContains(
                    result = result,
                    antallEventer = 45,
                    antallBrukere = 34,
                    details = "oppgave"
                )
            }
            client.get("$baseUrl/stats/frequency-distribution/active/innboks").also { response ->
                response.status shouldBe HttpStatusCode.OK
                objectMapper.readTree(response.bodyAsText())["eventFrequencies"].toList().size shouldBe 0
            }
        }

        clearMocks(statistickServiceMock)
    }

    private fun assertEventFrequenciesContains(
        result: List<JsonNode>,
        antallEventer: Int,
        antallBrukere: Int,
        details: String = ""
    ) =
        result.find { jsonNode -> jsonNode["antallEventer"].asInt() == antallEventer && jsonNode["antallBrukere"].asInt() == antallBrukere }
            ?: throw AssertionError("$details inneholder ikke element med antallEvents = $antallEventer og antallBrukere = $antallBrukere")


    private fun TestApplicationBuilder.mockApiWithAzureAuth() {
        mockEventHandlerApi(
            eventStatisticsService = eventStatisticsService,
            installAuthenticatorsFunction = {
                installMockedAuthenticators {
                    installAzureAuthMock {
                        setAsDefault = false
                        alwaysAuthenticated = true
                    }
                }
            }
        )

    }

    private fun TestApplicationBuilder.mockServiceAndApiWithAzureAuth(eventStatisticsService: EventStatisticsService) {
        mockEventHandlerApi(
            eventStatisticsService = eventStatisticsService,
            installAuthenticatorsFunction = {
                installMockedAuthenticators {
                    installAzureAuthMock {
                        setAsDefault = false
                        alwaysAuthenticated = true
                    }
                }
            }
        )

    }

}

private fun mockDecimal(max: Double, min: Double, avg: Double): DecimalMeasurement = DecimalMeasurement(
    min = min,
    max = max,
    avg = avg,
    percentile25 = 0.0,
    percentile50 = 0.0,
    percentile75 = 0.0,
    percentile90 = 0.0,
    percentile99 = 0.0
)

private fun assertIntegerStat(body: String, max: Int, min: Int, avg: Double, details: String = "Integer assertion") {
    val result = objectMapper.readTree(body)
    requireNotNull(result)
    assertEquals(max, result["max"].asInt(), "$details: max-count")
    assertEquals(min, result["min"].asInt(), "$details: min-count")
    assertEquals(avg, result["avg"].asDouble(), "$details: average")
}

private fun assertDecimalStat(
    body: String,
    max: Double,
    min: Double,
    avg: Double,
    details: String = "Double assertion"
) {
    val result = objectMapper.readTree(body)
    requireNotNull(result)
    assertEquals(max, result["max"].asDouble(), "$details max-count")
    assertEquals(min, result["min"].asDouble(), "$details min-count")
    assertEquals(avg, result["avg"].asDouble(), "$details average")
}

private fun avgCount(vararg x: Int) = x.sumOf { it.toDouble() } / x.size.toDouble()
private fun maxCount(vararg x: Int) = x.toList().maxOf { it }
private fun minCount(vararg x: Int) = x.toList().minOf { it }
private fun maxTextLength(strings: List<String>): Int =
    strings.stream().max(Comparator.comparingInt(String::length)).get().length

private fun minTextLength(strings: List<String>): Int =
    strings.stream().min(Comparator.comparingInt(String::length)).get().length

private fun avgTextLenght(strings: List<String>): Double = strings.sumOf { it.length }.toDouble() / strings.size

private interface TestBruker {
    val fodselsnummer: String
    val beskjeder: List<Beskjed>
    val oppgaver: List<Oppgave>
    val innbokser: List<Innboks>
    val totaltAntallVarsler: Int
        get() = beskjeder.size + oppgaver.size + innbokser.size
    val antallAktiveBeskjeder: Int
        get() = beskjeder.filter { it.aktiv }.size
    val antallAktiveOppgaver: Int
        get() = oppgaver.filter { it.aktiv }.size
    val antallAktiveInnbokser: Int
        get() = innbokser.filter { it.aktiv }.size
    val totaltAntallAktive: Int
        get() = antallAktiveBeskjeder + antallAktiveOppgaver + antallAktiveInnbokser

    fun beskjedTekster() = beskjeder.map { it.tekst }
    fun oppgaveTekster() = oppgaver.map { it.tekst }
    fun innboksTeskter() = innbokser.map { it.tekst }

}

private object TestBruker1 : TestBruker {

    override val fodselsnummer = "12345"

    override val beskjeder = listOf(
        createBeskjed(fodselsnummer = fodselsnummer),
        createBeskjed(fodselsnummer = fodselsnummer, tekst = "Kort tekst"),
        createBeskjed(fodselsnummer = fodselsnummer, tekst = "tekst fordi gøy eller noe"),
    )

    override val oppgaver = listOf(
        OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer),
        OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer),
        OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer),
        OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer),
        OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer, tekst = "Laaaaaaaaaaaaaang tekst"),
    )

    override val innbokser = listOf(
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer),
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer),
        InnboksObjectMother.createInnboks(
            fodselsnummer = fodselsnummer,
            tekst = "legger til litt her også da, sånn at det ikke blir helt likt"
        ),
    )
}


private object TestBruker2 : TestBruker {

    override val fodselsnummer = "99999"

    override val beskjeder = listOf(
        createBeskjed(fodselsnummer = fodselsnummer),
    )

    override val oppgaver = listOf(
        OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer),
        OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer),
        OppgaveObjectMother.createOppgave(
            fodselsnummer = fodselsnummer,
            aktiv = false,
            tekst = "Og tekst på inaktive saker, regner med det teller"
        )
    )

    override val innbokser = listOf(
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer),
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer, aktiv = false),
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer, aktiv = false),
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer, aktiv = false),
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer, aktiv = false),
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer, aktiv = false),
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer, aktiv = false),
        InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer, aktiv = false),
    )
}

private object TestBruker3 : TestBruker {

    override val fodselsnummer = "99998"

    override val beskjeder = listOf(
        createBeskjed(fodselsnummer = fodselsnummer, tekst = "Tekst fordi tekst er tekst"),
    )
    override val oppgaver: List<Oppgave> = listOf()
    override val innbokser: List<Innboks> = listOf()

}
