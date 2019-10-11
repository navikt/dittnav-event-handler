package no.nav.personbruker.dittnav.eventhandler.informasjon

import no.nav.brukernotifikasjon.schemas.Informasjon
import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.informasjonTopicName
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.oppgaveTopicName
import no.nav.personbruker.dittnav.eventhandler.informasjon.ProduceInformasjonDto
import no.nav.personbruker.dittnav.eventhandler.oppgave.ProduceOppgaveDto
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.time.Instant

object InformasjonProducer {

    val log = LoggerFactory.getLogger(InformasjonProducer::class.java)

    fun produceInformasjonEventForIdent(ident: String, dto: ProduceInformasjonDto) {
        KafkaProducer<String, Informasjon>(Kafka.producerProps(Environment())).use { producer ->
            producer.send(ProducerRecord(informasjonTopicName, createInformasjonForIdent(ident, dto)))
        }
        log.info("Har produsert et informasjons-event for identen: $ident")
    }

    private fun createInformasjonForIdent(ident: String, dto: ProduceInformasjonDto): Informasjon {
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
