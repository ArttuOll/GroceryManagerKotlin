package com.bsuuv.grocerymanager.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class responsible for operations related to capturing and saving an image.
 * @throws IOException, if writing the image file is interrupted.
 */
class CameraUtil(private val mContext: Context) {

    private val mImageFile: File

    init {
        mImageFile = createImageFile()
    }

    private fun createImageFile(): File {
        val imageFileName = getImageFileName()
        val storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun getImageFileName(): String {
        val timeStamp = SimpleDateFormat.getDateInstance().format(Date())
        return "JPEG_${timeStamp}_"
    }

    /**
     * @return Intent configured for capturing an image using the device's camera app
     */
    fun getIntentToCaptureImage(): Intent {
        val toCaptureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageUri = FileProvider.getUriForFile(
            mContext, "com.bsuuv.android.fileprovider",
            mImageFile
        )
        toCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        return toCaptureImage
    }

    /**
     * @return String URI of the last taken image
     */
    fun getImagePath() = Uri.parse(mImageFile.toURI().path).path.toString()

    /**
     * @param toCaptureImage Intent configured to launch the device's camera app
     * @return boolean telling if a camera app has been installed on the device
     */
    fun cameraAppExists(toCaptureImage: Intent) =
        toCaptureImage.resolveActivity(mContext.packageManager) != null
}