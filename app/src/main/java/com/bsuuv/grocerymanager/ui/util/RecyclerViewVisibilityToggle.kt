package com.bsuuv.grocerymanager.ui.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewVisibilityToggle {
    companion object {
        /**
         * @param recyclerView             The <code>RecyclerView</code> to act on
         * @param recyclerViewPlaceholder  Placeholder <code>TextView</code> for the <code>RecyclerView</code>
         * @param visibility               Visibility value from the class <code>View</code> to set on the
         *                                 <code>RecyclerView</code>
         * @param placeholderStrResourceId String resource id for the string to be displayed in the
         *                                 placeholder
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