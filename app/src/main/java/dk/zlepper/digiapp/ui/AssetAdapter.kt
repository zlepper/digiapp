package dk.zlepper.digiapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dk.zlepper.digiapp.R
import dk.zlepper.digiapp.models.Asset
import kotlinx.android.synthetic.main.asset_list_view_item.view.*

class AssetAdapter : PagedListAdapter<Asset, AssetAdapter.ViewHolder>(ASSET_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.asset_list_view_item, parent, false)

        return ViewHolder(view)
    }

    var onAssetSelected: (Asset) -> Unit = {}

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asset = getItem(position)
        holder.bind(asset)
        if (asset != null) {
            holder.itemView.setOnClickListener { onAssetSelected(asset) }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    class ViewHolder(
        view: View,
        private val title: TextView = view.titleTextView,
        private val byline: TextView = view.bylineTextView,
        private val description: TextView = view.descriptionTextView,
        private val thumbnail: ImageView = view.thumbnailImageView
    ) : RecyclerView.ViewHolder(view) {
        fun bind(asset: Asset?) {
            if (asset == null) {
                val res = itemView.resources
                title.text = res.getString(R.string.loading)
                byline.text = res.getString(R.string.loading)
                description.visibility = View.GONE
                // Clear the image
                thumbnail.setImageResource(android.R.color.transparent)
            } else {
                title.text = asset.name
                byline.text = byline.resources.getString(R.string.byline, asset.uploadedByName)

                if (asset.thumb.isNotEmpty()) {
                    Glide.with(itemView).load(asset.thumb).into(thumbnail)
                } else {
                    thumbnail.setImageResource(android.R.color.transparent)
                }


                if (asset.description.isNotEmpty()) {
                    description.text = asset.description
                    description.visibility = View.VISIBLE
                } else {
                    description.visibility = View.GONE
                }
            }
        }
    }
}