package com.desmond.allaboutrecyclerview.customLayoutManager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;

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
    private static final int DIRECTION_LEFT = 0;
    private static final int DIRECTION_RIGHT = 1;
    private static final int DIRECTION_UP = 2;
    private static final int DIRECTION_DOWN = 3;

    /* First (top-left) global position visible at any point */
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
     * Set the number of columns the layout manager will use. This will
     * trigger a layout update.
     * @param count Number of columns.
     */
    public void setTotalColumnCount(int count) {
        mTotalColumnCount = count;
        requestLayout();
    }

    /**
     * You must return true from this method if you want your
     * LayoutManager to support anything beyond "simple" item
     * animations. Enabling this causes onLayoutChildren() to
     * be called twice on each animated change; once for a
     * pre-layout, and again for the real layout.
     */
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    /**
     * Called by RecyclerView when a view removal is triggered. This is called
     * before onLayoutChildren() in pre-layout if the views removed are not visible. We
     * use it in this case to inform pre-layout that a removal took place.
     *
     * This method is still called if the views removed were visible, but it will
     * happen AFTER pre-layout.
     * @param recyclerView
     * @param positionStart
     * @param itemCount
     */
    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        mFirstChangedPosition = positionStart;
        mChangedPositionCount = itemCount;
    }

    /**
     * These methods are called before onLayoutChildren() in pre-layout if the views involved
     * are not visible. Use these methods to keep track of the variables require
     *
     * These methods are still called if the views involved are visible, but AFTER pre-layout
     */
    @Override
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        super.onItemsMoved(recyclerView, from, to, itemCount);
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        super.onItemsChanged(recyclerView);
    }

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
     *
     * In preLayout, set up the initial conditions for the change animation.
     * This means that you need to lay out all the views that are currently visible before
     * the change AND any additional views that will be visible after the animation runs (these are termed APPEARING views)
     * These extra appearing views should be laid out in the off-screen positions where
     * the user would expect them to be coming from
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // Nothing to show for an empty data set but clear any existing views
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        // Clear change tracking state when a real layout occurs
        if (!state.isPreLayout()) {
            mFirstChangedPosition = mChangedPositionCount = 0;
        }

        // First or empty layout
        if (getChildCount() == 0) {
            // Scrap measure one child
            View scrap = recycler.getViewForPosition(0);
            addView(scrap);
            // Use this instead of child.measure() to measure views coming from Recycler
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

        SparseIntArray removedCache = null;
        /*
         * During pre-layout, we need to take note of any views that are
         * being removed in order to handle predictive animations
         */
        if (state.isPreLayout()) {
            removedCache = new SparseIntArray(getChildCount());
            for (int i = 0; i < getChildCount(); i++) {
                // View is in the "old" position before the data set changes
                final View view = getChildAt(i);
                LayoutParams lp = (LayoutParams) view.getLayoutParams();

                if (lp.isItemRemoved()) {
                    // Track these view removals as visible
                    removedCache.put(lp.getViewLayoutPosition(), REMOVE_VISIBLE);
                }
            }
            // TODO: Check if this method should be placed inside state.isPreLayout()
            // Track view removals that happened out of bounds (i.e. off-screen)
            if (removedCache.size() == 0 && mChangedPositionCount > 0) {
                for (int i = mFirstChangedPosition; i < mFirstChangedPosition + mChangedPositionCount; ++i) {
                    removedCache.put(i, REMOVE_INVISIBLE);
                }
            }
        }

        int childLeft, childTop;
        if (!state.isPreLayout() && getChildCount() == 0) {
            // First or empty layout
            // Reset the visible and scroll positions
            mFirstVisiblePosition = 0;
            childLeft = childTop = 0;
        } else if (!state.isPreLayout() && getVisibleChildCount() >= getItemCount()) {
            // Data set is too small to be scrolled fully, just reset position
            mFirstVisiblePosition = 0;
            childLeft = childTop = 0;
        } else {
            // Adapter data set changes

            /*
             * Keep the existing initial position, & save off
             * the current scrolled offset
             */
            final View topChild = getChildAt(0);
            if (mForceClearOffsets) {
                childLeft = childTop = 0;
                mForceClearOffsets  = false;
            } else {
                childLeft = getDecoratedLeft(topChild);
                childTop = getDecoratedTop(topChild);
            }

            /*
             * When the new data set is too small to be scrolled vertically, adjust vertical offset
             * and shift position to the first row, preserving current column
             */
            if (!state.isPreLayout() && getVerticalSpace() > (getTotalRowCount() * mDecoratedChildHeight)) {
                mFirstVisiblePosition = mFirstVisiblePosition % getTotalColumnCount();
                childTop = 0;

                // If the shift over-scrolled the column max, back it off
                if (mFirstVisiblePosition + mVisibleColumnCount > getItemCount()) {
                    mFirstVisiblePosition = Math.max(getItemCount() - mVisibleColumnCount, 0);
                    childLeft = 0;
                }
            }

            /*
             * Adjust the visible positions if out of bounds in the new layout.
             * This occurs when the new item count in an adapter is much
             * smaller than it was before, and you scrolled to a location
             * where no items would exist
             */
            int maxFirstRow = getTotalRowCount() - (mVisibleRowCount - 1);
            int maxFirstCol = getTotalColumnCount() - (mVisibleColumnCount - 1);
            boolean isOutOfRowBounds = getFirstVisibleRow() > maxFirstRow;
            boolean isOutOfColBounds = getFirstVisibleColumn() > maxFirstCol;

            if (isOutOfColBounds || isOutOfRowBounds) {
                int firstRow;
                if (isOutOfRowBounds) firstRow = maxFirstRow;
                else                  firstRow = getFirstVisibleRow();

                int firstCol;
                if (isOutOfColBounds) firstCol = maxFirstCol;
                else                  firstCol = getFirstVisibleColumn();

                mFirstVisiblePosition = firstRow * getTotalColumnCount() + firstCol;

                childLeft = getHorizontalSpace() - (mDecoratedChildWidth * mVisibleColumnCount);
                childTop = getVerticalSpace() - (mDecoratedChildHeight * mVisibleRowCount);

                /*
                 * Correct cases where shifting to the bottom right overscrolls the top-left
                 * This happens on data sets too small to scroll in a direction
                 */
                if (getFirstVisibleRow() == 0) childTop = Math.min(childTop, 0);

                if (getFirstVisibleColumn() == 0) childLeft = Math.min(childLeft, 0);
            }
        }

        // Clear all attached views into the recycle bin
        detachAndScrapAttachedViews(recycler);

        // Fill the grid for the initial layout of views
        fillGrid(DIRECTION_NONE, childLeft, childTop, recycler, state.isPreLayout(), removedCache);

        // Evaluate any disappearing views that may exist, they are attached during the preLayout
        // Hence, the views are not being recycled from the scrapList
        if (!state.isPreLayout() && !recycler.getScrapList().isEmpty()) {

            // Views still in scrap at this point after #fillGrid() that
            // aren’t considered removed from the data set are disappearing views
            // Lay these views out in their off-screen positions so the animation system
            // can slide them out of view (instead of just fading them out).
            final List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
            final HashSet<View> disappearingViews = new HashSet<>(scrapList.size());

            for (RecyclerView.ViewHolder viewHolder : scrapList) {
                final View child = viewHolder.itemView;
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (!lp.isItemRemoved()) {
                    disappearingViews.add(child);
                }
            }

            for (View child : disappearingViews) {
                layoutDisappearingView(child);
            }
        }
    }



    /**
     * Swapping of the entire adapter, instead of changing the data in the adapter
     * @param oldAdapter
     * @param newAdapter
     */
    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        // Completely scrap the existing layout
        removeAllViews();
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

    /**
     * Layout the child views of the RecyclerView
     * @param direction Direction of fill
     * @param emptyLeft Left of first visible child, without padding
     * @param emptyTop  Top of first visible child, without padding
     * @param recycler
     * @param preLayout
     * @param removedPositions  positions of views (visible & invisible) slated for removal
     */
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
            final View topView = getChildAt(0);
            // Take into account decoration
            startLeftOffset = getDecoratedLeft(topView);
            startTopOffset = getDecoratedTop(topView);

            switch (direction) {
                case DIRECTION_LEFT:
                    startLeftOffset -= mDecoratedChildWidth;
                    break;
                case DIRECTION_RIGHT:
                    startLeftOffset += mDecoratedChildWidth;
                    break;
                case DIRECTION_UP:
                    startTopOffset -= mDecoratedChildHeight;
                    break;
                case DIRECTION_DOWN:
                    startTopOffset += mDecoratedChildHeight;
                    break;
            }

            // Cache all current number of child views attached to the parent RecyclerView,
            // by their existing position, before updating counts
            for (int i = 0; i < getChildCount(); i++) {
                int positionOfChild = globalViewPositionOfIndex(i);
                final View child = getChildAt(i);
                viewCache.put(positionOfChild, child);
            }

            // Temporarily detach all current number of child views attached to the parent RecyclerView,
            // Views we still need will be added back at the proper index
            for (int i = 0; i < viewCache.size(); i++) {
                // viewCache.valueAt() != viewCache.get()
                detachView(viewCache.valueAt(i));
            }
        }

        /*
         * Next, advance the visible position based on the fill direction
         * DIRECTION_NONE doesn't advance the position in any direction
         */
        switch (direction) {
            case DIRECTION_LEFT:
                mFirstVisiblePosition--;
                break;
            case DIRECTION_RIGHT:
                mFirstVisiblePosition++;
                break;
            case DIRECTION_UP:
                mFirstVisiblePosition -= getTotalColumnCount();
                break;
            case DIRECTION_DOWN:
                mFirstVisiblePosition += getTotalColumnCount();
                break;
        }

        /*
         * Supply the grid of items that are deemed visible.
         * If these items were previously there, they will simply be
         * re-attached. New views that must be created are obtained
         * from the Recycler & added
         */
        int leftOffset = startLeftOffset;
        int topOffset = startTopOffset;

        for (int i = 0; i < getVisibleChildCount(); i++) {
            int globalChildViewPos = globalViewPositionOfIndex(i);

            /*
             * When a removal happens out of bounds, the pre-layout positions of items
             * after the removal are shifted to their final position ahead of schedule.
             * We have to track off-screen removals & shift those positions back so we
             * can properly lay out all current (& appearing) views in their initial locations
             */
            int offsetPositionDelta = 0;
            if (preLayout) {
                int offsetPosition = globalChildViewPos;

                for (int offset = 0; offset < removedPositions.size(); offset++) {
                    // Look for off-screen removals that are less-than this
                    if (removedPositions.valueAt(offset) == REMOVE_INVISIBLE
                            && removedPositions.keyAt(offset) < globalChildViewPos) {
                        // Offset position to match
                        offsetPosition--;
                    }
                }
                offsetPositionDelta = globalChildViewPos - offsetPosition;
                globalChildViewPos = offsetPosition;
            }

            if (globalChildViewPos < 0 || globalChildViewPos >= getItemCount()) {
                // Item space beyond the data set, don't attempt to add a view
                continue;
            }

            // Layout this position
            View view = viewCache.get(globalChildViewPos);
            if (view == null) {
                /*
                 * The Recycler will give us either a newly constructed view, or
                 * a recycled view it has on-hand. In either case, the view will
                 * already be fully bounded to the data by the adapter
                 */
                view = recycler.getViewForPosition(globalChildViewPos);
                addView(view);

                /*
                 * Update the new view's metadata, but only when this is a real
                 * layout pass
                 */
                if (!preLayout) {
                    LayoutParams lp = (LayoutParams) view.getLayoutParams();
                    lp.row = getGlobalRowOfPosition(globalChildViewPos);
                    lp.column = getGlobalColumnOfPosition(globalChildViewPos);
                }

                /*
                 * It is prudent to measure/layout each new view we
                 * receive from the Recycler. We don't have to do
                 * this for views we are just re-arranging.
                 */
                measureChildWithMargins(view, 0, 0);
                // Use this instead of child.layout() to layout views coming from Recycler
                layoutDecorated(view, leftOffset, topOffset,
                        leftOffset + mDecoratedChildWidth,
                        topOffset + mDecoratedChildHeight);
            } else {
                // Re-attach the cached view at its new index
                attachView(view);
                viewCache.remove(globalChildViewPos);
            }

            // At each column end
            if (i % mVisibleColumnCount == (mVisibleColumnCount - 1)) {
                leftOffset = startLeftOffset;
                topOffset += mDecoratedChildHeight;

                // During pre-layout, on each column end, apply any additional appearing views
                if (preLayout) {
                    layoutAppearingViews(recycler, view, globalChildViewPos, removedPositions.size(), offsetPositionDelta);
                }
            } else {
                leftOffset += mDecoratedChildWidth;
            }
        }

        /*
         * Finally, we ask the Recycler to scrap and store any views
         * that we did not re-attach. These are views that are not currently
         * necessary because they are no longer visible.
         */
        for (int i = 0; i < viewCache.size(); i++) {
            final View removingView = viewCache.valueAt(i);
            recycler.recycleView(removingView);
        }
    }

    /**
     * You must override this method if you would like to support external calls
     * to shift the view to a given adapter position. In our implementation, this
     * is the same as doing a fresh layout with the given position as the top-left
     * (or first visible), so we simply set that value and trigger onLayoutChildren()
     */
    @Override
    public void scrollToPosition(int position) {
        if (position >= getItemCount()) {
            Log.e(TAG, "Cannot scroll to " + position + ", item count is " + getItemCount());
            return;
        }

        // Ignore current scroll offset, snap to top-left
        mForceClearOffsets = true;
        // Set requested position as first visible
        mFirstVisiblePosition = position;
        // Trigger a new view layout
        requestLayout();
    }

    /**
     * You must override this method if you would like to support external calls
     * to animate a change to a new adapter position. The framework provides a
     * helper scroller implementation (LinearSmoothScroller), which we leverage
     * to do the animation calculations.
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
        if (position >= getItemCount()) return;

        /*
         * LinearSmoothScroller's default behaviour is to scroll the contents until the child
         * is fully visible. It will snap to the top-left or bottom-right of the parent
         * depending on whether the direction of travel was positive or negative
         */
        LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {

            /*
             * LinearSmoothScroller, at a min, just needs to know the vector
             * (x/y distance) to travel in order to get from the current positioning
             * to the target
             */
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {

                // To return the initial direction of scroll and approximate distance it needs to
                // travel to get from its current location to the target locationØ
                final int rowOffset = getGlobalRowOfPosition(targetPosition)
                        - getGlobalRowOfPosition(mFirstVisiblePosition);
                final int colOffset = getGlobalColumnOfPosition(targetPosition)
                        - getGlobalColumnOfPosition(mFirstVisiblePosition);

                return new PointF(colOffset * mDecoratedChildWidth, rowOffset * mDecoratedChildHeight);
            }
        };
        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    /**
     * This method describes how far RecyclerView thinks that the contents should be scrolled
     * horizontally. You're responsible for verifying edge boundaries, and determining if this
     * scroll event somehow requires that new views be added or old views get recycled
     *
     * @return if value doesn't exactly match the dx passed in, expect some amount of edge glow to be drawn
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0) return 0;

        // Take leftmost measurements from the top-left child
        final View topView = getChildAt(0);
        // Take rightmost measurements from the top-right child
        final View bottomView = getChildAt(mVisibleColumnCount - 1);

        // Optimize the case where the entire data set is too small to scroll
        int viewSpan = getDecoratedRight(bottomView) - getDecoratedLeft(topView);
        if (viewSpan < getHorizontalSpace()) return 0;

        int delta;
        boolean leftBoundReached = getFirstVisibleColumn() == 0;
        boolean rightBoundReached = getLastVisibleColumn() >= getTotalColumnCount();

        if (dx > 0) { // Scroll right
            if (rightBoundReached) {
                /* Reached the last column, enforce limits
                 * rightOffset should be 0 when the last col is fully visible
                 * else, negative value
                 */
                int rightOffset = getHorizontalSpace() - getDecoratedRight(bottomView) + getPaddingRight();
                delta = Math.max(-dx, rightOffset);
            } else {
                // No limits while the last column isn't visible
                delta = -dx;
            }
        } else { // Scroll left
            if (leftBoundReached) {
                int leftOffset = -getDecoratedLeft(topView) + getPaddingLeft();
                delta = Math.min(-dx, leftOffset);
            } else {
                delta = -dx;
            }
        }

        // Manually move the views
        offsetChildrenHorizontal(delta);

        // Trigger another fill operation to swap views based on the direction scrolled.
        if (dx > 0) {
            if (getDecoratedRight(topView) < 0 && !rightBoundReached) {
                fillGrid(DIRECTION_RIGHT, recycler);
            } else if (!rightBoundReached) {
                fillGrid(DIRECTION_NONE, recycler);
            }
        } else {
            if (getDecoratedLeft(topView) > 0 && !leftBoundReached) {
                fillGrid(DIRECTION_LEFT, recycler);
            } else if (!leftBoundReached) {
                fillGrid(DIRECTION_NONE, recycler);
            }
        }

        /*
         * Return value determines if a boundary has been reached
         * (for edge effects and flings). If returned value doesn't
         * match original delta (dx), RecyclerView will draw an edge effect
         */
        return -delta;
    }

    /**
     * This method describes how far RecyclerView thinks that the contents should be scrolled
     * vertically.
     *
     * @return if value doesn't exactly match the dy passed in, expect some amount of edge glow to be drawn
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0) return 0;

        // Take top measurements from the top-left child
        final View topView = getChildAt(0);
        // Take bottom measurements from the bottom-right child
        final View bottomView = getChildAt(getChildCount() - 1);

        // Optimize the case where the entire data set is too small to scroll
        int viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
        if (viewSpan < getVerticalSpace()) {
            // We cannot scroll in either direction
            return 0;
        }

        int delta;
        int maxRowCount = getTotalRowCount();
        boolean topBoundReached = getFirstVisibleRow() == 0;
        boolean bottomBoundReached = getLastVisibleRow() >= maxRowCount;

        if (dy > 0) { // Scroll down

            if (bottomBoundReached) {
                int bottomOffset;

                // Reached the last row, enforce limits
                if (rowOfIndex(getChildCount() - 1) >= (maxRowCount - 1)) {
                    // Truly at the bottom, determine how far
                    // bottomOffset should be 0 when the last row is fully visible
                    bottomOffset = getVerticalSpace() - getDecoratedBottom(bottomView)
                            + getPaddingBottom();
                } else {
                    /*
                     * Extra space added to account for allowing bottom space in the grid.
                     * This occurs when the overlap in the last row is not large enough to
                     * ensure that at least one element in that row isn't fully recycled.
                     */
                    bottomOffset = getVerticalSpace() - (getDecoratedBottom(bottomView)
                            + mDecoratedChildHeight) + getPaddingBottom();
                }

                delta = Math.max(-dy, bottomOffset);
            } else {
                // No limits while the last row isn't visible
                delta = -dy;
            }
        } else { // Scroll up
            if (topBoundReached) {
                int topOffset = -getDecoratedTop(topView) + getPaddingTop();
                delta = Math.min(-dy, topOffset);
            } else {
                delta = -dy;
            }
        }

        // Manually move the views
        offsetChildrenVertical(delta);

        // Trigger another fill operation to swap views based on the direction scrolled.
        if (dy > 0) {
            if (getDecoratedBottom(topView) < 0 && !bottomBoundReached) {
                // Continue to scroll down
                fillGrid(DIRECTION_DOWN, recycler);
            } else if (!bottomBoundReached) {
                // No more scrolling
                fillGrid(DIRECTION_NONE, recycler);
            }
        } else {
            if (getDecoratedTop(topView) > 0 && !topBoundReached) {
                // Continue to scroll up
                fillGrid(DIRECTION_UP, recycler);
            } else if (!topBoundReached) {
                fillGrid(DIRECTION_NONE, recycler);
            }
        }

        /*
         * Return value determines if a boundary has been reached
         * (for edge effects and flings). If returned value does not
         * match original delta (passed in), RecyclerView will draw
         * an edge effect.
         */
        return -delta;
    }

    /**
     * Mapping between child views global positions and adapter data
     * positions helps fill the proper views during scrolling
     */
    private int globalViewPositionOfIndex(int childIndex) {
        int row = childIndex / mVisibleColumnCount;
        int column = childIndex % mVisibleColumnCount;

        return mFirstVisiblePosition + (row * getTotalColumnCount()) + column;
    }

    private int rowOfIndex(int childIndex) {
        int position = globalViewPositionOfIndex(childIndex);

        return position / getTotalColumnCount();
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

    /**
     * Return the overall column index of this position in the global layout
     **/
    private int getGlobalColumnOfPosition(int position) {
        return position % mTotalColumnCount;
    }

    /**
     *  Return the overall row index of this position in the global layout
     **/
    private int getGlobalRowOfPosition(int position) {
        return position / mTotalColumnCount;
    }

    /** Animation Layout Helpers */

    /**
     *  Helper to obtain and place extra appearing views
     **/
    private void layoutAppearingViews(RecyclerView.Recycler recycler, View referenceView,
                                      int referenceGlobalPosition, int extraCount, int offset) {
        // No appearing views so nothing to do...
        if (extraCount < 1) return;

        for (int extra = 1; extra <= extraCount; extra++) {
            // Grab the next position after the reference
            // globalPosOfAppearingView is the global position of the views after appearing
            final int globalPosOfAppearingView = referenceGlobalPosition + extra;
            if (globalPosOfAppearingView < 0 || globalPosOfAppearingView >= getItemCount()) {
                // Can't do anything with this
                continue;
            }

            /*
             * Obtain additional position views that we expect to appear
             * as part of the animation.
             */
            View appearing = recycler.getViewForPosition(globalPosOfAppearingView);
            addView(appearing);

            // Find layout delta from reference position
            final int newRow = getGlobalRowOfPosition(globalPosOfAppearingView + offset);
            final int rowDelta = newRow - getGlobalRowOfPosition(referenceGlobalPosition + offset);
            final int newCol = getGlobalColumnOfPosition(globalPosOfAppearingView + offset);
            final int colDelta = newCol - getGlobalColumnOfPosition(referenceGlobalPosition + offset);

            layoutTempChildView(appearing, rowDelta, colDelta, referenceView);
        }
    }

    /**
     * Helper to place a disappearing view
     **/
    private void layoutDisappearingView(View disappearingChild) {
        /*
         * LayoutManager has a special method for attaching views that
         * will only be around long enough to animate.
         */
        addDisappearingView(disappearingChild);

        // Adjust each disappearing view to its proper place
        final LayoutParams lp = (LayoutParams) disappearingChild.getLayoutParams();

        final int newRow = getGlobalRowOfPosition(lp.getViewLayoutPosition());
        final int rowDelta = newRow - lp.row;
        final int newCol = getGlobalColumnOfPosition(lp.getViewLayoutPosition());
        final int colDelta = newCol - lp.column;

        layoutTempChildView(disappearingChild, rowDelta, colDelta, disappearingChild);
    }

    /**
     * Helper to lay out appearing/disappearing children
     **/
    private void layoutTempChildView(View child, int rowDelta, int colDelta, View referenceView) {
        //Set the layout position to the global row/column difference from the reference view
        int layoutTop = getDecoratedTop(referenceView) + rowDelta * mDecoratedChildHeight;
        int layoutLeft = getDecoratedLeft(referenceView) + colDelta * mDecoratedChildWidth;

        measureChildWithMargins(child, 0, 0);
        layoutDecorated(child, layoutLeft, layoutTop,
                layoutLeft + mDecoratedChildWidth,
                layoutTop + mDecoratedChildHeight);
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
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
        } else {
            return new LayoutParams(lp);
        }
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {

        // Current row in the grid
        public int row;

        // Current column in the grid
        public int column;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
        public LayoutParams(int width, int height) {
            super(width, height);
        }
        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
        }
    }
}
