package com.desmond.allaboutrecyclerview.fixedTwoWayView;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desmond.allaboutrecyclerview.R;
import com.desmond.allaboutrecyclerview.customLayoutManager.FixedGridLayoutManager;
import com.desmond.allaboutrecyclerview.decoration.InsertDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class FixedTwoWayFragment extends Fragment {

    public static final String TAG = FixedTwoWayFragment.class.getSimpleName();

    private SimpleAdapter mAdapter;

    public static final Fragment newInstance() {
        FixedTwoWayFragment fragment = new FixedTwoWayFragment();
        return fragment;
    }

    public FixedTwoWayFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fixed_two_way, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fixed_two_way_recyclerview);
        FixedGridLayoutManager manager = new FixedGridLayoutManager();
        manager.setTotalColumnCount(10);
        recyclerView.setLayoutManager(manager);

        recyclerView.addItemDecoration(new InsertDecoration(getActivity()));

        mAdapter = new SimpleAdapter();
        mAdapter.setItemCount(12);

        recyclerView.setAdapter(mAdapter);
    }
}
