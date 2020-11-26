package no.nav.personbruker.dittnav.eventhandler.common.produsent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.validation.validateNonNullFieldMaxLength
import org.slf4j.LoggerFactory

class ProducerNameAliasService(private val database: Database) {

    private var producerNameAliases: Map<String, String> = emptyMap()
    private val log = LoggerFactory.getLogger(ProducerNameAliasService::class.java)

    suspend fun getProducerNameAlias(systemUser: String?): String {
        val systembruker = validateNonNullFieldMaxLength(systemUser, "systembruker", 100)
        updateProducerNameAliases()
        var alias = producerNameAliases[systembruker]

        if (alias.isNullOrBlank()) {
            log.warn("Alias for '$systembruker' ble ikke funnet i databasen, mangler det en mapping?")
            alias = ""
        }
        return alias
    }

    private suspend fun updateProducerNameAliases() = withContext(Dispatchers.IO) {
        producerNameAliases = getProducerNameAliasesFromDB()
    }

    private suspend fun getProducerNameAliasesFromDB(): Map<String, String> {
        return try {
            val producers = database.queryWithExceptionTranslation { getProdusent() }
            producers.map { it.systembruker to it.produsentnavn }.toMap()

        } catch (e: Exception) {
            log.error("En feil oppstod ved henting av produsentnavn, har ikke oppdatert cache med verdier fra DB.", e)
            producerNameAliases
        }
    }

}