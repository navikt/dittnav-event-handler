package no.nav.personbruker.dittnav.eventhandler.common

enum class IdentityClaim(val claimName : String) {

    SUBECT("sub"),
    PID("pid");

    companion object {
        fun fromClaimName(claimName: String): IdentityClaim {
            values().forEach { currentClaim ->
                if (currentClaim.claimName == claimName.toLowerCase()) {
                    return currentClaim
                }
            }
            val msg = "Ugyldig claim name '$claimName', gyldige verdier er ${values().toSet()}"
            throw IllegalArgumentException(msg)
        }
    }

    override fun toString(): String {
        return "IdentidyClaim(claimName='$claimName')"
    }

}
