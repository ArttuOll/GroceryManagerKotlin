package com.bsuuv.grocerymanager.ui.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

/**
 * Utility class for populating <code>ImageView</code>s using <code>Glide</code>.
 */
class ImageViewPopulater {
    companion object {
        /**
         * @param context   The activity in which the image is to be displayed
         * @param uri       String representing the location of the image file
         * @param imageView The <code>ImageView</code> that's going to host the image
         */
        fun populateFromUri(context: Context, uri: String, imageView: ImageView) {
            Glide.with(context).load(File(uri)).into(imageView)
        }
    }
}