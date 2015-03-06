package com.desmond.allaboutrecyclerview.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.desmond.allaboutrecyclerview.R;

/**
 * Insert margin around each child of the RecyclerView. It also draws
 * item dividers that are expected from a vertical list implementation,
 * such as ListView
 */
public class GridDividerDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = {android.R.attr.listDivider};

    private Drawable mDivider;
    private int mInserts;

    public GridDividerDecoration(Context context) {
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();

        mInserts = context.getResources().getDimensionPixelSize(R.dimen.card_insets);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    /**
     * Draw dividers to the right of each child view
     * @param c
     * @param parent
     */
    public void drawVertical(Canvas c, RecyclerView parent) {
        if (parent.getChildCount() == 0) return;

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final  View child = parent.getChildAt(0);
        if (child.getHeight() == 0) return;

        final RecyclerView.LayoutParams params =
                (RecyclerView.LayoutParams) child.getLayoutParams();
        int top = child.getBottom() + params.bottomMargin + mInserts;
        int bottom = top + mDivider.getIntrinsicHeight();

        final int parentBottom = parent.getHeight() - parent.getPaddingBottom();
        while (bottom < parentBottom) {
            // Bound is the entire parent view, and at each vertical interval
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);

            top += mInserts + params.topMargin + child.getHeight();
            bottom = top + mDivider.getIntrinsicHeight();
        }
    }

    /**
     * Draw dividers at each expected grid interval
     * @param c
     * @param parent
     */
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin + mInserts;
            final int right = left + mDivider.getIntrinsicWidth();

            // Bound is the entire parent view, and at each horizontal interval
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // Force inserts for each item view here in the Rect
        outRect.set(mInserts, mInserts, mInserts, mInserts);
    }
}
