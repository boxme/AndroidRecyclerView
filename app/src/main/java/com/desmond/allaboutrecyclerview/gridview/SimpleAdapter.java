package com.desmond.allaboutrecyclerview.gridview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.desmond.allaboutrecyclerview.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the default adapter to use to show the items in the
 * RecyclerView without any sections
 *
 * SectionedGridRecyclerViewAdapter will encapsulate this adapter to show
 * the sections
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {

    private static final int COUNT = 100;

    private final Context mContext;
    private List<Integer> mData;
    private int mCurrentItemId = 0;

    public SimpleAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            addItem(i);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item, viewGroup, false);

        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.title.setText(mData.get(position).toString());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Position = " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addItem(int position) {
        final int id = mCurrentItemId++;
        mData.add(position, id);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (position < getItemCount()) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
