package no.nav.personbruker.dittnav.eventhandler.informasjon

class ProduceInformasjonDto(val tekst: String, val link: String) {
    override fun toString(): String {
        return "ProduceDto{tekst='$tekst', lenke='$link'}"
    }
}