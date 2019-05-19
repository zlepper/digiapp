package dk.zlepper.digiapp.services

import dk.zlepper.digiapp.models.SystemConfig
import dk.zlepper.digiapp.services.http.ConfigService
import dk.zlepper.digiapp.services.http.mmDamRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.create

object ConfigService {
    private val configService = mmDamRetrofit.create<ConfigService>()
    private val configMutex = Mutex()
    private var loadedConfigs: SystemConfig? = null

    /**
     * Loads the config from Media Manager, so we know what folder
     * to request in the digizuite api
     */
    suspend fun getConfigs(): SystemConfig {
        return withContext(Dispatchers.Main) {
            configMutex.withLock {
                val accessKey = AuthenticatedUserService.accessKey ?: throw Exception("Application not authenticated")
                val cfg = loadedConfigs
                if (cfg != null) {
                    return@withContext cfg
                } else {
                    println("Loading configs")
                    val response = configService.getConfigs(accessKey)

                    if (!response.success) {
                        throw Exception("Loading configs failed")
                    }

                    println("Loaded configs: ${response.item}")
                    val c = response.item

                    val loadedCfg = SystemConfig(c.UploadName, c.MainSearchFolderId.toInt())
                    loadedConfigs = loadedCfg
                    return@withContext loadedCfg
                }
            }
        }!! // This is actually safe, however kotlin can't infer it for some reason
    }
}