package com.desmond.allaboutrecyclerview.listview;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desmond.allaboutrecyclerview.DividerItemDecoration;
import com.desmond.allaboutrecyclerview.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListLayoutFragment extends Fragment {

    public static final String TAG = ListLayoutFragment.class.getSimpleName();

    public static final Fragment newInstance() {
        ListLayoutFragment fragment = new ListLayoutFragment();
        return fragment;
    }

    public ListLayoutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        String[] data = new String[30];
        for (int i = 0; i < 30; i++) {
            data[i] = i + "";
        }

        SimpleAdapter recyclerViewAdapter = new SimpleAdapter(getActivity(), data);

        SimpleSectionedRecyclerViewAdapter sectionedAdapter =
                new SimpleSectionedRecyclerViewAdapter(
                        getActivity(), R.layout.section, R.id.section_text, recyclerViewAdapter);

        // This is the code to provide a sectioned list
        List<SimpleSectionedRecyclerViewAdapter.Section> sections = new ArrayList<>();

        // Sections
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Section 1"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(5,"Section 2"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(12,"Section 3"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(14,"Section 4"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(20,"Section 5"));

        // Add your adapter to the sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] sectionArray =
                new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        sectionedAdapter.setSections(sections.toArray(sectionArray));

        // Apply this adapter to the RecyclerView
        recyclerView.setAdapter(sectionedAdapter);
    }
}
