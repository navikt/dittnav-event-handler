package no.nav.personbruker.dittnav.eventhandler.health

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.coroutines.coroutineScope
import kotlinx.html.*
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthStatus
import no.nav.personbruker.dittnav.eventhandler.common.health.Status
import no.nav.personbruker.dittnav.eventhandler.config.ApplicationContext

suspend fun ApplicationCall.pingDependencies(appContext: ApplicationContext) = coroutineScope {

    val healthChecks: List<HealthStatus> = listOf(appContext.database.status(), appContext.doneProducer.status())
    val hasFailedChecks = healthChecks.any { healthStatus -> Status.ERROR == healthStatus.status }

    respondHtml {
        head {
            title { +"Selftest dittnav-event-handler" }
        }
        body {
            var text = "Service-status: OK"
            var backgroundStyle = "background: green"
            if(hasFailedChecks) {
                text = "FEIL"
                backgroundStyle = "background: red;font-weight:bold"
            }
            h1 {
                style = backgroundStyle
                +text
            }
            table {
                thead {
                    tr { th { +"SELFTEST DITTNAV-EVENT-HANDLER" } }
                }
                tbody {
                    healthChecks.map {
                        tr {
                            td { +it.serviceName }
                            td {
                                style = if (it.status == Status.OK) "background: green" else "background: red;font-weight:bold"
                                +it.status.toString()
                            }
                            td { +it.statusMessage }
                        }
                    }
                }
            }
        }
    }
}
