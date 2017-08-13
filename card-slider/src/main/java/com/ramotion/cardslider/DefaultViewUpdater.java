package com.ramotion.cardslider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Default implementation of {@link ViewUpdater}
 */
public class DefaultViewUpdater extends ViewUpdater {

    private static final float SCALE_LEFT = 0.65f;
    private static final float SCALE_CENTER = 0.95f;
    private static final float SCALE_RIGHT = 0.8f;
    private static final float SCALE_CENTER_TO_LEFT = SCALE_CENTER - SCALE_LEFT;
    private static final float SCALE_CENTER_TO_RIGHT = SCALE_CENTER - SCALE_RIGHT;

    private static final int Z_CENTER_1 = 12;
    private static final int Z_CENTER_2 = 16;
    private static final int Z_RIGHT = 8;

    private int cardWidth;
    private int activeCardLeft;
    private int activeCardRight;
    private int activeCardCenter;
    private float cardsGap;

    private int transitionEnd;
    private int transitionDistance;
    private float transitionRight2Center;

    public DefaultViewUpdater(CardSliderLayoutManager lm) {
        super(lm);
    }

    @Override
    public void onLayoutManagerInitialized() {
        this.cardWidth = lm.getCardWidth();
        this.activeCardLeft = lm.getActiveCardLeft();
        this.activeCardRight = lm.getActiveCardRight();
        this.activeCardCenter = lm.getActiveCardCenter();
        this.cardsGap = lm.getCardsGap();

        this.transitionEnd = activeCardCenter;
        this.transitionDistance = activeCardRight - transitionEnd;

        final float centerBorder = (cardWidth - cardWidth * SCALE_CENTER) / 2f;
        final float rightBorder = (cardWidth - cardWidth * SCALE_RIGHT) / 2f;
        final float right2centerDistance = (activeCardRight + centerBorder) - (activeCardRight - rightBorder);
        this.transitionRight2Center = right2centerDistance - cardsGap;
    }

    @Override
    public int getActiveCardPosition() {
        int result = RecyclerView.NO_POSITION;

        View biggestView = null;
        float lastScaleX = 0f;

        for (int i = 0, cnt = lm.getChildCount(); i < cnt; i++) {
            final View child = lm.getChildAt(i);
            final int viewLeft = lm.getDecoratedLeft(child);
            if (viewLeft >= activeCardRight) {
                continue;
            }

            final float scaleX = ViewCompat.getScaleX(child);
            if (lastScaleX < scaleX && viewLeft < activeCardCenter) {
                lastScaleX = scaleX;
                biggestView = child;
            }
        }

        if (biggestView != null) {
            result = lm.getPosition(biggestView);
        }

        return result;
    }

    @Nullable
    @Override
    public View getTopView() {
        if (lm.getChildCount() == 0) {
            return null;
        }

        View result = null;
        float lastValue = cardWidth;

        for (int i = 0, cnt = lm.getChildCount(); i < cnt; i++) {
            final View child = lm.getChildAt(i);
            if (lm.getDecoratedLeft(child) >= activeCardRight) {
                continue;
            }

            final int viewLeft = lm.getDecoratedLeft(child);
            final int diff = activeCardRight - viewLeft;
            if (diff < lastValue) {
                lastValue = diff;
                result = child;
            }
        }

        return result;
    }

    @Override
    public void updateView() {
        View prevView = null;

        for (int i = 0, cnt = lm.getChildCount(); i < cnt; i++) {
            final View view = lm.getChildAt(i);
            final int viewLeft = lm.getDecoratedLeft(view);

            final float scale;
            final float alpha;
            final float z;
            final float x;

            if (viewLeft < activeCardLeft) {
                final float ratio = (float) viewLeft / activeCardLeft;
                scale = SCALE_LEFT + SCALE_CENTER_TO_LEFT * ratio;
                alpha = 0.1f + ratio;
                z = Z_CENTER_1 * ratio;
                x = 0;
            } else if (viewLeft < activeCardCenter) {
                scale = SCALE_CENTER;
                alpha = 1;
                z = Z_CENTER_1;
                x = 0;
            } else if (viewLeft < activeCardRight) {
                final float ratio = (float) (viewLeft - activeCardCenter) / (activeCardRight - activeCardCenter);
                scale = SCALE_CENTER - SCALE_CENTER_TO_RIGHT * ratio;
                alpha = 1;
                z = Z_CENTER_2;
                if (Math.abs(transitionRight2Center) < Math.abs(transitionRight2Center * (viewLeft - transitionEnd) / transitionDistance)) {
                    x = -transitionRight2Center;
                } else {
                    x = -transitionRight2Center * (viewLeft - transitionEnd) / transitionDistance;
                }
            } else {
                scale = SCALE_RIGHT;
                alpha = 1;
                z = Z_RIGHT;

                if (prevView != null) {
                    final float prevViewScale;
                    final float prevTransition;
                    final int prevRight;

                    final boolean isFirstRight = lm.getDecoratedRight(prevView) <= activeCardRight;
                    if (isFirstRight) {
                        prevViewScale = SCALE_CENTER;
                        prevRight = activeCardRight;
                        prevTransition = 0;
                    } else {
                        prevViewScale = ViewCompat.getScaleX(prevView);
                        prevRight = lm.getDecoratedRight(prevView);
                        prevTransition = ViewCompat.getTranslationX(prevView);
                    }

                    final float prevBorder = (cardWidth - cardWidth * prevViewScale) / 2;
                    final float currentBorder = (cardWidth - cardWidth * SCALE_RIGHT) / 2;
                    final float distance = (viewLeft + currentBorder) - (prevRight - prevBorder + prevTransition);

                    final float transition = distance - cardsGap;
                    x = -transition;
                } else {
                    x = 0;
                }
            }

            onUpdateViewScale(view, scale);
            onUpdateViewTransitionX(view, x);
            onUpdateViewZ(view, z);
            onUpdateViewAlpha(view, alpha);

            prevView = view;
        }
    }

    protected void onUpdateViewAlpha(@NonNull View view, float alpha) {
        if (ViewCompat.getAlpha(view) != alpha) {
            ViewCompat.setAlpha(view, alpha);
        }
    }

    protected void onUpdateViewScale(@NonNull View view, float scale) {
        if (ViewCompat.getScaleX(view) != scale) {
            ViewCompat.setScaleX(view, scale);
            ViewCompat.setScaleY(view, scale);
        }
    }

    protected void onUpdateViewZ(@NonNull View view, float z) {
        if (ViewCompat.getZ(view) != z) {
            ViewCompat.setZ(view, z);
        }
    }

    protected void onUpdateViewTransitionX(@NonNull View view, float x) {
        if (ViewCompat.getTranslationX(view) != x) {
            ViewCompat.setTranslationX(view, x);
        }
    }

}
