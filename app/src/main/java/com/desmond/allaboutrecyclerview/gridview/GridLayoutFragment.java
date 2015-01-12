package com.desmond.allaboutrecyclerview.gridview;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
    }
}
