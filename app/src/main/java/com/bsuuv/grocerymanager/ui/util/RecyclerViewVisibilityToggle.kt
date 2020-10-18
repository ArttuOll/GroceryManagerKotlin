package com.bsuuv.grocerymanager.ui.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class RecyclerViewVisibilityToggle {
    companion object {
        /**
         * Toggles the visibility of the [recyclerView] on and off, replacing it with the
         * [placeholder] when off. The text of the [placeholder] is defined by the
         * [placeholderStrResId].
         */
        fun toggleOn(
            recyclerView: RecyclerView,
            placeholder: TextView,
            fab: ExtendedFloatingActionButton
        ) {
            recyclerView.visibility = View.VISIBLE
            fab.visibility = View.VISIBLE
            placeholder.visibility = View.GONE
        }

        fun toggleOff(
            recyclerView: RecyclerView,
            placeholder: TextView,
            placeholderStrResId: Int,
            fab: ExtendedFloatingActionButton
        ) {
            recyclerView.visibility = View.GONE
            fab.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
            placeholder.setText(placeholderStrResId)
        }

        fun toggleOn(
            recyclerView: RecyclerView,
            placeholder: TextView,
        ) {
            recyclerView.visibility = View.VISIBLE
            placeholder.visibility = View.GONE
        }

        fun toggleOff(
            recyclerView: RecyclerView,
            placeholder: TextView,
            placeholderStrResId: Int,
        ) {
            recyclerView.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
            placeholder.setText(placeholderStrResId)
        }
    }
}