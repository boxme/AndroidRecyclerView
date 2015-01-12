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
    private List<String> mLabels;

    public NumberAdapter(int count) {
        mLabels = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            mLabels.add(String.valueOf(i));
        }
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item, viewGroup, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TextViewHolder textViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return mLabels.size();
    }
}
