package com.desmond.allaboutrecyclerview.gridview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * RecyclerView to display a gridview that has auto_fit to find the best number of span counts
 * given a column width.
 *
 * Use this without setting a GridLayoutManager from outside the class, as this has already been done
 * within the class.
 */
public class AutoFitRecyclerView extends RecyclerView {

    private GridLayoutManager mManager;
    private int mColumnWidth = -1;

    public AutoFitRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public AutoFitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoFitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {android.R.attr.columnWidth};
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            mColumnWidth = array.getDimensionPixelOffset(0, -1);
            array.recycle();
        }

        mManager = new GridLayoutManager(context, 1);
        setLayoutManager(mManager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (mColumnWidth > 0) {
            int spanCount = Math.max(1, getMeasuredWidth() / mColumnWidth);
            mManager.setSpanCount(spanCount);
        }
    }
}
