package com.ramotion.cardslider;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class CardSliderLayoutManager extends RecyclerView.LayoutManager {

    private static final boolean DEBUG = true;

    private static final float VIEW_WIDTH_PERCENT = 0.3f;
    private static final float VIEW_HEIGHT_PERCENT = 1f;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        fillLeft(recycler);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int delta = scrollHorizontallyInternal(dx);
        offsetChildrenHorizontal(-delta);
        return delta;
    }

    private void fillLeft(RecyclerView.Recycler recycler) {
        final int viewWidth = (int) (getWidth() * VIEW_WIDTH_PERCENT);
        final int viewHeight = (int) (getHeight() * VIEW_HEIGHT_PERCENT);

        final int itemCount = getItemCount();
        final int width = getWidth();

        int pos = 0;
        int viewLeft = 0;
        boolean fillLeft = true;

        while (fillLeft && pos < itemCount) {
            final View view = recycler.getViewForPosition(pos);
            addView(view);

            measureChildWithMargins(view, 0, 0);
            layoutDecorated(view, viewLeft, 0, viewLeft + viewWidth, viewHeight);

            viewLeft = getDecoratedRight(view);
            fillLeft = viewLeft <= width;
            pos++;
        }
    }

    private int scrollHorizontallyInternal(int dx) {
        final int childCount = getChildCount();
        final int itemCount = getItemCount();

        if (childCount == 0){
            return 0;
        }

        int delta = 0;

        if (dx < 0) {
            final View firstView = getChildAt(0);
            final boolean isFirstItem = getPosition(firstView) == 0;
            if (isFirstItem) {
                delta = Math.max(getDecoratedLeft(firstView), dx);
            } else {
                delta = dx;
            }
        } else if (dx > 0) {
            final View lastView = getChildAt(childCount - 1);
            final boolean isLastItem = getPosition(lastView) == itemCount - 1;
            if (isLastItem) {
                delta = Math.min(getDecoratedRight(lastView), getWidth());
            } else {
                delta = dx;
            }
        }

        return delta;
    }

}
