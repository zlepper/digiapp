package dk.zlepper.digiapp.datasources

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import dk.zlepper.digiapp.models.Asset
import dk.zlepper.digiapp.services.AssetService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class AssetDataSource : PositionalDataSource<Asset>() {
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Asset>) {
        val startPage = params.startPosition / params.loadSize
        println("Loading range startPosition ${params.startPosition} loadSize ${params.loadSize} startPage $startPage")

        val assetList = runBlocking { AssetService.getAssets(startPage, params.loadSize) }

        callback.onResult(assetList.assets)
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Asset>) {
        println("Loading initial pageSize ${params.pageSize}, requestedLoadSize ${params.requestedLoadSize} requestedStartPosition ${params.requestedStartPosition}")

        val (total, assets) = runBlocking {
            val pages = params.requestedLoadSize / params.pageSize
            val startPage = params.requestedStartPosition / params.pageSize
            val assetLists = (0..pages)
                .map {
                    val page = startPage + it
                    println("Loading initial page $page")
                    async(Dispatchers.IO) {
                        AssetService.getAssets(page, params.pageSize)
                    }
                }
                .map { it.await() }


            val total = assetLists.first().total
            val assets = assetLists.flatMap { it.assets }

            Pair(total, assets)
        }

        callback.onResult(assets, params.requestedStartPosition, total)
    }

    companion object {
        val Factory = object : DataSource.Factory<Int, Asset>() {
            override fun create(): DataSource<Int, Asset> {
                return AssetDataSource()
            }
        }
    }
}