package com.ramotion.cardslider;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

public class CardSliderLayoutManager extends RecyclerView.LayoutManager {

    private static final boolean DEBUG = true;

    private final SparseArray<View> viewCache = new SparseArray<>();

    private SliderParams params;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (params == null) {
            params = new SliderParams(this);
        }

        detachAndScrapAttachedViews(recycler);
        fill(recycler);
    }

    @Override
    public boolean canScrollHorizontally() {
        return getChildCount() != 0;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int delta = scrollHorizontallyInternal(dx);
        offsetChildrenHorizontal(-delta);
        fill(recycler);
        return delta;
    }

    private int scrollHorizontallyInternal(int dx) {
        // TODO: fix scroll left

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

    @Nullable
    private View getAnchorView() {
        if (getChildCount() == 0) {
            return null;
        }

        View view = null;
        final int childCount = getChildCount();

        for (int i = 0; i < childCount - 1; i++) {
            view = getChildAt(i);
            final int right = getDecoratedRight(view);
            if (right > params.activeCardLeft && right < params.activeCardRight) {
                break;
            }
        }

        return view;
    }

    private void fill(RecyclerView.Recycler recycler) {
        final View anchorView = getAnchorView();

        viewCache.clear();

        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }

        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }

        fillLeft(anchorView, recycler);
        fillRight(anchorView, recycler);

        for (int i=0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }

        updateViewScale();
    }

    private void fillLeft(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        if (anchorView == null) {
            return;
        }

        final int stackedCount = 2;

        int anchorPos = getPosition(anchorView);
        int pos = Math.max(0, anchorPos - stackedCount);
        int viewLeft = 0;
        int anchorViewLeft = getDecoratedLeft(anchorView);
        int layoutStep = (anchorViewLeft - viewLeft) / stackedCount;

        while (pos < anchorPos) {
            View view = viewCache.get(pos);
            if (view != null) {
                attachView(view);
                viewCache.remove(pos);
            } else {
                view = recycler.getViewForPosition(pos);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                final int viewHeight = getDecoratedMeasuredHeight(view);
                layoutDecorated(view, viewLeft, 0, viewLeft + params.cardWidth, viewHeight);
            }

            viewLeft += layoutStep;
            pos++;
        }

    }

    private void fillRight(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        final int width = getWidth();
        final int itemCount = getItemCount();

        int pos = 0;
        int viewLeft = params.activeCardLeft;
        if (anchorView != null) {
            pos = getPosition(anchorView);
            viewLeft = getDecoratedLeft(anchorView);
        }

        boolean fillRight = true;
        while (fillRight && pos < itemCount) {
            View view = viewCache.get(pos);
            if (view != null) {
                attachView(view);
                viewCache.remove(pos);
            } else {
                view = recycler.getViewForPosition(pos);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                final int viewHeight = getDecoratedMeasuredHeight(view);
                layoutDecorated(view, viewLeft, 0, viewLeft + params.cardWidth, viewHeight);
            }

            viewLeft = getDecoratedRight(view);
            fillRight = viewLeft < width;
            pos++;
        }
    }

    private void updateViewScale() {
        final int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View view = getChildAt(i);
            final int viewLeft = getDecoratedLeft(view);

            if (viewLeft < params.activeCardLeft) {
                // TODO: scale down

                final float minScale = 0.7f;
                final float maxScale = 0.95f;
                final float totalScale = maxScale - minScale;

                final float ratio = (float) viewLeft / params.activeCardLeft;
                final float scale = minScale + totalScale * ratio;
                ViewCompat.setScaleX(view, scale);
                ViewCompat.setScaleY(view, scale);
//                ViewCompat.setAlpha(view, ratio);

                if (DEBUG) {
                    Log.d("D", "viewLeft < leftBorder: " + i);
                    Log.d("D", "ratio: " + ratio);
                    Log.d("D", "scale: " + scale);
                }
            } else if (viewLeft < params.activeCardCenter) {
                // TODO: no scale - anchorView
                // TODO: restore alpha
                if (DEBUG) {
                    Log.d("D", "viewLeft < center: " + i);
                }
                ViewCompat.setScaleX(view, 0.95f);
                ViewCompat.setScaleY(view, 0.95f);
                ViewCompat.setZ(view, 10);
            } else if (viewLeft < params.activeCardRight) {
                // TODO: scale up
                if (DEBUG) {
                    Log.d("D", "viewLeft < rightBorder: " + i);
                }
            } else if (viewLeft >= params.activeCardRight) {
                if (DEBUG) {
                    Log.d("D", "viewLeft >= rightBorder: " + i);
                }

                ViewCompat.setScaleX(view, 0.8f);
                ViewCompat.setScaleY(view, 0.8f);
                ViewCompat.setZ(view, 5);
            }
        }
    }

}

class SliderParams{

    private static final float VIEW_WIDTH_PERCENT = 0.35f;
    private static final float LEFT_BORDER_PERCENT = 0.1f;

    final int cardWidth;

    final int activeCardLeft;
    final int activeCardRight;
    final int activeCardCenter;

    SliderParams(RecyclerView.LayoutManager lm) {
        final int width = lm.getWidth();
        cardWidth = (int) (width * VIEW_WIDTH_PERCENT);

        activeCardLeft = (int) (width * LEFT_BORDER_PERCENT);
        activeCardRight = activeCardLeft + (int) (width * VIEW_WIDTH_PERCENT);
        activeCardCenter = activeCardLeft + ((activeCardRight - activeCardLeft) / 2);
    }

}