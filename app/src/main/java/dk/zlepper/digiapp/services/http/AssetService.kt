package dk.zlepper.digiapp.services.http

import androidx.annotation.Keep
import dk.zlepper.digiapp.models.Asset
import dk.zlepper.digiapp.models.DigizuiteApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface AssetService {
    @GET("/dmm3bwsv3/SearchService.js?SearchName=DigiZuite_System_Framework_Search&sort=sortAssetidDesc&sMenu=&assetHasNoPrevref=0&config=facet=true&config=facet.sort=count")
    suspend fun getAssets(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sLayoutFolderId") folderId: Int,
        @Query("accessKey") accessKey: String
    ): DigizuiteApiResponse<Asset>

    @GET("/dmm3bwsv3/SearchService.js?SearchName=GetAssetsById&page=1&limit=99999&sAssetId_type_multiids=1")
    suspend fun getAsset(
        @Query("sAssetId") assetId: Int,
        @Query("sLayoutFolderId") folderId: Int,
        @Query("accessKey") accessKey: String
    ): DigizuiteApiResponse<Asset>
}