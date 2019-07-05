package no.nav.personbruker.dittnav.eventhandler.kafka

import no.nav.personbruker.dittnav.event.schemas.Informasjon
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.informasjonTopicName
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.time.Instant

object Producer {

    val log = LoggerFactory.getLogger(Producer::class.java)

    fun produceInformasjonEventForIdent(ident: String) {
        KafkaProducer<String, Informasjon>(Kafka.producerProps(Environment())).use { producer ->
            producer.send(ProducerRecord(informasjonTopicName, createInformasjonForIdent(ident)))
        }
        log.info("Har produsert et informasjons-event for identen: $ident")
    }

    private fun createInformasjonForIdent(ident: String): Informasjon {
        val nowInMs = Instant.now().toEpochMilli()
        val build = Informasjon.newBuilder()
                .setAktorId(ident)
                .setDokumentId("100" + nowInMs)
                .setEventId("" + nowInMs)
                .setProdusent("DittNAV")
                .setLink("https://nav.no/systemX/" + nowInMs)
                .setTekst("Dette er informasjon til brukeren")
                .setTidspunkt(nowInMs)
                .setSikkerhetsniva(4)
        return build.build()
    }

}
