package dk.zlepper.digiapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dk.zlepper.digiapp.R
import dk.zlepper.digiapp.models.AssetType
import dk.zlepper.digiapp.services.AssetService
import dk.zlepper.digiapp.services.AuthenticatedUserService
import dk.zlepper.digiapp.ui.preview.FallbackPreviewFragment
import dk.zlepper.digiapp.ui.preview.ImageAssetPreviewFragment
import dk.zlepper.digiapp.ui.preview.VideoAssetPreviewFragment
import kotlinx.android.synthetic.main.fragment_asset_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AssetDetailsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AssetDetailsFragment.newInstance] Factory method to
 * create an instance of this fragment.
 *
 */
class AssetDetailsFragment : Fragment() {

    val args: AssetDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_asset_details, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (!AuthenticatedUserService.authenticated) {
            val action = AssetDetailsFragmentDirections.actionAssetDetailsFragmentToLoginFragment()
            findNavController().navigate(action)
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            val asset = AssetService.getAsset(args.assetId)
            val previewFragment = when (asset.assetType) {
                AssetType.Image -> ImageAssetPreviewFragment(asset.image1080p)
                AssetType.Video -> VideoAssetPreviewFragment(asset.desktopH264)
                else -> FallbackPreviewFragment()
            }

            // We are intentionally _not_ adding the preview to the backstack, as it doesn't make sense
            // to navigate away from it
            childFragmentManager.beginTransaction().replace(R.id.previewOutput, previewFragment).commit()

            previewLoadSpinner.visibility = View.GONE
            titleTextView.apply {
                visibility = View.VISIBLE
                text = asset.name
            }
            descriptionTextView.apply {
                visibility = View.VISIBLE
                text = asset.description
            }
            bylineTextView.apply {
                visibility = View.VISIBLE
                text = getString(R.string.byline, asset.uploadedByName)
            }
        }
    }


}
