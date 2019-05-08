package dk.zlepper.digiapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AssetListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AssetListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AssetListFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("Creating view")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_asset_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("On create")

        findNavController().navigate(R.id.action_assetListFragment_to_loginFragment)
    }

}
