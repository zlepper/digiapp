package dk.zlepper.digiapp.services

import dk.zlepper.digiapp.models.Asset
import dk.zlepper.digiapp.services.http.AssetService
import dk.zlepper.digiapp.services.http.mmDamRetrofit
import retrofit2.create

data class AssetList(val assets: List<Asset>, val total: Int)

object AssetService {
    private val assetService = mmDamRetrofit.create<AssetService>()

    suspend fun getAssets(page: Int, limit: Int): AssetList {
        val config = ConfigService.getConfigs()
        val accessKey = AuthenticatedUserService.requireAccessKey()

        val response = assetService.getAssets(page + 1, limit, config.folderId, accessKey)

        if (!response.success) {
            throw Exception("getAssets failed: $response")
        }

        return AssetList(response.items, response.total)
    }
}