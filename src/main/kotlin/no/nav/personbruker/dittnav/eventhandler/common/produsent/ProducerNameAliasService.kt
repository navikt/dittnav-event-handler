package no.nav.personbruker.dittnav.eventhandler.common.produsent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.validation.validateNonNullFieldMaxLength
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime

class ProducerNameAliasService(private val database: Database) {

    private var producerNameAliases: Map<String, String> = emptyMap()
    private var lastRetrievedFromDB: LocalDateTime? = null
    private val CHECK_PRODUCERNAME_CACHE_IN_MINUTES = 15

    private val log = LoggerFactory.getLogger(ProducerNameAliasService::class.java)

    suspend fun getProducerNameAlias(systemUser: String?): String {
        val systembruker = validateNonNullFieldMaxLength(systemUser, "systembruker", 100)
        updateCacheIfNeeded(systembruker)
        var alias = producerNameAliases[systembruker]

        if (alias.isNullOrBlank()) {
            log.warn("Alias for '$systembruker' ble verken funnet i produsent-cache eller databasen, mangler det en mapping?")
            alias = ""
        }
        return alias
    }

    private suspend fun updateCacheIfNeeded(systembruker: String) {
        if (shouldFetchNewValuesFromDB()) {
            log.info("Periodisk oppdatering av produsent-cache.")
            updateCache()

        } else {
            val containsAlias = producerNameAliases.containsKey(systembruker)
            if (!containsAlias) {
                log.info("Manglet alias for '$systembruker', forsøker å oppdatere produsent-cache på nytt.")
                updateCache()
            }
        }
    }

    private fun shouldFetchNewValuesFromDB(): Boolean {
        return producerNameAliases.isEmpty() ||
                lastRetrievedFromDB == null ||
                Math.abs(Duration.between(lastRetrievedFromDB, LocalDateTime.now()).toMinutes()) > CHECK_PRODUCERNAME_CACHE_IN_MINUTES
    }

    private suspend fun updateCache() = withContext(Dispatchers.IO) {
        producerNameAliases = populateProducerNameCache()
        lastRetrievedFromDB = LocalDateTime.now()
    }

    private suspend fun populateProducerNameCache(): Map<String, String> {
        return try {
            val producers = database.queryWithExceptionTranslation { getProdusent() }
            producers.map { it.systembruker to it.produsentnavn }.toMap()

        } catch (e: Exception) {
            log.error("En feil oppstod ved henting av produsentnavn, har ikke oppdatert cache med verdier fra DB.", e)
            producerNameAliases
        }
    }

}