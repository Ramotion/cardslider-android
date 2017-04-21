package com.ramotion.cardslider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.util.LinkedList;

public class CardSliderLayoutManager extends RecyclerView.LayoutManager {

    private static final boolean DEBUG = true;

    private static final int INVALID_VALUE = -1;

    private final SparseArray<View> viewCache = new SparseArray<>();

    private SliderParams params;

    private int requestedPosition = INVALID_VALUE;

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
    public void scrollToPosition(int position) {
        if (position > getItemCount()) {
            return;
        }

        requestedPosition = position;
        requestLayout();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        requestedPosition = INVALID_VALUE;

        int delta;
        if (dx < 0) {
            delta = scrollRight(dx);
        } else {
            delta = scrollLeft(dx);
        }

        fill(recycler);
        return delta;
    }

    private int scrollRight(int dx) {
        final int childCount = getChildCount();

        if (childCount == 0) {
            return 0;
        }

        final int delta = getAllowedRightDelta(getChildAt(childCount - 1), dx);

        final LinkedList<View> rightViews = new LinkedList<>();
        final LinkedList<View> leftViews = new LinkedList<>();

        for (int i = childCount - 1; i >= 0; i--) {
            final View view = getChildAt(i);
            final int viewLeft = getDecoratedLeft(view);

            if (viewLeft >= params.activeCardRight) {
                rightViews.add(view);
            } else {
                leftViews.add(view);
            }
        }

        for (View view: rightViews) {
            view.offsetLeftAndRight(-getAllowedRightDelta(view, dx));
        }

        View prevView = null;
        for (int i = 0, cnt = leftViews.size(); i < cnt; i++) {
            if (prevView == null || getDecoratedLeft(prevView) > params.activeCardRight) {
                final View view = leftViews.get(i);
                view.offsetLeftAndRight(-getAllowedRightDelta(view, dx));
                prevView = view;
            } else if (getDecoratedLeft(prevView) > params.activeCardCenter) {
                final int borderStep = params.activeCardLeft / SliderParams.LEFT_CARD_COUNT;
                int border = params.activeCardLeft;

                for (int j = i; j < cnt; j++) {
                    final View view = leftViews.get(j);
                    final int viewLeft = getDecoratedLeft(view);

                    if (viewLeft - delta >= border) {
                        view.offsetLeftAndRight(border - viewLeft);
                    } else {
                        view.offsetLeftAndRight(-delta);
                    }

                    border = Math.max(0, border - borderStep);
                }

                break;
            } else {
                break;
            }
        }

        return delta;
    }

    private int scrollLeft(int dx) {
        final int childCount = getChildCount();

        if (childCount == 0) {
            return 0;
        }

        int delta;

        final View lastView = getChildAt(childCount - 1);
        final boolean isLastItem = getPosition(lastView) == getItemCount() - 1;
        if (isLastItem) {
            delta = Math.min(dx, getDecoratedRight(lastView) - params.activeCardRight);
        } else {
            delta = dx;
        }

        final LinkedList<View> leftViews = new LinkedList<>();
        final LinkedList<View> centerViews = new LinkedList<>();

        for (int i = 0; i < childCount; i++) {
            final View view = getChildAt(i);
            final int viewLeft = getDecoratedLeft(view);

            if (viewLeft < params.activeCardLeft) {
                leftViews.add(view);
            } else if (viewLeft < params.activeCardRight) {
                centerViews.add(view);
            } else {
                view.offsetLeftAndRight(getAllowedLeftDelta(view, delta, 0));
            }
        }

        if (centerViews.size() >= 2) {
            final View secondTopCard = centerViews.get(1);
            final int secondTopCardLeft = getDecoratedLeft(secondTopCard);

            if (secondTopCardLeft <= params.activeCardCenter) {
                final View firstTopCard = centerViews.get(0);
                firstTopCard.offsetLeftAndRight(getAllowedLeftDelta(firstTopCard, delta, 0));
            }
            secondTopCard.offsetLeftAndRight(getAllowedLeftDelta(secondTopCard, delta, 0));
        } else if (!leftViews.isEmpty() && !centerViews.isEmpty()) {
            for (int i = 0, cnt = leftViews.size(); i < cnt; i++) {
                int leftBorder = 0;
                if (i == cnt - 1) {
                    leftBorder = params.activeCardLeft / 2;
                }
                final View view = getChildAt(i);
                view.offsetLeftAndRight(getAllowedLeftDelta(view, delta, leftBorder));
            }

            for (View view : centerViews) {
                view.offsetLeftAndRight(getAllowedLeftDelta(view, delta, params.activeCardLeft));
            }
        }

        return delta;
    }

    private int getAllowedLeftDelta(@NonNull View view, int dx, int border) {
        final int viewLeft = getDecoratedLeft(view);
        if (viewLeft - dx > border) {
            return -dx;
        } else {
            return border - viewLeft;
        }
    }

    private int getAllowedRightDelta(@NonNull View view, int dx) {
        final int pos = getPosition(view);
        final int border = params.activeCardLeft + pos * params.cardWidth;
        final int vewLeft = getDecoratedLeft(view);

        if (vewLeft + Math.abs(dx) < border) {
            return dx;
        } else {
            return vewLeft - border;
        }
    }

    @Nullable
    private View getTopView() {
        if (getChildCount() == 0) {
            return null;
        }

        View result = null;
        float lastScaleX = 0f;

        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            final View view = getChildAt(i);
            if (getDecoratedLeft(view) >= params.activeCardRight) {
                continue;
            }

            final float scaleX = ViewCompat.getScaleX(view);
            if (lastScaleX < scaleX) {
                lastScaleX = scaleX;
                result = view;
            }
        }

        return result;
    }

    private int getAnchorPos() {
        int result = 0;
        if (requestedPosition != INVALID_VALUE) {
            result = requestedPosition;
        } else {
            final View topView = getTopView();
            if (topView != null) {
                result = getPosition(topView);
            }
        }

        return result;
    }

    private void fill(RecyclerView.Recycler recycler) {
        final int anchorPos = getAnchorPos();

        viewCache.clear();

        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }

        for (int i = 0, cnt = viewCache.size(); i < cnt; i++) {
            detachView(viewCache.valueAt(i));
        }

        fillLeft(anchorPos, recycler);
        fillRight(anchorPos, recycler);

        for (int i = 0, cnt = viewCache.size(); i < cnt; i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }

        updateViewScale();
    }

    private void fillLeft(int anchorPos, RecyclerView.Recycler recycler) {
        if (anchorPos == INVALID_VALUE) {
            return;
        }

        final int layoutStep = params.activeCardLeft / SliderParams.LEFT_CARD_COUNT;
        int pos = Math.max(0, anchorPos - SliderParams.LEFT_CARD_COUNT);
        int viewLeft = Math.max(0, SliderParams.LEFT_CARD_COUNT - (anchorPos - pos)) * layoutStep;

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

    private void fillRight(int anchorPos, RecyclerView.Recycler recycler) {
        if (anchorPos == INVALID_VALUE) {
            return;
        }

        final int width = getWidth();
        final int itemCount = getItemCount();

        int pos = anchorPos;
        int viewLeft = params.activeCardLeft;
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
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            final View view = getChildAt(i);
            final int viewLeft = getDecoratedLeft(view);

            if (viewLeft < params.activeCardLeft) {
                final float ratio = (float) viewLeft / params.activeCardLeft;
                final float scale = SliderParams.SCALE_LEFT + SliderParams.SCALE_CENTER_TO_LEFT * ratio;
                ViewCompat.setScaleX(view, scale);
                ViewCompat.setScaleY(view, scale);
                ViewCompat.setAlpha(view, 0.1f + ratio);
                if (viewLeft < params.activeCardLeft / 2) {
                    ViewCompat.setZ(view, 4);
                } else {
                    ViewCompat.setZ(view, 6);
                }
            } else if (viewLeft < params.activeCardCenter) {
                ViewCompat.setScaleX(view, SliderParams.SCALE_CENTER);
                ViewCompat.setScaleY(view, SliderParams.SCALE_CENTER);
                ViewCompat.setZ(view, 12); // TODO: move to contants
                ViewCompat.setAlpha(view, 1);
            } else if (viewLeft < params.activeCardRight) {
                final float ratio = (float) (viewLeft - params.activeCardCenter) / (params.activeCardRight - params.activeCardCenter);
                final float scale = SliderParams.SCALE_CENTER - SliderParams.SCALE_CENTER_TO_RIGHT * ratio;
                ViewCompat.setScaleX(view, scale);
                ViewCompat.setScaleY(view, scale);
                ViewCompat.setZ(view, 16); // TODO: move to contants
                ViewCompat.setAlpha(view, 1);
            } else if (viewLeft >= params.activeCardRight) {
                ViewCompat.setScaleX(view, SliderParams.SCALE_RIGHT);
                ViewCompat.setScaleY(view, SliderParams.SCALE_RIGHT);
                ViewCompat.setZ(view, 8); // TODO: move to contants
                ViewCompat.setAlpha(view, 1);
            }
        }
    }

}

class SliderParams{

    private static final float VIEW_WIDTH_PERCENT = 0.35f;
    private static final float LEFT_BORDER_PERCENT = 0.1f;

    static final float SCALE_LEFT = 0.7f;
    static final float SCALE_CENTER = 0.95f;
    static final float SCALE_RIGHT = 0.8f;

    static final float SCALE_CENTER_TO_LEFT = SCALE_CENTER - SCALE_LEFT;
    static final float SCALE_CENTER_TO_RIGHT = SCALE_CENTER - SCALE_RIGHT;

    static final int LEFT_CARD_COUNT = 2;

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