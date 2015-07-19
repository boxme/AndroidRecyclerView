package com.desmond.recyclerviewboilerplate;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.util.SparseIntArray;

/**
 * Credit:
 * https://github.com/koush/boilerplate/blob/master/src/com/koushikdutta/boilerplate/recyclerview/GridRecyclerView.java
 */
public class GridRecyclerView extends HeaderRecyclerView {

    SparseIntArray mTypeToSpan;
    GridLayoutManager mGridLayoutManager;
    GridLayoutManager.SpanSizeLookup mSpanSizeLookup;

    public GridRecyclerView(Context context) {
        super(context);
    }

    public GridRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void init(Context context, AttributeSet attrs, int defStyleAttr) {
        super.init(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.GridRecyclerView, defStyleAttr, 0);
        int numColumns = a.getInt(R.styleable.GridRecyclerView_numColumns, 1);
        a.recycle();

        setNumColumns(context, numColumns);
    }

    public void setNumColumns(int numColumns) {
        setNumColumns(getContext(), numColumns);
    }

    private void setNumColumns(Context context, int numColumns) {
        if (mGridLayoutManager == null) {
            mGridLayoutManager = new GridLayoutManager(context, numColumns);
            mTypeToSpan = new SparseIntArray();

            mGridLayoutManager.setSpanSizeLookup(mSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getAdapter().getItemViewType(position);
                    int span = mTypeToSpan.get(viewType, INVALID_TYPE);

                    if (span != INVALID_TYPE) {
                        return span;
                    }

                    ViewHolder vh = getAdapter().createViewHolder(GridRecyclerView.this, viewType);
                    int foundSpan;
                    if (vh instanceof SpanningViewHolder) {
                        foundSpan = ((SpanningViewHolder) vh).getSpanSize(mGridLayoutManager.getSpanCount());
                    } else {
                        foundSpan = 1;
                    }

                    mTypeToSpan.put(viewType, foundSpan);
                    return foundSpan;
                }
            });
            setLayoutManager(mGridLayoutManager);
        } else {
            mGridLayoutManager.setSpanCount(numColumns);
        }

        mTypeToSpan.clear();
        mSpanSizeLookup.invalidateSpanIndexCache();
        requestLayout();
    }

    public interface SpanningViewHolder {
        int getSpanSize(int spanCount);
    }
}
