package com.desmond.allaboutrecyclerview.fixedTwoWayView;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.desmond.allaboutrecyclerview.R;
import com.desmond.allaboutrecyclerview.listview.ListLayoutFragment;

public class FixedTwoWayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_two_way);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,
                    FixedTwoWayFragment.newInstance(),
                    FixedTwoWayFragment.TAG)
                    .commit();
        }
    }
}
