package com.desmond.allaboutrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.desmond.allaboutrecyclerview.fixedTwoWayView.FixedTwoWayActivity;
import com.desmond.allaboutrecyclerview.gridview.GridLayoutActivity;
import com.desmond.allaboutrecyclerview.listview.ListLayoutActivity;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showGridView(View view) {
        Intent showGridViewIntent = new Intent(this, GridLayoutActivity.class);
        showGridViewIntent.putExtra(GridLayoutActivity.SECTIONED, false);
        startActivity(showGridViewIntent);
    }

    public void showSectionedGridView(View view) {
        Intent showGridViewIntent = new Intent(this, GridLayoutActivity.class);
        showGridViewIntent.putExtra(GridLayoutActivity.SECTIONED, true);
        startActivity(showGridViewIntent);
    }

    public void showListView(View view) {
        Intent showListViewIntent = new Intent(this, ListLayoutActivity.class);
        startActivity(showListViewIntent);
    }

    public void showTwoWayView(View view) {
        Intent showTwoWayViewIntent = new Intent(this, FixedTwoWayActivity.class);
        startActivity(showTwoWayViewIntent);
    }
}
