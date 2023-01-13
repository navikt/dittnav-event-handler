create table if not exists beskjed
(
    id serial not null
        constraint beskjed_pkey primary key,
    systembruker        varchar(100),
    eventtidspunkt      timestamp,
    fodselsnummer       varchar(50),
    eventid             varchar(50) unique,
    grupperingsid       varchar(100),
    tekst               varchar(500),
    link                varchar(200),
    sikkerhetsnivaa     integer,
    sistoppdatert       timestamp,
    aktiv               boolean,
    synligfremtil       timestamp,
    uid                 varchar(100),
    namespace           varchar(100),
    appnavn             varchar(100),
    forstbehandlet      timestamp,
    eksternVarsling     boolean,
    prefererteKanaler   varchar(100),
    frist_utløpt BOOLEAN DEFAULT NULL,
    constraint beskjedeventidprodusent
    unique (eventid, systembruker)
);

create table if not exists oppgave
(
    id serial not null
        constraint oppgave_pkey primary key,
    systembruker        varchar(100),
    eventtidspunkt      timestamp,
    fodselsnummer       varchar(50),
    eventid             varchar(50) unique,
    grupperingsid       varchar(100),
    tekst               varchar(500),
    link                varchar(200),
    sikkerhetsnivaa     integer,
    sistoppdatert       timestamp,
    aktiv               boolean,
    synligfremtil       timestamp,
    namespace           varchar(100),
    appnavn             varchar(100),
    forstbehandlet      timestamp,
    eksternVarsling     boolean,
    prefererteKanaler   varchar(100),
    frist_utløpt BOOLEAN DEFAULT NULL,
  constraint oppgaveeventidprodusent
  unique (eventid, systembruker)
);

create table if not exists innboks
(
  id serial not null
    constraint innboks_pkey primary key,
  systembruker    varchar(100),
  eventtidspunkt  timestamp,
  fodselsnummer   varchar(50),
  eventid         varchar(50) unique,
  grupperingsid   varchar(100),
  tekst           varchar(500),
  link            varchar(200),
  sikkerhetsnivaa integer,
  sistoppdatert   timestamp,
  aktiv           boolean,
  namespace       varchar(100),
  appnavn         varchar(100),
  forstbehandlet  timestamp,
  eksternVarsling     boolean,
  prefererteKanaler   varchar(100),
  frist_utløpt BOOLEAN DEFAULT NULL,
  constraint innbokseventidprodusent
  unique (eventid, systembruker)
);

create table if not exists done (
    systembruker    varchar(100),
    eventTidspunkt  timestamp,
    fodselsnummer   varchar(50),
    eventId         varchar(50),
    grupperingsId   varchar(100),
    namespace       varchar(100),
    appnavn         varchar(100),
    forstbehandlet  timestamp,
    constraint doneeventidprodusent
    unique (eventid, systembruker)
);

CREATE OR REPLACE VIEW brukernotifikasjon_view as
  SELECT beskjed.eventid, beskjed.systembruker, 'beskjed' :: text AS type, beskjed.fodselsnummer, beskjed.aktiv
  FROM beskjed
  UNION
  SELECT oppgave.eventid, oppgave.systembruker, 'oppgave' :: text AS type, oppgave.fodselsnummer, oppgave.aktiv
  FROM oppgave
  UNION
  SELECT innboks.eventid, innboks.systembruker, 'innboks' :: text AS type, innboks.fodselsnummer, innboks.aktiv
  FROM innboks;

CREATE TABLE doknotifikasjon_status_beskjed (
    eventId                 VARCHAR(50) UNIQUE,
    status                  TEXT,
    melding                 TEXT,
    distribusjonsId         BIGINT,
    tidspunkt               TIMESTAMP WITHOUT TIME ZONE,
    kanaler                 TEXT,
    antall_oppdateringer    SMALLINT,
    constraint fk_dokstatus_beskjed_eventid FOREIGN KEY(eventId) REFERENCES beskjed(eventId)
);

CREATE TABLE doknotifikasjon_status_oppgave (
    eventId                 VARCHAR(50) UNIQUE,
    status                  TEXT,
    melding                 TEXT,
    distribusjonsId         BIGINT,
    tidspunkt               TIMESTAMP WITHOUT TIME ZONE,
    kanaler                 TEXT,
    antall_oppdateringer    SMALLINT,
    constraint fk_dokstatus_oppgave_eventid FOREIGN KEY(eventId) REFERENCES oppgave(eventId)
);

CREATE TABLE doknotifikasjon_status_innboks (
    eventId                 VARCHAR(50) UNIQUE,
    status                  TEXT,
    melding                 TEXT,
    distribusjonsId         BIGINT,
    tidspunkt               TIMESTAMP WITHOUT TIME ZONE,
    kanaler                 TEXT,
    antall_oppdateringer    SMALLINT,
    constraint fk_dokstatus_innboks_eventid FOREIGN KEY(eventId) REFERENCES innboks(eventId)
);
