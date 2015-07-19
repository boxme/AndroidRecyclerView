package com.desmond.allaboutrecyclerview.fixedTwoWayView;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.desmond.allaboutrecyclerview.R;
import com.desmond.allaboutrecyclerview.customLayoutManager.TwoWayGridLayoutManager;
import com.desmond.allaboutrecyclerview.decoration.InsertDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class FixedTwoWayFragment extends Fragment {

    public static final String TAG = FixedTwoWayFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private SimpleAdapter mAdapter;

    public static Fragment newInstance() {
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

        setHasOptionsMenu(true);

        mRecyclerView= (RecyclerView) view.findViewById(R.id.fixed_two_way_recyclerview);
        TwoWayGridLayoutManager manager = new TwoWayGridLayoutManager();
        manager.setTotalColumnCount(10);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addItemDecoration(new InsertDecoration(getActivity()));

        mRecyclerView.getItemAnimator().setAddDuration(1000);
        mRecyclerView.getItemAnimator().setChangeDuration(1000);
        mRecyclerView.getItemAnimator().setMoveDuration(1000);
        mRecyclerView.getItemAnimator().setRemoveDuration(1000);

        mAdapter = new SimpleAdapter();
        mAdapter.setItemCount(12);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fixed_two_way, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NumberPickerDialog dialog;
        switch (item.getItemId()) {
            case R.id.action_add:
                dialog = new NumberPickerDialog(getActivity());
                dialog.setTitle("Position to Add");
                dialog.setPickerRange(0, mAdapter.getItemCount() - 1);
                dialog.setOnNumberSelectedListener(new NumberPickerDialog.OnNumberSelectedListener() {
                    @Override
                    public void onNumberSelected(int value) {
                        mAdapter.addItem(value);
                    }
                });
                dialog.show();

                return true;
            case R.id.action_remove:
                dialog = new NumberPickerDialog(getActivity());
                dialog.setTitle("Position to Remove");
                dialog.setPickerRange(0, mAdapter.getItemCount() - 1);
                dialog.setOnNumberSelectedListener(new NumberPickerDialog.OnNumberSelectedListener() {
                    @Override
                    public void onNumberSelected(int value) {
                        mAdapter.removeItem(value);
                    }
                });
                dialog.show();

                return true;
            case R.id.action_empty:
                mAdapter.setItemCount(0);
                return true;
            case R.id.action_small:
                mAdapter.setItemCount(5);
                return true;
            case R.id.action_medium:
                mAdapter.setItemCount(25);
                return true;
            case R.id.action_large:
                mAdapter.setItemCount(196);
                return true;
            case R.id.action_scroll_zero:
                mRecyclerView.scrollToPosition(0);
                return true;
            case R.id.action_smooth_zero:
                mRecyclerView.smoothScrollToPosition(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class NumberPickerDialog extends AlertDialog implements DialogInterface.OnClickListener {

        public interface OnNumberSelectedListener {
            public void onNumberSelected(int value);
        }

        private NumberPicker mPicker;
        private OnNumberSelectedListener mNumberSelectedListener;

        public NumberPickerDialog(Context context) {
            super(context);
            mPicker = new NumberPicker(context);
        }

        protected NumberPickerDialog(Context context, int theme) {
            super(context, theme);
        }

        protected NumberPickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setButton(BUTTON_NEGATIVE, getContext().getString(android.R.string.cancel), this);
            setButton(BUTTON_POSITIVE, getContext().getString(android.R.string.ok), this);
            setView(mPicker);

            //Install contents
            super.onCreate(savedInstanceState);
        }

        public void setOnNumberSelectedListener(OnNumberSelectedListener listener) {
            mNumberSelectedListener = listener;
        }

        public void setPickerRange(int minValue, int maxValue) {
            mPicker.setMinValue(minValue);
            mPicker.setMaxValue(maxValue);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == BUTTON_POSITIVE && mNumberSelectedListener != null) {
                mNumberSelectedListener.onNumberSelected(mPicker.getValue());
            }
        }
    }
}
