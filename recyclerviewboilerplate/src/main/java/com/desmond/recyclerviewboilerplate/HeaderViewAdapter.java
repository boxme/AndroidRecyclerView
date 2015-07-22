package com.desmond.recyclerviewboilerplate;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Credit:
 * https://github.com/koush/boilerplate/blob/master/src/com/koushikdutta/boilerplate/recyclerview/HeaderViewAdapter.java
 */
public class HeaderViewAdapter extends RecyclerView.Adapter<HeaderViewAdapter.ViewHolder> {

    List<Header> mHeaders = new ArrayList<>();
    int mViewTypeCount;

    public void addHeaderView(int index, View header) {
        Header h = new Header();
        h.mView = header;
        h.mViewType = mViewTypeCount++;
        mHeaders.add(index, h);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mHeaders.get(position).mViewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        for (Header h: mHeaders) {
            if (h.mViewType == viewType)
                return new ViewHolder(h.mView);
        }
        throw new AssertionError("unexpected viewtype");
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mHeaders.size();
    }

    private static class Header {
        View mView;
        int mViewType;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements GridRecyclerView.SpanningViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int getSpanSize(int spanCount) {
            return spanCount;
        }
    }
}
