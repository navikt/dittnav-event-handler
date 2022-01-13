package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.common.statistics.EventCountForProducer
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import java.sql.Connection
import java.sql.ResultSet


fun Connection.getAllGroupedDoneEventsBySystemuser(): Map<String, Int> {
    return prepareStatement("SELECT systembruker, COUNT(*) FROM done GROUP BY systembruker",
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                val resultSet = statement.executeQuery()
                mutableMapOf<String, Int>().apply {
                    while (resultSet.next()) {
                        put(resultSet.getString(1), resultSet.getInt(2))
                    }
                }
            }
}

fun Connection.getAllGroupedDoneEventsByProducer(): List<EventCountForProducer> {
    return prepareStatement("SELECT namespace, appnavn, COUNT(*) FROM done GROUP BY namespace, appnavn")
        .use { statement ->
            statement.executeQuery().mapList {
                EventCountForProducer(
                    namespace = getString(1),
                    appName = getString(2),
                    count = getInt(3),
                )
            }
        }
}

fun Connection.countTotalNumberOfBrukernotifikasjonerByActiveStatus(aktiv: Boolean): Map<String, Int> {
    return prepareStatement(
            """SELECT
                subquery.systembruker, sum(count)
        FROM (
             SELECT systembruker, COUNT(1) as count FROM BESKJED WHERE aktiv = ? GROUP BY systembruker
             UNION ALL
             SELECT systembruker, COUNT(1) as count FROM OPPGAVE WHERE aktiv = ? GROUP BY systembruker
             UNION ALL
             SELECT systembruker, COUNT(1) as count FROM INNBOKS WHERE aktiv = ? GROUP BY systembruker
        ) as subquery group by subquery.systembruker order by subquery.systembruker;
    """,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                statement.setBoolean(1, aktiv)
                statement.setBoolean(2, aktiv)
                statement.setBoolean(3, aktiv)
                val resultSet = statement.executeQuery()
                mutableMapOf<String, Int>().apply {
                    while (resultSet.next()) {
                        put(resultSet.getString(1), resultSet.getInt(2))
                    }
                }
            }
}

fun Connection.countTotalNumberPerProducerByActiveStatus(aktiv: Boolean): List<EventCountForProducer> {
    return prepareStatement(
            """SELECT
                subquery.appname, namespace, sum(count)
        FROM (
             SELECT appname, namespace, COUNT(1) as count FROM BESKJED WHERE aktiv = ? GROUP BY appname, namespace
             UNION ALL
             SELECT appname, namespace, COUNT(1) as count FROM OPPGAVE WHERE aktiv = ? GROUP BY appname, namespace
             UNION ALL
             SELECT appname, namespace, COUNT(1) as count FROM INNBOKS WHERE aktiv = ? GROUP BY appname, namespace
        ) as subquery group by subquery.systembruker order by subquery.systembruker;
    """,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                statement.setBoolean(1, aktiv)
                statement.setBoolean(2, aktiv)
                statement.setBoolean(3, aktiv)

                statement.executeQuery().mapList {
                    EventCountForProducer(
                        namespace = getString(1),
                        appName = getString(2),
                        count = getInt(3),
                    )
                }
            }
}

fun ResultSet.toDone(): Done {
    return Done(
            fodselsnummer = getString("fodselsnummer"),
            grupperingsId = getString("grupperingsId"),
            eventId = getString("eventId"),
            eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            systembruker = getString("systembruker")
    )
}
