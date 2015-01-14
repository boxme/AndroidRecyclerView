package com.desmond.allaboutrecyclerview.gridview;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.desmond.allaboutrecyclerview.R;

public class GridLayoutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,
                    GridLayoutFragment.newInstance(),
                    GridLayoutFragment.TAG)
                    .commit();
        }
    }
}
