package com.desmond.allaboutrecyclerview.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.desmond.allaboutrecyclerview.R;

/**
 * Credit: https://github.com/devunwired/recyclerview-playground
 *
 * Applies an insert margin around each child of the RecyclerView.
 * The insert value is controlled by a dimension resource
 */
public class InsertDecoration extends RecyclerView.ItemDecoration {

    private int mInserts;

    public InsertDecoration(Context context) {
        super();
        mInserts = context.getResources().getDimensionPixelSize(R.dimen.card_insets);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // Force inserts for each item view here in the Rect
        outRect.set(mInserts, mInserts, mInserts, mInserts);
    }
}
