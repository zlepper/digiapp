package dk.zlepper.digiapp.services.http

import androidx.annotation.Keep
import dk.zlepper.digiapp.models.DigizuiteApiResponse
import okhttp3.RequestBody
import retrofit2.http.*

data class InitiateUploadResponse(val itemId: Int, val uploadId: Int)

data class FinishUploadResponse(val AssetId: Int, val ItemId: Int)

@Keep
interface UploadService {
    /**
     * Call this first
     */
    @FormUrlEncoded
    @POST("/dmm3bwsv3/UploadService.js")
    suspend fun initiateUpload(
        @Field("accessKey") accessKey: String,
        @Field("filename") filename: String,
        @Field("uploadername") computerName: String,
        @Field("method") method: String
    ): DigizuiteApiResponse<InitiateUploadResponse>

    /**
     * Call this second, repeatedly
     */
    @POST("/dmm3bwsv3/UploadFileChunk.js?jsonresponse=1")
    suspend fun uploadFileChunk(
        @Query("accessKey") accessKey: String,
        @Query("itemid") itemId: Int,
        @Query("finished") finished: Int,
        @Body body: RequestBody
    )

    /**
     * Call this last
     */
    @FormUrlEncoded
    @POST("/dmm3bwsv3/UploadService.js")
    suspend fun finishUpload(
        @Field("method") method: String,
        @Field("accessKey") accessKey: String,
        @Field("itemId") itemId: Int,
        @Field("digiuploadId") digiuploadId: Int
    ): DigizuiteApiResponse<FinishUploadResponse>
}