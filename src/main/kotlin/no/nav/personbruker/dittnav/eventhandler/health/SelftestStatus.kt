package no.nav.personbruker.dittnav.eventhandler.health


import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.doneTopicName
import no.nav.personbruker.dittnav.eventhandler.done.DoneProducer
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.AuthenticationException
import org.apache.kafka.common.errors.TimeoutException
import org.slf4j.LoggerFactory
import java.sql.SQLException


enum class Status {
    OK, ERROR
}

data class SelftestStatus(val status: Status, val statusMessage: String)

private val log = LoggerFactory.getLogger(SelftestStatus::class.java)

suspend fun getDatasourceConnectionStatus(database: Database): SelftestStatus {
    return try {
        database.dbQuery { prepareStatement("""SELECT 1""").execute() }
        SelftestStatus(Status.OK, "200 OK")
    } catch (e: SQLException) {
        log.error("Vi har ikke tilgang til databasen.", e)
        SelftestStatus(Status.ERROR, "Feil mot DB")
    } catch (e: Exception) {
        log.error("Vi får en uventet feil mot databasen.", e)
        SelftestStatus(Status.ERROR, "Feil mot DB")
    }
}

fun getKafkaHealthStatus(doneProducer: DoneProducer): SelftestStatus {
    return try {
        doneProducer.getKafkaStatus()
        SelftestStatus(Status.OK, "200 OK")
    } catch (e: AuthenticationException) {
        log.error("SelftestStatus klarte ikke å autentisere seg mot Kafka. TopicName: ${doneTopicName}", e)
        SelftestStatus(Status.ERROR, "Feil mot Kafka")
    } catch (e: TimeoutException) {
        log.error("Noe gikk galt, vi fikk timeout mot Kafka. TopicName: ${doneTopicName}", e)
        SelftestStatus(Status.ERROR, "Feil mot Kafka")
    } catch (e: KafkaException) {
        log.error("Fikk en uventet feil mot Kafka. TopicName: ${doneTopicName}", e)
        SelftestStatus(Status.ERROR, "Feil mot Kafka")
    }
}


