package com.desmond.recyclerviewboilerplate;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Credit:
 * https://github.com/koush/boilerplate/blob/master/src/com/koushikdutta/boilerplate/recyclerview/IHeaderRecyclerView.java
 */
public interface IHeaderRecyclerView {
    void addHeaderView(int index, View view);
    void addOnScrollListener(RecyclerView.OnScrollListener listener);
    int findFirstVisibleItemPosition();
}
