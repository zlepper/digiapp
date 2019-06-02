package dk.zlepper.digiapp.ui


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import dk.zlepper.digiapp.R
import dk.zlepper.digiapp.services.AuthenticatedUserService
import dk.zlepper.digiapp.services.UploadService
import kotlinx.android.synthetic.main.activity_take_picture.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class TakePictureActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 1
    var currentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //takePicture()
        setContentView(R.layout.activity_take_picture)
        upload_image_preview.setImageURI(Uri.parse(currentPhotoPath))
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }

    }

    fun buttonHandler(view: View) {
        val f = File(currentPhotoPath)
        GlobalScope.launch {
            val uploadsrvc = UploadService()
            val ak = AuthenticatedUserService.requireAccessKey()
            uploadsrvc.uploadFile(f.inputStream(), f.name, ak)

        }
        Toast.makeText(this, "Uploading file" + f.name, Toast.LENGTH_LONG).show()
    }



}
