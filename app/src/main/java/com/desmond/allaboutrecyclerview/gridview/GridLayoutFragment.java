package com.desmond.allaboutrecyclerview.gridview;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.desmond.allaboutrecyclerview.R;

/**
 * Fragment to show the usage of RecyclerView to display a grid layout
 * Included in this example is also the addition of a headerview to the
 * grid layout.
 */
public class GridLayoutFragment extends Fragment {

    public static final String TAG = GridLayoutFragment.class.getSimpleName();

    private GridLayoutManager mGridLayoutMgr;
    private int mSpanCount;

    public static GridLayoutFragment newInstance() {
        GridLayoutFragment fragment = new GridLayoutFragment();
        return fragment;
    }

    public GridLayoutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mSpanCount = 3;

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.grid_recyclerview);
        recyclerView.setHasFixedSize(true);

        mGridLayoutMgr = new GridLayoutManager(
                getActivity(), mSpanCount, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mGridLayoutMgr);

        recyclerView.setAdapter(new NumberAdapter(30));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_grid_layout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.change_span) {
            boolean shouldChangeSpan = item.isChecked();
            item.setChecked(!shouldChangeSpan);
            setSpanSize(!shouldChangeSpan);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSpanSize(final boolean shouldChangeSpan) {
        mGridLayoutMgr.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return shouldChangeSpan ? (mSpanCount - position % mSpanCount) : 1;
            }
        });
        mGridLayoutMgr.requestLayout();
    }
}
