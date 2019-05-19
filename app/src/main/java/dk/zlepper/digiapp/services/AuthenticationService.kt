package dk.zlepper.digiapp.services

import dk.zlepper.digiapp.models.CreateAccessKeyResponse
import dk.zlepper.digiapp.services.http.ConnectService
import dk.zlepper.digiapp.services.http.mmDamRetrofit
import dk.zlepper.digiapp.utilities.md5
import retrofit2.create

object AuthenticationService {
    val connectService = mmDamRetrofit.create<ConnectService>()

    suspend fun login(username: String, password: String): CreateAccessKeyResponse {
        val pwHash = md5(password)

        // Yes, we can hardcode anything but the username and password
        println("username: $username, pwHash: $pwHash")
        val result = connectService.createAccessKey("CreateAccesskey", 2, 1, 1, 25, username, pwHash)

        if (result.success) {
            val ak = result.item.accessKey

            return logonWithAccessKeyOptions(ak)
        } else {
            throw LoginException("Login failed")
        }
    }

    private suspend fun logonWithAccessKeyOptions(accessKey: String): CreateAccessKeyResponse {
        val options =
            """{"dez.configversionid":"!/5/","dez.dataversionid":"!/5/","dez.useversionedmetadata":0,"dez.setmembersystemlanguage":3}"""

        val result = connectService.logonAccessKeyOptions("LogOnAccessKeyOptions", options, accessKey)

        if (result.success) {
            return result.item
        } else {
            throw LoginException("Access key change failed")
        }

    }

    class LoginException(message: String) : Exception(message)
}