package com.skilrock.boomlotto.utility

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val language: String, private var spanCount: Int = 0,
                           private var spacing: Int = 0, private var includeEdge: Boolean =  false) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position    = parent.getChildAdapterPosition(view)
        val column      = position % spanCount

        if (includeEdge) {
            outRect.left    = spacing - column * spacing / spanCount
            outRect.right   = (column + 1) * spacing / spanCount

            if (position < spanCount)
                outRect.top = spacing

            outRect.bottom = spacing
        } else {
            if (language == LANGUAGE_ENGLISH) {
                outRect.left    = column * spacing / spanCount
                outRect.right   = spacing - (column + 1) * spacing / spanCount
            } else {
                outRect.left    = spacing - (column + 1) * spacing / spanCount
                outRect.right   = column * spacing / spanCount
            }

            if (position >= spanCount)
                outRect.top = spacing
        }
    }

}