package no.nav.personbruker.dittnav.eventhandler.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature

object HttpClientBuilder {

    fun build(): HttpClient {
        return HttpClient(Apache) {
            install(JsonFeature) {
                serializer = buildJsonSerializer()
            }
        }
    }

}