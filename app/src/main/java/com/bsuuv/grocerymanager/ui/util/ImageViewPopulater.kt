package com.bsuuv.grocerymanager.ui.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

/**
 * Utility class for populating `ImageView`s using `Glide`.
 */
class ImageViewPopulater {
    companion object {
        /**
         * In the activity specified by [context], populates the given [imageView] with the image
         * found in the given string [uri].
         */
        fun populateFromUri(context: Context, uri: String, imageView: ImageView) {
            Glide.with(context).load(File(uri)).into(imageView)
        }
    }
}