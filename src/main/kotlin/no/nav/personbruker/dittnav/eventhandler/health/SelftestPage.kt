package no.nav.personbruker.dittnav.eventhandler.health

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.html.*
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.config.HttpClientBuilder
import java.net.URL

suspend fun ApplicationCall.pingDependencies(environment: Environment, database: Database) = coroutineScope {
    val client = HttpClientBuilder.build()

    val dittnavApiPingableURL = URL("${environment.dittnavApiURL}/internal/isAlive")

    val dittnavApiSelftestStatus = async { getStatus(dittnavApiPingableURL, client) }
    val dataSourceSelftestStatus = async { getDataSourceRunningStatus(database) }

    val services =
            mapOf(
                    "DITTNAV_API:" to dittnavApiSelftestStatus.await(),
                    "DATABASE:" to dataSourceSelftestStatus.await()
            )

    client.close()

    val serviceStatus = if (services.values.any { it.status == Status.ERROR }) Status.ERROR else Status.OK

    respondHtml {
        head {
            title { +"Selftest dittnav-event-handler" }
        }
        body {
            h1 {
                style = if (serviceStatus == Status.OK) "background: green" else "background: red;font-weight:bold"
                +"Service status: $serviceStatus"
            }
            table {
                thead {
                    tr { th { +"SELFTEST DITTNAV-EVENT-HANDLER" } }
                }
                tbody {
                    services.map {
                        tr {
                            td { +it.key }
                            td { +it.value.pingedURL.let { url -> url?.toString() ?: "" } }
                            td {
                                style = if (it.value.status == Status.OK) "background: green" else "background: red;font-weight:bold"
                                +it.value.status.toString()
                            }
                            td { +it.value.statusMessage }
                        }
                    }
                }
            }
        }
    }
}
