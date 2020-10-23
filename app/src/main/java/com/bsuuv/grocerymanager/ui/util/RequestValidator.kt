package com.bsuuv.grocerymanager.ui.util

import android.app.Activity.RESULT_OK

/**
 * Utility class for interpreting results of different kinds of `Activity`-related
 * requests.
 */
class RequestValidator {
    companion object {
        const val REQUEST_IMAGE_CAPTURE = 3

        fun imageCaptureSuccesful(requestCode: Int, resultCode: Int): Boolean {
            return requestCode == REQUEST_IMAGE_CAPTURE &&
                    resultCode == RESULT_OK
        }
    }
}
