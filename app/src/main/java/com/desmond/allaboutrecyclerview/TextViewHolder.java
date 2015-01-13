package com.desmond.allaboutrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by desmond on 13/1/15.
 */
public class TextViewHolder extends RecyclerView.ViewHolder {
    View mParentView;
    TextView mTextView;

    public TextViewHolder(View parent) {
        super(parent);
        mParentView = parent;
        mTextView = (TextView) parent.findViewById(R.id.text);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }
}
