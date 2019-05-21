package dk.zlepper.digiapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import dk.zlepper.digiapp.R
import dk.zlepper.digiapp.models.Asset
import dk.zlepper.digiapp.services.AuthenticatedUserService
import kotlinx.android.synthetic.main.fragment_asset_list.*
import kotlinx.android.synthetic.main.fragment_asset_list.view.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AssetListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AssetListFragment.newInstance] Factory method to
 * create an instance of this fragment.
 *
 */
class AssetListFragment : Fragment() {
    private val adapter = AssetAdapter()
    private lateinit var viewModel: AssetListViewModel

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

        if (!AuthenticatedUserService.authenticated) {
            val loginAction =
                AssetListFragmentDirections.actionAssetListFragmentToLoginFragment()
            findNavController().navigate(loginAction)
        }
    }

    override fun onStart() {
        super.onStart()

        if (!AuthenticatedUserService.authenticated) return

        initViewModel()
        initAdapter()
        adapter.onAssetSelected = {
            val action = AssetListFragmentDirections.actionAssetListFragmentToAssetDetailsFragment(it.assetId)
            findNavController().navigate(action)
        }

        uploadButton.setOnClickListener { onUploadClicked() }
    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AssetListViewModel() as T
            }

        })[AssetListViewModel::class.java]
    }

    private fun initAdapter() {
        val v = view
        if (v != null) {
            v.assetListList.adapter = adapter
            viewModel.assets.observe(this, Observer<PagedList<Asset>> {
                adapter.submitList(it)
            })
        } else {
            println("View was null?????")
        }
    }

    fun onUploadClicked() {
        println("Upload clicked!!")


    }

}
