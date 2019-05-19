package dk.zlepper.digiapp.services.http

import androidx.annotation.Keep
import dk.zlepper.digiapp.models.CreateAccessKeyResponse
import dk.zlepper.digiapp.models.DigizuiteApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@Keep
interface ConnectService {
    @FormUrlEncoded
    @POST("/dmm3bwsv3/ConnectService.js")
    suspend fun createAccessKey(
        @Field("method") method: String,
        @Field("usertype") usertype: Int,

        @Field("useversionedmetadata") useversionedmetadata: Int,
        @Field("page") page: Int,

        @Field("limit") limit: Int,
        @Field("username") username: String,

        @Field("password") password: String
    ): DigizuiteApiResponse<CreateAccessKeyResponse>

    @FormUrlEncoded
    @POST("/dmm3bwsv3/ConnectService.js")
    suspend fun logonAccessKeyOptions(
        @Field("method") method: String,
        @Field("options") options: String,
        @Field("accessKey") accessKey: String
    ): DigizuiteApiResponse<CreateAccessKeyResponse>

}