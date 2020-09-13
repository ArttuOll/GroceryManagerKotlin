package com.bsuuv.grocerymanager.ui.util

import android.app.Activity.RESULT_OK

/**
 * Utility class for interpreting results of different kinds of `Activity`-related
 * requests.
 */
class RequestValidator {
    companion object {
        const val NONE = 0
        const val FOOD_ITEM_CREATE_REQUEST = 1
        const val FOOD_ITEM_EDIT_REQUEST = 2
        const val REQUEST_IMAGE_CAPTURE = 3

        fun foodItemCreationSuccesful(requestCode: Int, resultCode: Int): Boolean {
            return requestCode == FOOD_ITEM_CREATE_REQUEST &&
                    resultCode == RESULT_OK
        }

        fun foodItemEditSuccesful(requestCode: Int, resultCode: Int): Boolean {
            return requestCode == FOOD_ITEM_EDIT_REQUEST &&
                    resultCode == RESULT_OK
        }

        fun imageCaptureSuccesful(requestCode: Int, resultCode: Int): Boolean {
            return requestCode == REQUEST_IMAGE_CAPTURE &&
                    resultCode == RESULT_OK
        }
    }
}
