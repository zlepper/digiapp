package dk.zlepper.digiapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.zlepper.digiapp.R

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_asset_details, container, false)
    }


}
