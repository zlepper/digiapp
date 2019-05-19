package dk.zlepper.digiapp.services.http

import androidx.annotation.Keep
import dk.zlepper.digiapp.models.DigizuiteApiResponse
import dk.zlepper.digiapp.models.SystemConfigResponse
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface ConfigService {
    @GET("/dmm3bwsv3/ConfigService.js?method=GetConfigs&page=1&limit=25&useVersionedMetadata=true")
    suspend fun getConfigs(
        @Query("accessKey") accessKey: String
    ): DigizuiteApiResponse<SystemConfigResponse>
}