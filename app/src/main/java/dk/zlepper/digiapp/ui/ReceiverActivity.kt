package dk.zlepper.digiapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.OpenableColumns
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import dk.zlepper.digiapp.R
import dk.zlepper.digiapp.services.AuthenticatedUserService
import dk.zlepper.digiapp.services.AuthenticationService
import dk.zlepper.digiapp.services.UploadService
import kotlinx.android.synthetic.main.activity_receiver.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent) // Handle single image being sent
                }
            }
        }
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            GlobalScope.launch {
                AuthenticationService.login("superadministrator", "test")
            }
            uploadFile(it)
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

            val resolver = contentResolver ?: throw Exception("Could not get content resolver")

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
            val cursor = contentResolver?.query(uri, null, null, null, null)
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
}
