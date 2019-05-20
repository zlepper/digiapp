package dk.zlepper.digiapp.ui.preview


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dk.zlepper.digiapp.R
import kotlinx.android.synthetic.main.fragment_image_asset_preview.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ImageAssetPreviewFragment(val previewSource: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_asset_preview, container, false)
    }

    override fun onStart() {
        super.onStart()

        Glide.with(this.context).load(previewSource).into(imageView)
    }


}
