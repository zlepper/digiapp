package dk.zlepper.digiapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
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
import com.leinardi.android.speeddial.SpeedDialActionItem
import dk.zlepper.digiapp.R
import dk.zlepper.digiapp.models.Asset
import dk.zlepper.digiapp.services.AuthenticatedUserService
import dk.zlepper.digiapp.services.UploadService
import kotlinx.android.synthetic.main.fragment_asset_list.*
import kotlinx.android.synthetic.main.fragment_asset_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val READ_FILE_REQUEST_CODE = 4578

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

        uploadButton.addAllActionItems(
            listOf(
                SpeedDialActionItem.Builder(R.id.upload_from_camera, android.R.drawable.ic_menu_camera)
                    .setLabel(R.string.from_camera).create(),
                SpeedDialActionItem.Builder(R.id.upload_from_system, android.R.drawable.ic_menu_gallery)
                    .setLabel(R.string.from_gallery).create()
            )
        )

        uploadButton.setOnActionSelectedListener {
            when (it.id) {
                R.id.upload_from_camera -> uploadNewImageFromCamera()
                R.id.upload_from_system -> uploadFile()
                else -> {
                    println("Unknown action id ${it.id}")
                }
            }
            false
        }
    }


    private fun uploadFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startActivityForResult(intent, READ_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == READ_FILE_REQUEST_CODE) {
                data?.data?.also {
                    Log.i("Result file", "Uri: $it")
                    uploadFile(it)
                }
            }
        }
    }

    private fun uploadFile(uri: Uri) {
        GlobalScope.launch(Dispatchers.Main) {
            val ak = AuthenticatedUserService.requireAccessKey()
            val info = getFileInfo(uri)

            println("Uploading file with info: $info")

            val uploadService = UploadService()

            uploadService.progressUpdates = {
                println("Progress: $it")
            }

            val resolver = context?.contentResolver ?: throw Exception("Could not get content resolver")

            resolver.openInputStream(uri)?.use {
                println("Starting file upload")
                uploadService.uploadFile(it, info.name, ak)
                println("Finished file upload")
            }

        }
    }

    data class FileInfo(val size: Long, val name: String)

    private suspend fun getFileInfo(uri: Uri): FileInfo {
        return withContext(Dispatchers.IO) {
            // The query, since it only applies to a single document, will only return
            // one row. There's no need to filter, sort, or select fields, since we want
            // all fields for one document.
            val cursor = context?.contentResolver?.query(uri, null, null, null, null)
            cursor?.use {
                // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                // "if there's anything to look at, look at it" conditionals.
                if (it.moveToFirst()) {
                    val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    Log.i("Uploading...", "Display Name: $displayName")

                    val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                    // If the size is unknown, the value stored is null.  But since an
                    // int can't be null in Java, the behavior is implementation-specific,
                    // which is just a fancy term for "unpredictable".  So as
                    // a rule, check if it's null before assigning to an int.  This will
                    // happen often:  The storage API allows for remote files, whose
                    // size might not be locally known.
                    val size = if (!it.isNull(sizeIndex)) {
                        it.getLong(sizeIndex)
                    } else {
                        -1
                    }
                    Log.i("Uploading...", "Size: $size")

                    return@withContext FileInfo(size, displayName)
                }
            }

            println("Could not get file info, cursor was null!")
            throw Exception("Could not get file info, as cursor was null")
        }
    }


    private fun uploadNewImageFromCamera() {
        // TODO(fhm) Please implement this :D :D :D :D
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


}
