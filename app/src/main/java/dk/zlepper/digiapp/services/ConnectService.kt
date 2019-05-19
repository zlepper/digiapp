package dk.zlepper.digiapp.services

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
        @Field("accessKey") accessKey: String): DigizuiteApiResponse<CreateAccessKeyResponse>

    /*
    method: LogOnAccessKeyOptions
options: {"dez.configversionid":"!/5/","dez.dataversionid":"!/5/","dez.useversionedmetadata":0,"dez.setmembersystemlanguage":3}
accessKey: d0b79ade-3f95-47c0-93b3-b65484c64a8b
     */
}