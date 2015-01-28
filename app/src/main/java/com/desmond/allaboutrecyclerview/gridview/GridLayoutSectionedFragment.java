package com.desmond.allaboutrecyclerview.gridview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desmond.allaboutrecyclerview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by desmond on 27/1/15.
 */
public class GridLayoutSectionedFragment extends Fragment {

    public static final String TAG = GridLayoutSectionedFragment.class.getSimpleName();

    private GridLayoutManager mLayoutMgr;
    private int mSpanCount;

    public static GridLayoutSectionedFragment newInstance() {
        GridLayoutSectionedFragment fragment = new GridLayoutSectionedFragment();
        return fragment;
    }

    public GridLayoutSectionedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.grid_recyclerview);
        recyclerView.setHasFixedSize(true);

        mSpanCount = 4;
        mLayoutMgr = new GridLayoutManager(getActivity(), mSpanCount, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutMgr);

        SimpleAdapter adapter = new SimpleAdapter(getActivity());

        List<SectionedGridRecyclerViewAdapter.Section> sections = new ArrayList<>();

        // Sections
        sections.add(new SectionedGridRecyclerViewAdapter.Section(0,"Section 1"));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(5,"Section 2"));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(12,"Section 3"));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(14,"Section 4"));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(20,"Section 5"));

        // Add adapter to the sectionAdapter
        SectionedGridRecyclerViewAdapter.Section[] dummy =
                new SectionedGridRecyclerViewAdapter.Section[sections.size()];
        SectionedGridRecyclerViewAdapter sectionedAdapter =
                new SectionedGridRecyclerViewAdapter(
                        getActivity(), recyclerView, R.layout.section, R.id.section_text, adapter);
        sectionedAdapter.setSections(sections.toArray(dummy));

        recyclerView.setAdapter(sectionedAdapter);
    }
}
