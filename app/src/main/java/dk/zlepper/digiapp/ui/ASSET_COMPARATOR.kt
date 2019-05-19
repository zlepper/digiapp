package dk.zlepper.digiapp.ui

import androidx.recyclerview.widget.DiffUtil
import dk.zlepper.digiapp.models.Asset

object ASSET_COMPARATOR : DiffUtil.ItemCallback<Asset>() {
    override fun areItemsTheSame(oldItem: Asset, newItem: Asset): Boolean {
        return oldItem.assetId == newItem.assetId
    }

    override fun areContentsTheSame(oldItem: Asset, newItem: Asset): Boolean {
        return oldItem == newItem
    }

}