package dk.zlepper.digiapp.ui

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import dk.zlepper.digiapp.datasources.AssetDataSource

class AssetListViewModel : ViewModel() {
    private val repository = AssetDataSource()

    val assets = LivePagedListBuilder(AssetDataSource.Factory, 12).build()

}