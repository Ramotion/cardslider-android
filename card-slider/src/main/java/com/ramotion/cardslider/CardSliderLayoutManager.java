package com.ramotion.cardslider;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import java.util.LinkedList;

public class CardSliderLayoutManager extends RecyclerView.LayoutManager
        implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    private static final boolean DEBUG = true;

    private static final float SCALE_LEFT = 0.7f;
    private static final float SCALE_CENTER = 0.95f;
    private static final float SCALE_RIGHT = 0.8f;
    private static final float SCALE_CENTER_TO_LEFT = SCALE_CENTER - SCALE_LEFT;
    private static final float SCALE_CENTER_TO_RIGHT = SCALE_CENTER - SCALE_RIGHT;
    private static final int LEFT_CARD_COUNT = 2;

    private final SparseArray<View> viewCache = new SparseArray<>();

    private int cardWidth;
    private int activeCardLeft;
    private int activeCardRight;
    private int activeCardCenter;

    private int scrollRequestedPosition = 0;

    public CardSliderLayoutManager(int activeCardLeft, int cardWidth) {
        this.cardWidth = cardWidth;
        this.activeCardLeft = activeCardLeft;
        this.activeCardRight = activeCardLeft + cardWidth;
        this.activeCardCenter = activeCardLeft + ((this.activeCardRight - activeCardLeft) / 2);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        if (getChildCount() == 0 && state.isPreLayout()) {
            return;
        }

        int anchorPos = getActiveCardPosition();

        final LinkedList<Integer> removedPositions = new LinkedList<>();
        if (state.isPreLayout()) {
            for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
                final View child = getChildAt(i);
                final boolean isRemoved = ((RecyclerView.LayoutParams)child.getLayoutParams()).isItemRemoved();
                if (isRemoved) {
                    removedPositions.add(getPosition(child));
                }
            }

            if (removedPositions.contains(anchorPos)) {
                final int last = removedPositions.getLast();
                final int first = removedPositions.getFirst();

                final int right = Math.min(last, getItemCount() - 1);

                int left = right;
                if (last != first) {
                    left = Math.max(first, 0);
                }

                anchorPos = Math.max(left, right);
            }
        }

        detachAndScrapAttachedViews(recycler);
        fill(anchorPos, recycler, state);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
    }

    @Override
    public boolean canScrollHorizontally() {
        return getChildCount() != 0;
    }

    @Override
    public void scrollToPosition(int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }

        scrollRequestedPosition = position;
        requestLayout();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scrollRequestedPosition = RecyclerView.NO_POSITION;

        int delta;
        if (dx < 0) {
            delta = scrollRight(dx);
        } else {
            delta = scrollLeft(dx);
        }

        fill(getActiveCardPosition(), recycler, state);
        return delta;
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return new PointF(targetPosition - getActiveCardPosition(), 0);
    }

    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, RecyclerView.State state, final int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }

        final LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            public int calculateDxToMakeVisible(View view, int snapPreference) {
                final int viewStart = getDecoratedLeft(view);
                if (viewStart > activeCardLeft) {
                    return activeCardLeft - viewStart;
                } else {
                    int delta = 0;
                    int topViewPos = 0;

                    final View topView = getTopView();
                    if (topView != null) {
                        topViewPos = getPosition(topView);
                        if (topViewPos != position) {
                            final int topViewLeft = getDecoratedLeft(topView);
                            if (topViewLeft >= activeCardLeft && topViewLeft < activeCardRight) {
                                delta = activeCardRight - topViewLeft;
                            }
                        }
                    }

                    return delta + (cardWidth) * Math.max(0, topViewPos - position - 1);
                }
            }

            @Override
            protected int calculateTimeForDeceleration(int dx) {
                return 500;
            }
        };

        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }

    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int count) {
        final int anchorPos = getActiveCardPosition();
        if (positionStart + count <= anchorPos) {
            scrollRequestedPosition = anchorPos - 1;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState state = new SavedState();
        state.anchorPos = getActiveCardPosition();
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState state = (SavedState) parcelable;
            scrollRequestedPosition = state.anchorPos;
            requestLayout();
        }
    }

    public int getActiveCardPosition() {
        int result = RecyclerView.NO_POSITION;

        if (scrollRequestedPosition != RecyclerView.NO_POSITION) {
            result = scrollRequestedPosition;
        } else {
            View biggestView = null;
            float lastScaleX = 0f;

            for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
                final View child = getChildAt(i);
                if (getDecoratedLeft(child) >= activeCardRight) {
                    continue;
                }

                final float scaleX = ViewCompat.getScaleX(child);
                if (lastScaleX < scaleX) {
                    lastScaleX = scaleX;
                    biggestView = child;
                }
            }

            if (biggestView != null) {
                result = getPosition(biggestView);
            }
        }

        return result;
    }

    @Nullable
    View getTopView() {
        if (getChildCount() == 0) {
            return null;
        }

        View result = null;
        float lastZ = 0f;

        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            final View child = getChildAt(i);
            if (getDecoratedLeft(child) >= activeCardRight) {
                continue;
            }

            final float z = ViewCompat.getZ(child);
            if (lastZ < z) {
                lastZ = z;
                result = child;
            }
        }

        return result;
    }

    int getActiveCardLeft() {
        return activeCardLeft;
    }

    int getCardWidth() {
        return cardWidth;
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

            if (viewLeft >= activeCardRight) {
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
            if (prevView == null || getDecoratedLeft(prevView) > activeCardRight) {
                final View view = leftViews.get(i);
                view.offsetLeftAndRight(-getAllowedRightDelta(view, dx));
                prevView = view;
            } else if (getDecoratedLeft(prevView) > activeCardCenter) {
                final int borderStep = activeCardLeft / LEFT_CARD_COUNT;
                int border = activeCardLeft;

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
            delta = Math.min(dx, getDecoratedRight(lastView) - activeCardRight);
        } else {
            delta = dx;
        }

        final LinkedList<View> leftViews = new LinkedList<>();
        final LinkedList<View> centerViews = new LinkedList<>();

        for (int i = 0; i < childCount; i++) {
            final View view = getChildAt(i);
            final int viewLeft = getDecoratedLeft(view);

            if (viewLeft < activeCardLeft) {
                leftViews.add(view);
            } else if (viewLeft < activeCardRight) {
                centerViews.add(view);
            } else {
                view.offsetLeftAndRight(getAllowedLeftDelta(view, delta, 0));
            }
        }

        if (centerViews.size() >= 2) {
            final View secondTopCard = centerViews.get(1);
            final int secondTopCardLeft = getDecoratedLeft(secondTopCard);

            if (secondTopCardLeft <= activeCardCenter) {
                final View firstTopCard = centerViews.get(0);
                firstTopCard.offsetLeftAndRight(getAllowedLeftDelta(firstTopCard, delta, 0));
            }
            secondTopCard.offsetLeftAndRight(getAllowedLeftDelta(secondTopCard, delta, 0));
        } else if (!leftViews.isEmpty() && !centerViews.isEmpty()) {
            for (int i = 0, cnt = leftViews.size(); i < cnt; i++) {
                int leftBorder = 0;
                if (i == cnt - 1) {
                    leftBorder = activeCardLeft / 2;
                }
                final View view = getChildAt(i);
                view.offsetLeftAndRight(getAllowedLeftDelta(view, delta, leftBorder));
            }

            for (View view : centerViews) {
                view.offsetLeftAndRight(getAllowedLeftDelta(view, delta, activeCardLeft));
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
        final int border = activeCardLeft + pos * cardWidth;
        final int vewLeft = getDecoratedLeft(view);

        if (vewLeft + Math.abs(dx) < border) {
            return dx;
        } else {
            return vewLeft - border;
        }
    }

    private void fill(int anchorPos, RecyclerView.Recycler recycler, RecyclerView.State state) {
        viewCache.clear();

        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }

        for (int i = 0, cnt = viewCache.size(); i < cnt; i++) {
            detachView(viewCache.valueAt(i));
        }

        if (!state.isPreLayout()) {
            fillLeft(anchorPos, recycler);
            fillRight(anchorPos, recycler);
        }

        for (int i = 0, cnt = viewCache.size(); i < cnt; i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }

        updateViewScale();
    }

    private void fillLeft(int anchorPos, RecyclerView.Recycler recycler) {
        if (anchorPos == RecyclerView.NO_POSITION) {
            return;
        }

        final int layoutStep = activeCardLeft / LEFT_CARD_COUNT;
        int pos = Math.max(0, anchorPos - LEFT_CARD_COUNT);
        int viewLeft = Math.max(0, LEFT_CARD_COUNT - (anchorPos - pos)) * layoutStep;

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
                layoutDecorated(view, viewLeft, 0, viewLeft + cardWidth, viewHeight);
            }

            viewLeft += layoutStep;
            pos++;
        }

    }

    private void fillRight(int anchorPos, RecyclerView.Recycler recycler) {
        if (anchorPos == RecyclerView.NO_POSITION) {
            return;
        }

        final int width = getWidth();
        final int itemCount = getItemCount();

        int pos = anchorPos;
        int viewLeft = activeCardLeft;
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
                layoutDecorated(view, viewLeft, 0, viewLeft + cardWidth, viewHeight);
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

            if (viewLeft < activeCardLeft) {
                final float ratio = (float) viewLeft / activeCardLeft;
                final float scale = SCALE_LEFT + SCALE_CENTER_TO_LEFT * ratio;
                ViewCompat.setScaleX(view, scale);
                ViewCompat.setScaleY(view, scale);
                ViewCompat.setAlpha(view, 0.1f + ratio);
                if (viewLeft < activeCardLeft / 2) {
                    ViewCompat.setZ(view, 4);
                } else {
                    ViewCompat.setZ(view, 6);
                }
            } else if (viewLeft < activeCardCenter) {
                ViewCompat.setScaleX(view, SCALE_CENTER);
                ViewCompat.setScaleY(view, SCALE_CENTER);
                ViewCompat.setZ(view, 12); // TODO: move to contants
                ViewCompat.setAlpha(view, 1);
            } else if (viewLeft < activeCardRight) {
                final float ratio = (float) (viewLeft - activeCardCenter) / (activeCardRight - activeCardCenter);
                final float scale = SCALE_CENTER - SCALE_CENTER_TO_RIGHT * ratio;
                ViewCompat.setScaleX(view, scale);
                ViewCompat.setScaleY(view, scale);
                ViewCompat.setZ(view, 16); // TODO: move to contants
                ViewCompat.setAlpha(view, 1);
            } else if (viewLeft >= activeCardRight) {
                ViewCompat.setScaleX(view, SCALE_RIGHT);
                ViewCompat.setScaleY(view, SCALE_RIGHT);
                ViewCompat.setZ(view, 8); // TODO: move to contants
                ViewCompat.setAlpha(view, 1);
            }
        }
    }

    private static class SavedState implements Parcelable {

        int anchorPos;

        SavedState() {

        }

        SavedState(Parcel in) {
            anchorPos = in.readInt();
        }

        public SavedState(SavedState other) {
            anchorPos = other.anchorPos;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(anchorPos);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}