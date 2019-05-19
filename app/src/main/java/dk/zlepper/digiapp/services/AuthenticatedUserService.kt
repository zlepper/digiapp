package dk.zlepper.digiapp.services

object AuthenticatedUserService {
    var accessKey: String? = null

    val authenticated: Boolean
        get() = accessKey != null

    fun requireAccessKey(): String {
        return accessKey ?: throw Exception("Not logged in")
    }
}