package dk.zlepper.digiapp.services

import dk.zlepper.digiapp.models.Asset
import dk.zlepper.digiapp.services.http.UploadService
import dk.zlepper.digiapp.services.http.mmDamRetrofit
import kotlinx.coroutines.delay
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.create
import java.io.InputStream

data class UploadedAsset(val asset: Asset)

interface UploadProgressUpdate

data class ItemIdRequested(val itemId: Int) : UploadProgressUpdate

data class FileUploaded(val itemId: Int) : UploadProgressUpdate

data class UploadFinished(val itemId: Int, val assetId: Int) : UploadProgressUpdate

data class AssetReady(val asset: Asset) : UploadProgressUpdate

/**
 * Determines the size of each file slice
 * 1 MB
 */
val SLICE_SIZE = 1000 * 1000

class UploadService {

    var progressUpdates: (UploadProgressUpdate) -> Unit = {}

    val uploadService = mmDamRetrofit.create<UploadService>()

    suspend fun uploadFile(stream: InputStream, filename: String, accessKey: String): UploadedAsset {
        val start = uploadService.initiateUpload(accessKey, filename, "Digizuite Media Manager", "InitiateUpload")
        val itemId = start.item.itemId

        progressUpdates(ItemIdRequested(itemId))

        // I have no idea how to do this better right now, though there probably is a way
        var more = true
        do {
            val bytes = ByteArray(SLICE_SIZE)

            val numberOfReadBytes = stream.read(bytes)

            val isLast = if (numberOfReadBytes == -1 || numberOfReadBytes < bytes.size) {
                more = false
                1
            } else {
                0
            }

            val body = RequestBody.create(MediaType.parse("application/octet"), bytes)

            uploadService.uploadFileChunk(accessKey, itemId, isLast, body)
        } while (more)
        progressUpdates(FileUploaded(itemId))

        val finishUploadResult = uploadService.finishUpload("UploadAsset", accessKey, itemId, start.item.uploadId)
        progressUpdates(UploadFinished(finishUploadResult.item.ItemId, finishUploadResult.item.AssetId))

        val asset = waitForAsset(itemId)

        progressUpdates(AssetReady(asset))

        return UploadedAsset(asset)
    }

    private suspend fun waitForAsset(itemId: Int): Asset {
        do {
            // It really should be on the first page, if we request 100. The DC is not that fast..
            val asset = AssetService.getAssets(0, 100).assets.find { it.itemId == itemId }

            if (asset != null) {
                return asset
            }

            // Try again in 10 seconds
            delay(10000)

        } while (true)
    }

}