package no.nav.personbruker.dittnav.eventhandler.kafka

import no.nav.brukernotifikasjon.schemas.Informasjon
import no.nav.personbruker.dittnav.eventhandler.api.ProduceDto
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.informasjonTopicName
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.oppgaveTopicName
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.time.Instant

object Producer {

    val log = LoggerFactory.getLogger(Producer::class.java)

    fun produceInformasjonEventForIdent(ident: String, dto: ProduceDto) {
        KafkaProducer<String, Informasjon>(Kafka.producerProps(Environment())).use { producer ->
            producer.send(ProducerRecord(informasjonTopicName, createEventForIdent(ident, dto)))
        }
        log.info("Har produsert et informasjons-event for identen: $ident")
    }

    fun produceOppgaveEventForIdent(ident: String, dto: ProduceDto) {
        KafkaProducer<String, Informasjon>(Kafka.producerProps(Environment())).use { producer ->
            producer.send(ProducerRecord(oppgaveTopicName, createEventForIdent(ident, dto)))
        }
        log.info("Har produsert et oppgace-event for identen: $ident")
    }

    private fun createEventForIdent(ident: String, dto: ProduceDto): Informasjon {
        val nowInMs = Instant.now().toEpochMilli()
        val build = Informasjon.newBuilder()
                .setAktorId(ident)
                .setDokumentId("100$nowInMs")
                .setEventId("$nowInMs")
                .setProdusent("DittNAV")
                .setLink(dto.link)
                .setTekst(dto.tekst)
                .setTidspunkt(nowInMs)
                .setSikkerhetsniva(4)
        return build.build()
    }

}
