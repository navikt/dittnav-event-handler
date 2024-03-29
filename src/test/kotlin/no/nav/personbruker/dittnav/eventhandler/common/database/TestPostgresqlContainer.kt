package no.nav.personbruker.dittnav.eventhandler.common.database

import org.testcontainers.containers.PostgreSQLContainer

class TestPostgresqlContainer : PostgreSQLContainer<TestPostgresqlContainer?>(IMAGE_VERSION) {

    companion object {
        private const val IMAGE_VERSION = "postgres:12.6"
    }
}
