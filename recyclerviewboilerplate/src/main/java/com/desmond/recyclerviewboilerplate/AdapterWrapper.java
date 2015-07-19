package com.desmond.recyclerviewboilerplate;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Credit:
 * https://github.com/koush/boilerplate/blob/master/src/com/koushikdutta/boilerplate/recyclerview/AdapterWrapper.java
 *
 * A wrapper to create one collective adapter from multiple adapters
 */
public class AdapterWrapper extends RecyclerView.Adapter {

    static final int INVALID = -1;

    // start at 1, 0 is header.
    int mViewTypeMapCount = 1;
    SparseArray<WrappedAdapter> mViewTypes = new SparseArray<>();

    ArrayList<WrappedAdapter> mListOfAdapters = new ArrayList<>();

    @LayoutRes int mHeaderLayoutId;

    private int nextEmptyViewType() {
        // empty views are always even view types
        if (mViewTypeMapCount % 2 == 1) {
            mViewTypeMapCount++;
        }
        return mViewTypeMapCount++;
    }

    private int nextViewType() {
        // normal views are always odd view types
        if (mViewTypeMapCount % 2 == 0) {
            mViewTypeMapCount++;
        }
        return mViewTypeMapCount++;
    }

    protected void setHeaderLayoutId(@LayoutRes int id) {
        mHeaderLayoutId = id;
    }

    private int getAdapterStartPosition(RecyclerView.Adapter adapter) {
        int count = 0;
        for (WrappedAdapter info: mListOfAdapters) {
            if (info.mAdapter == adapter) {
                return count;
            }

            int adapterCount = info.mAdapter.getItemCount();
            count += adapterCount;

            // header check
            if (info.isShowingHeader()) {
                count++;
            }
            if (info.isShowingEmptyView()) {
                count++;
            }
        }
        throw new RuntimeException("invalid mAdapter");
    }

    public WrappedAdapter wrapAdapter(RecyclerView.Adapter adapter) {
        if (adapter == null) {
            throw new AssertionError("adapter must not be null");
        }

        WrappedAdapter info = new WrappedAdapter();
        info.mAdapter = adapter;
        mListOfAdapters.add(info);

        // Register another Observer to the adapter
        adapter.registerAdapterDataObserver(info);
        notifyDataSetChanged();

        return info;
    }

    public void remove(RecyclerView.Adapter adapter) {
//        for(Iterator<Map.Entry<Integer, WrappedAdapter>> it = mViewTypes.entrySet().iterator(); it.hasNext(); ) {
//            Map.Entry<Integer, WrappedAdapter> entry = it.next();
//
//            if (entry.getValue().mAdapter == adapter) {
//                it.remove();
//            }
//        }
        int numOfKeys = mViewTypes.size();
        for (int i = 0; i < numOfKeys; i++) {
            if (mViewTypes.get(mViewTypes.keyAt(i)).mAdapter == adapter) {
                // Remove the specific AdapterWrapper
                mViewTypes.removeAt(i);
            }
        }

        // De-register the observer to the adapter
        for (int i = 0; i < mListOfAdapters.size(); i++) {
            if (mListOfAdapters.get(i).mAdapter == adapter) {
                unregisterAdapterDataObserver(mListOfAdapters.remove(i));
                notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        for (WrappedAdapter info: mListOfAdapters) {
            // header check
            if (info.isShowingHeader()) {
                if (position == 0) {
                    return 0;
                }
                position--;
            }

            if (position < info.mAdapter.getItemCount()) {
                final int viewType;
                boolean isEmpty = info.isShowingEmptyView();
                if (isEmpty) {
                    viewType = info.mEmptyViewType;
                } else {
                    viewType = info.mAdapter.getItemViewType(position);
                }

                if (info.mViewTypes.get(viewType, INVALID) == INVALID) {
                    int mappedViewType = isEmpty ? nextEmptyViewType() : nextViewType();

                    mViewTypes.put(mappedViewType, info);

                    info.mViewTypes.put(viewType, mappedViewType);
                    info.mUnmappedViewTypes.put(mappedViewType, viewType);

                    return mappedViewType;
                } else {
                    // Return the mapped view type
                    return info.mViewTypes.get(viewType);
                }
            }
            position -= info.mAdapter.getItemCount();
        }

        throw new RuntimeException("invalid position");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View header = LayoutInflater.from(parent.getContext()).inflate(mHeaderLayoutId, parent, false);
            return new HeaderViewHolder(header);
        }

        WrappedAdapter info = mViewTypes.get(viewType);
        if (info.mEmptyViewType == viewType) {
            return new EmptyViewHolder(info.mEmptyView);
        }

        int unmappedViewType = info.mUnmappedViewTypes.get(viewType);
        return info.mAdapter.onCreateViewHolder(parent, unmappedViewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder) {
            // no op, nothing to bind.
            return;
        }
        for (WrappedAdapter info: mListOfAdapters) {
            // header check
            if (info.isShowingHeader()) {
                if (position == 0) {
                    ((HeaderViewHolder) holder).bind(info);
                    return;
                }
                position--;
            }

            if (position < info.mAdapter.getItemCount()) {
                info.mAdapter.onBindViewHolder(holder, position);
                return;
            }

            position -= info.mAdapter.getItemCount();
        }
        throw new RuntimeException("invalid position");
    }

    public boolean isEmptyView(int position) {
        return getItemViewType(position) % 2 == 0;
    }

    public boolean isHeaderView(int position) {
        return getItemViewType(position) == 0;
    }

    @Override
    public int getItemCount() {
        return getItemCount(true);
    }

    private int getItemCount(boolean withEmptyView) {
        int count = 0;
        for (WrappedAdapter info: mListOfAdapters) {
            int adapterCount = info.mAdapter.getItemCount();
            count += adapterCount;

            // header check
            if (info.isShowingHeader()) {
                count++;
            }
            if (info.isShowingEmptyView()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Wrapped an adapter to combine with other multiple adapters
     * A section header can be assigned to each of them
     */
    public class WrappedAdapter extends RecyclerView.AdapterDataObserver {
        String mSectionHeader;
        View mEmptyView;
        int mEmptyViewType;

        SparseIntArray mViewTypes = new SparseIntArray();
        SparseIntArray mUnmappedViewTypes = new SparseIntArray();
        RecyclerView.Adapter mAdapter;

        private boolean isShowingHeader() {
            return mSectionHeader != null && mAdapter.getItemCount() > 0;
        }

        private boolean isShowingEmptyView() {
            return mEmptyView != null && mAdapter.getItemCount() == 0;
        }

        public WrappedAdapter sectionHeader(String sectionHeader) {
            mSectionHeader = sectionHeader;
            notifyDataSetChanged();
            return this;
        }

        public WrappedAdapter sectionHeader(Context context, int string) {
            return sectionHeader(context.getString(string));
        }

        public WrappedAdapter setEmptyView(View emptyView) {
            mEmptyView = emptyView;
            mEmptyViewType = nextEmptyViewType();
            notifyDataSetChanged();
            return this;
        }

        public WrappedAdapter setEmptyViewWithId(Context context, @LayoutRes int emptyView) {
            return setEmptyView(LayoutInflater.from(context).inflate(emptyView, null));
        }

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            int startPosition = getAdapterStartPosition(mAdapter);
            notifyItemRangeChanged(startPosition + positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            int startPosition = getAdapterStartPosition(mAdapter);
            notifyItemRangeInserted(startPosition + positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            int startPosition = getAdapterStartPosition(mAdapter);
            notifyItemRangeRemoved(startPosition + positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int startPosition = getAdapterStartPosition(mAdapter);
            for (int i = 0; i < itemCount; i++) {
                notifyItemMoved(fromPosition + startPosition + i, toPosition + startPosition + i);
            }
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder
            implements GridRecyclerView.SpanningViewHolder {

        TextView textView;

        /**
         * TextView Resource Id must be android.R.id.text1
         * @param itemView
         */
        public HeaderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(android.R.id.text1);
        }

        public void bind(WrappedAdapter info) {
            textView.setText(info.mSectionHeader);
        }

        @Override
        public int getSpanSize(int spanCount) {
            return spanCount;
        }

    }

    private static class EmptyViewHolder extends RecyclerView.ViewHolder
            implements GridRecyclerView.SpanningViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int getSpanSize(int spanCount) {
            return spanCount;
        }
    }
}
