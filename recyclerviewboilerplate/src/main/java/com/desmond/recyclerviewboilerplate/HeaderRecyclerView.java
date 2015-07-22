package com.desmond.recyclerviewboilerplate;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Credit:
 * https://github.com/koush/boilerplate/blob/master/src/com/koushikdutta/boilerplate/recyclerview/HeaderRecyclerView.java
 */
public class HeaderRecyclerView extends RecyclerView implements IHeaderRecyclerView {

    private Adapter mAdapter;
    private AdapterWrapper mAdapterWrapper = new AdapterWrapper();
    private HeaderViewAdapter mHeaderViewAdapter = new HeaderViewAdapter();
    private AdapterWrapper.WrappedAdapter mWrappedAdapter;
    private View mEmptyView;

    public HeaderRecyclerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public HeaderRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public HeaderRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mAdapterWrapper.wrapAdapter(mHeaderViewAdapter);

        // Register an observer to the adapterWrapper
        mAdapterWrapper.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                updateEmptyState();
            }
        });
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        // Remove old adapter
        mAdapterWrapper.remove(mAdapter);

        // Change to new adapter
        mAdapter = adapter;

        // Insert new adapter
        mWrappedAdapter = mAdapterWrapper.wrapAdapter(adapter);
        super.setAdapter(mAdapterWrapper);
    }

    public AdapterWrapper.WrappedAdapter getWrappedAdapter() {
        return mWrappedAdapter;
    }

    public void addHeaderView(View view) {
        mHeaderViewAdapter.addHeaderView(mHeaderViewAdapter.getItemCount(), view);
    }

    @Override
    public void addHeaderView(int index, View view) {
        mHeaderViewAdapter.addHeaderView(index, view);
    }

    @Override
    public int findFirstVisibleItemPosition() {
        return ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
    }

    public void setEmptyView(View view) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
        }

        mEmptyView = view;
        updateEmptyState();
    }

    void updateEmptyState() {
        if (mEmptyView == null) {
            return;
        }

        if (mAdapterWrapper.getItemCount() - mHeaderViewAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
        }
    }
}
