package com.desmond.allaboutrecyclerview.listview;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.desmond.allaboutrecyclerview.R;
import com.desmond.allaboutrecyclerview.gridview.GridLayoutFragment;

public class ListLayoutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_layout);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,
                    ListLayoutFragment.newInstance(),
                    ListLayoutFragment.TAG)
                    .commit();
        }
    }
}
