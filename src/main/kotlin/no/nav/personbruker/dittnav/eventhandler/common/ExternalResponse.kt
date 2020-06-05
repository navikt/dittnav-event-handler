package no.nav.personbruker.dittnav.eventhandler.common

data class ExternalResponse(val dryRun: String)

fun isDryrun(dryRun: String): Boolean {
    return dryRun != "false"
}