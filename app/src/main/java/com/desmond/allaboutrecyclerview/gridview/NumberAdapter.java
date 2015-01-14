package com.desmond.allaboutrecyclerview.gridview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desmond.allaboutrecyclerview.R;
import com.desmond.allaboutrecyclerview.TextViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by desmond on 13/1/15.
 */
public class NumberAdapter extends RecyclerView.Adapter<TextViewHolder> {

    private int ITEM_VIEW_TYPE_HEADER = 0;
    private int ITEM_VIEW_TYPE_ITEM = 1;

    private List<String> mLabels;

    private View mHeaderView;

    public NumberAdapter(View headerView, int count) {
        mHeaderView = headerView;
        mLabels = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            mLabels.add(String.valueOf(i));
        }
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == ITEM_VIEW_TYPE_HEADER && mHeaderView != null) {
            view = mHeaderView;
        }
        else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item, viewGroup, false);
        }
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TextViewHolder textViewHolder, int position) {
        if (isHeader(position)) {
            textViewHolder.setText("Grid View With HeaderView Using RecyclerView");
            return;
        }

        textViewHolder.setText(mLabels.get(position - 1));
    }

    @Override
    public int getItemCount() {
        return mLabels.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }
}
