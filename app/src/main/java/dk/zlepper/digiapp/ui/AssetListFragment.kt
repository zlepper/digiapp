package dk.zlepper.digiapp.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val READ_FILE_REQUEST_CODE = 4578
const val EXTERNAL_STORAGE_REQUEST_CODE = 1
val REQUEST_TAKE_PHOTO = 1
val REQUEST_IMAGE_CAPTURE = 1
var currentPhotoPath: String = ""
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
            else if (requestCode == REQUEST_TAKE_PHOTO) {
                val f = File(currentPhotoPath)
                GlobalScope.launch {
                    println("Uploading...")
                    val uploadsrvc = UploadService()
                    val ak = AuthenticatedUserService.requireAccessKey()
                    uploadsrvc.uploadFile(f.inputStream(), f.name, ak)

                }
                Toast.makeText(context, "Uploading file" + f.name, Toast.LENGTH_LONG).show()
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

    @Throws(Exception::class)
    fun convertFileToContentUri(file: File): Uri {

        //Uri localImageUri = Uri.fromFile(localImageFile); // Not suitable as it's not a content Uri

        val cr = context?.contentResolver
        val imagePath = file.absolutePath
        val imageName = null
        val imageDescription = null
        val uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription)
        return Uri.parse(uriString)
    }




    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context?.packageManager as PackageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File, probably because a storage reference couldn't be obtained
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    //galleryAddPic()
                    val photoURI: Uri? = context?.let { it1 ->
                        FileProvider.getUriForFile(
                            it1,
                            "dk.zlepper.digiapp.fileprovider",
                            it
                        )
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun uploadNewImageFromCamera() {
        takePicture()
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }

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
