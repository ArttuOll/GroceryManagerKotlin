package com.bsuuv.grocerymanager.ui.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//TODO: ala käyttää oliona?
class RecyclerViewVisibilityToggle {
    companion object {
        /**
         * Toggles the visibility of the [recyclerView] on and off, replacing it with the
         * [placeholder] when off. The text of the [placeholder] is defined by the
         * [placeholderStrResId].
         */
        fun toggle(
            recyclerView: RecyclerView,
            placeholder: TextView,
            visibility: Int,
            placeholderStrResId: Int
        ) {
            when (visibility) {
                View.VISIBLE -> {
                    recyclerView.visibility = visibility
                    placeholder.visibility = View.GONE
                }
                else -> {
                    recyclerView.visibility = View.GONE
                    placeholder.visibility = View.VISIBLE
                    placeholder.setText(placeholderStrResId)
                }
            }
        }
    }
}