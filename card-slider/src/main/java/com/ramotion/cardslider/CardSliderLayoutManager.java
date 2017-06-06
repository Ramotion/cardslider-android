package com.ramotion.cardslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;

import java.util.LinkedList;

/**
 * A {@link android.support.v7.widget.RecyclerView.LayoutManager} implementation.
 */
public class CardSliderLayoutManager extends RecyclerView.LayoutManager
        implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    private static final int DEFAULT_ACTIVE_CARD_LEFT_OFFSET = 50;
    private static final int DEFAULT_CARD_WIDTH = 148;
    private static final int DEFAULT_CARDS_GAP = 12;

    private static final float SCALE_LEFT = 0.65f;
    private static final float SCALE_CENTER = 0.95f;
    private static final float SCALE_RIGHT = 0.8f;
    private static final float SCALE_CENTER_TO_LEFT = SCALE_CENTER - SCALE_LEFT;
    private static final float SCALE_CENTER_TO_RIGHT = SCALE_CENTER - SCALE_RIGHT;
    private static final int LEFT_CARD_COUNT = 2;

    private static final int Z_LEFT_1 = 4;
    private static final int Z_LEFT_2 = 6;
    private static final int Z_CENTER_1 = 12;
    private static final int Z_CENTER_2 = 16;
    private static final int Z_RIGHT = 8;

    private final SparseArray<View> viewCache = new SparseArray<>();
    private final SparseIntArray cardsXCoords = new SparseIntArray();

    private int cardWidth;
    private int activeCardLeft;
    private int activeCardRight;
    private int activeCardCenter;

    private float cardsGap;
    private int transitionEnd;
    private int transitionDistance;
    private float transitionRight2Center;

    private int scrollRequestedPosition = 0;

    /**
     * Creates CardSliderLayoutManager with default values
     *
     * @param context   Current context, will be used to access resources.
     */
    public CardSliderLayoutManager(@NonNull Context context) {
        this(context, null, 0, 0);
    }

    /**
     * Constructor used when layout manager is set in XML by RecyclerView attribute
     * "layoutManager".
     *
     * @attr ref R.styleable#CardSlider_activeCardLeftOffset
     * @attr ref R.styleable#CardSlider_cardWidth
     * @attr ref R.styleable#CardSlider_cardsGap
     */
    public CardSliderLayoutManager(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final float density = context.getResources().getDisplayMetrics().density;

        final int defaultCardWidth = (int) (DEFAULT_CARD_WIDTH * density);
        final int defaultActiveCardLeft = (int) (DEFAULT_ACTIVE_CARD_LEFT_OFFSET * density);
        final float defaultCardsGap = DEFAULT_CARDS_GAP * density;

        if (attrs == null) {
            initialize(defaultActiveCardLeft, defaultCardWidth, defaultCardsGap);
        } else {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardSlider, 0, 0);
            try {
                initialize(
                    a.getDimensionPixelSize(R.styleable.CardSlider_cardWidth, defaultCardWidth),
                    a.getDimensionPixelSize(R.styleable.CardSlider_activeCardLeftOffset, defaultActiveCardLeft),
                    a.getDimension(R.styleable.CardSlider_cardsGap, defaultCardsGap)
                );
            } finally {
                a.recycle();
            }
        }
    }

    /**
     * Creates CardSliderLayoutManager with specified values in pixels.
     *
     * @param activeCardLeft    Active card offset from start of RecyclerView. Default value is 50dp.
     * @param cardWidth         Card width. Default value is 148dp.
     * @param cardsGap          Distance between cards. Default value is 12dp.
     */
    public CardSliderLayoutManager(int activeCardLeft, int cardWidth, float cardsGap) {
        initialize(activeCardLeft, cardWidth, cardsGap);
    }

    private void initialize(int activeCardLeft, int cardWidth, float cardsGap) {
        this.cardWidth = cardWidth;
        this.activeCardLeft = activeCardLeft;
        this.activeCardRight = activeCardLeft + cardWidth;
        this.activeCardCenter = activeCardLeft + ((this.activeCardRight - activeCardLeft) / 2);

        this.transitionEnd = activeCardCenter;
        this.transitionDistance = activeCardRight - transitionEnd;
        this.cardsGap = cardsGap;

        final float centerBorder = (cardWidth - cardWidth * SCALE_CENTER) / 2f;
        final float rightBorder = (cardWidth - cardWidth * SCALE_RIGHT) / 2f;
        final float right2centerDistance = (activeCardRight + centerBorder) - (activeCardRight - rightBorder);
        this.transitionRight2Center = right2centerDistance - cardsGap;
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

        if (state.isPreLayout()) {
            final LinkedList<Integer> removedPositions = new LinkedList<>();
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
        if (cardsXCoords.size() != 0) {
            layoutByCoords();
        }
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
            delta = scrollRight(Math.max(dx, -cardWidth));
        } else {
            delta = scrollLeft(dx);
        }

        fill(getActiveCardPosition(), recycler, state);

        cardsXCoords.clear();
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            final View view = getChildAt(i);
            cardsXCoords.put(getPosition(view), getDecoratedLeft(view));
        }

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

        final LinearSmoothScroller scroller = getSmoothScroller(recyclerView);
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

    /**
     * @return active card position or RecyclerView.NO_POSITION
     */
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
        float lastValue = cardWidth;

        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            final View child = getChildAt(i);
            if (getDecoratedLeft(child) >= activeCardRight) {
                continue;
            }

            final int viewLeft = getDecoratedLeft(child);
            final int diff = activeCardRight - viewLeft;
            if (diff < lastValue) {
                lastValue = diff;
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

    LinearSmoothScroller getSmoothScroller(final RecyclerView recyclerView) {
        return new LinearSmoothScroller(recyclerView.getContext()) {
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
                        if (topViewPos != getTargetPosition()) {
                            final int topViewLeft = getDecoratedLeft(topView);
                            if (topViewLeft >= activeCardLeft && topViewLeft < activeCardRight) {
                                delta = activeCardRight - topViewLeft;
                            }
                        }
                    }

                    return delta + (cardWidth) * Math.max(0, topViewPos - getTargetPosition() - 1);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 0.5f;
            }

        };
    }

    private int scrollRight(int dx) {
        final int childCount = getChildCount();

        if (childCount == 0) {
            return 0;
        }

        final View rightestView = getChildAt(childCount - 1);
        final int deltaBorder = activeCardLeft + getPosition(rightestView) * cardWidth;
        final int delta = getAllowedRightDelta(rightestView, dx, deltaBorder);

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
            final int border = activeCardLeft + getPosition(view) * cardWidth;
            final int allowedDelta = getAllowedRightDelta(view, delta, border);
            view.offsetLeftAndRight(-allowedDelta);
        }

        final int step = activeCardLeft / LEFT_CARD_COUNT;
        final int jDelta = (int) Math.floor(1f * delta * step / cardWidth);

        View prevView = null;
        int j = 0;

        for (int i = 0, cnt = leftViews.size(); i < cnt; i++) {
            final View view = leftViews.get(i);
            if (prevView == null || getDecoratedLeft(prevView) > activeCardRight) {
                final int border = activeCardLeft + getPosition(view) * cardWidth;
                final int allowedDelta = getAllowedRightDelta(view, delta, border);
                view.offsetLeftAndRight(-allowedDelta);
            } else {
                final int border = activeCardLeft - step * j;

                final int allowedDelta;
                if (border == activeCardLeft && i == cnt - 1) {
                    allowedDelta = getAllowedRightDelta(view, delta, border);
                } else {
                    allowedDelta = getAllowedRightDelta(view, jDelta, border);
                }

                view.offsetLeftAndRight(-allowedDelta);
                j++;
            }

            prevView = view;
        }

        return delta;
    }

    private int scrollLeft(int dx) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        }

        final View lastView = getChildAt(childCount - 1);
        final boolean isLastItem = getPosition(lastView) == getItemCount() - 1;

        final int delta;
        if (isLastItem) {
            delta = Math.min(dx, getDecoratedRight(lastView) - activeCardRight);
        } else {
            delta = dx;
        }

        final int step = activeCardLeft / LEFT_CARD_COUNT;
        final int jDelta = (int) Math.ceil(1f * delta * step / cardWidth);

        for (int i = childCount - 1; i >= 0; i--) {
            final View view = getChildAt(i);
            final int viewLeft = getDecoratedLeft(view);

            if (viewLeft > activeCardLeft) {
                view.offsetLeftAndRight(getAllowedLeftDelta(view, delta, activeCardLeft));
            } else {
                int border = activeCardLeft - step;
                for (int j = i; j >= 0; j--) {
                    final View jView = getChildAt(j);
                    jView.offsetLeftAndRight(getAllowedLeftDelta(jView, jDelta, border));
                    border -= step;
                }

                break;
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

    private int getAllowedRightDelta(@NonNull View view, int dx, int border) {
        final int viewLeft = getDecoratedLeft(view);
        if (viewLeft + Math.abs(dx) < border) {
            return dx;
        } else {
            return viewLeft - border;
        }
    }

    private void layoutByCoords() {
        final int count = Math.min(getChildCount(), cardsXCoords.size());
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i);
            final int viewLeft = cardsXCoords.get(getPosition(view));
            layoutDecorated(view, viewLeft, 0, viewLeft + cardWidth, getDecoratedBottom(view));
        }
        updateViewScale();
        cardsXCoords.clear();
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
            fillRight = viewLeft < width + cardWidth;
            pos++;
        }
    }

    private void updateViewScale() {
        View prevView = null;

        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            final View view = getChildAt(i);
            final int viewLeft = getDecoratedLeft(view);

            ViewCompat.setAlpha(view, 1);

            if (viewLeft < activeCardLeft) {
                final float ratio = (float) viewLeft / activeCardLeft;
                final float scale = SCALE_LEFT + SCALE_CENTER_TO_LEFT * ratio;
                ViewCompat.setScaleX(view, scale);
                ViewCompat.setScaleY(view, scale);
                ViewCompat.setAlpha(view, 0.1f + ratio);
                if (viewLeft < activeCardLeft / 2) {
                    ViewCompat.setZ(view, Z_LEFT_1);
                } else {
                    ViewCompat.setZ(view, Z_LEFT_2);
                }
                ViewCompat.setTranslationX(view, 0);
            } else if (viewLeft < activeCardCenter) {
                ViewCompat.setScaleX(view, SCALE_CENTER);
                ViewCompat.setScaleY(view, SCALE_CENTER);
                ViewCompat.setZ(view, Z_CENTER_1);
                ViewCompat.setTranslationX(view, 0);
            } else if (viewLeft < activeCardRight) {
                final float ratio = (float) (viewLeft - activeCardCenter) / (activeCardRight - activeCardCenter);
                final float scale = SCALE_CENTER - SCALE_CENTER_TO_RIGHT * ratio;
                ViewCompat.setScaleX(view, scale);
                ViewCompat.setScaleY(view, scale);

                ViewCompat.setZ(view, Z_CENTER_2);

                final float transition = Math.min(transitionRight2Center, transitionRight2Center * (viewLeft - transitionEnd) / transitionDistance);
                ViewCompat.setTranslationX(view, -transition);
            } else if (viewLeft >= activeCardRight) {
                ViewCompat.setScaleX(view, SCALE_RIGHT);
                ViewCompat.setScaleY(view, SCALE_RIGHT);

                ViewCompat.setZ(view, Z_RIGHT);

                if (prevView != null) {
                    final int prevRight = getDecoratedRight(prevView);
                    final float prevBorder = (cardWidth - cardWidth * ViewCompat.getScaleX(prevView)) / 2;
                    final float prevTransition = ViewCompat.getTranslationX(prevView);
                    final float currentBorder = (cardWidth - cardWidth * ViewCompat.getScaleX(view)) / 2;
                    final float distance = (viewLeft + currentBorder) - (prevRight - prevBorder + prevTransition);

                    final float transition = distance - cardsGap;
                    ViewCompat.setTranslationX(view, -transition);
                }
            }

            prevView = view;
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