package com.desmond.allaboutrecyclerview.customLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by desmond on 9/3/15.
 */
public class FixedGridLayoutManager extends RecyclerView.LayoutManager {

    public static final String TAG = FixedGridLayoutManager.class.getSimpleName();

    private static final int DEFAULT_COUNT = 1;

    /* View Removal Constants */
    private static final int REMOVE_VISIBLE = 0;
    private static final int REMOVE_INVISIBLE = 1;

    /* Fill Direction Constants */
    private static final int DIRECTION_NONE = -1;
    private static final int DIRECTION_START = 0;
    private static final int DIRECTION_END = 1;
    private static final int DIRECTION_UP = 2;
    private static final int DIRECTION_DOWN = 3;

    /* First (top-left) position visible at any point */
    private int mFirstVisiblePosition;

    /* Consistent size applied to all child views */
    private int mDecoratedChildWidth;
    private int mDecoratedChildHeight;

    /* Number of columns that exist in the grid */
    private int mTotalColumnCount = DEFAULT_COUNT;

    /* Metrics for the visible window of our data */
    private int mVisibleColumnCount;
    private int mVisibleRowCount;

    /* Flag to force current scroll offsets to be ignored on re-layout */
    private boolean mForceClearOffsets;

    /* Used for tracking off-screen change events */
    private int mFirstChangedPosition;
    private int mChangedPositionCount;

    /**
     * This method is your initial call from the framework. You will receive it when you
     * need to start laying out the initial set of views. This method will not be called
     * repeatedly, so don't rely on it to continually process changes during user
     * interaction.
     *
     * This method will be called when the data set in the adapter changes, so it can be
     * used to update a layout based on a new item count.
     *
     * If predictive animations are enabled, you will see this called twice. First, with
     * state.isPreLayout() returning true to lay out children in their initial conditions.
     * Then again to lay out children in their final locations.
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // TODO

        // First or empty layout
        if (getChildCount() == 0) {
            // Scrap measure one child
            View scrap = recycler.getViewForPosition(0);
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);

            /*
             * We make some assumptions in this code based on every child view
             * being the same size (uniform grid). This allows us to compute
             * the following values upfront because they won't change
             */
            mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
            mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);

            /*
             * Remove views you want to temporarily reorganize
             * & expect to re-attach inside the same layout pass
             */
            detachAndScrapView(scrap, recycler);
        }

        // Always update the visible row/column counts
        updateWindowSizing();

        SparseIntArray removeCache = null;
        /*
         * During pre-layout, we need to take note of any views that are
         * being removed in order to handle predictive animations
         */
        if (state.isPreLayout()) {

        }

        int childLeft = 0, childTop = 0;
        // First or empty layout
        if (state.isPreLayout() && getChildCount() == 0) {
            // Reset the visible and scroll positions
            mFirstVisiblePosition = 0;
            childLeft = childTop = 0;
        } else if (!state.isPreLayout() && getVisibleChildCount() >= getItemCount()) {
            // Data set is too small to scroll fully, just reset position
            mFirstVisiblePosition = 0;
            childLeft = childTop = 0;
        } else {
            // Adapter data set changes

            /*
             * Keep the existing initial position, & save off
             * the current scrolled offset
             */

        }

        // Clear all attached views into the recycle bin
        detachAndScrapAttachedViews(recycler);

        // Fill the grid for the initial layout of views
        fillGrid(DIRECTION_DOWN, childLeft, childTop, recycler, state.isPreLayout(), removeCache);
    }

    /**
     * Even without extending LayoutParams, we must override this method
     * to provide the default layout parameters that each child view
     * will receive when added
     *
     * @return new instance of the RecyclerView.LayoutParams that you want applied by default
     *         to all the child views returned from the Recycler.
     *         These parameters will be applied to each child before they return from #getViewForPosition()
     */
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Rather than continuously checking how many views we can fit based on scroll offsets,
     * we simplify the math by computing the visible grid as what will initially fit on screen,
     * plus one
     */
    private void updateWindowSizing() {
        mVisibleColumnCount = (getHorizontalSpace() / mDecoratedChildWidth) + 1;
        if (getHorizontalSpace() % mDecoratedChildWidth > 0) {
            mVisibleColumnCount++;
        }

        // Allow min value for small data sets
        if (mVisibleColumnCount > getTotalColumnCount()) {
            mVisibleColumnCount = getTotalColumnCount();
        }

        mVisibleRowCount = (getVerticalSpace() / mDecoratedChildHeight) + 1;
        if (mVisibleRowCount % mDecoratedChildHeight > 0) {
            mVisibleRowCount++;
        }

        if (mVisibleRowCount > getTotalRowCount()) {
            mVisibleRowCount = getTotalRowCount();
        }
    }

    private void fillGrid(int direction, RecyclerView.Recycler recycler) {
        fillGrid(direction, 0, 0, recycler, false, null);
    }

    private void fillGrid(int direction, int emptyLeft, int emptyTop, RecyclerView.Recycler recycler,
                          boolean preLayout, SparseIntArray removedPositions) {
        if (mFirstVisiblePosition < 0) mFirstVisiblePosition = 0;
        if (mFirstVisiblePosition >= getItemCount()) mFirstVisiblePosition = (getItemCount() - 1);

        /*
         * First, we will detach all existing views from the layout.
         * #detachView() is a lightweight operation that we can use to
         * quickly reorder views without a full add/remove
         */
        SparseArray<View> viewCache = new SparseArray<>(getChildCount());
        int startLeftOffset = getPaddingLeft() + emptyLeft;
        int startTopOffset = getPaddingTop() + emptyTop;
        if (getChildCount() != 0) {
            // TODO
        }
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private int getFirstVisibleColumn() {
        return (mFirstVisiblePosition % getTotalColumnCount());
    }

    private int getLastVisibleColumn() {
        return getFirstVisibleColumn() + mVisibleColumnCount;
    }

    private int getFirstVisibleRow() {
        return (mFirstVisiblePosition / getTotalColumnCount());
    }

    private int getLastVisibleRow() {
        return getFirstVisibleRow() + mVisibleRowCount;
    }

    private int getVisibleChildCount() {
        return mVisibleColumnCount * mVisibleRowCount;
    }

    private int getTotalColumnCount() {
        if (getItemCount() < mTotalColumnCount) {
            return getItemCount();
        }

        return mTotalColumnCount;
    }

    private int getTotalRowCount() {
        if (getItemCount() == 0 || mTotalColumnCount == 0) {
            return 0;
        }

        int maxRow = getItemCount() / mTotalColumnCount;
        // Bump the row count if it's not exactly even
        if (getItemCount() % mTotalColumnCount != 0) {
            maxRow++;
        }

        return maxRow;
    }
}
