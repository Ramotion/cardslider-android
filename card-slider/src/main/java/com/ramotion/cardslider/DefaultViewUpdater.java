package com.ramotion.cardslider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Default implementation of {@link CardSliderLayoutManager.ViewUpdater}
 */
public class DefaultViewUpdater implements CardSliderLayoutManager.ViewUpdater {

    public static final float SCALE_LEFT = 0.65f;
    public static final float SCALE_CENTER = 0.95f;
    public static final float SCALE_RIGHT = 0.8f;
    public static final float SCALE_CENTER_TO_LEFT = SCALE_CENTER - SCALE_LEFT;
    public static final float SCALE_CENTER_TO_RIGHT = SCALE_CENTER - SCALE_RIGHT;

    public static final int Z_CENTER_1 = 12;
    public static final int Z_CENTER_2 = 16;
    public static final int Z_RIGHT = 8;

    private int cardWidth;
    private int activeCardLeft;
    private int activeCardRight;
    private int activeCardCenter;
    private float cardsGap;

    private int transitionEnd;
    private int transitionDistance;
    private float transitionRight2Center;

    private CardSliderLayoutManager lm;

    private View previewView;

    @Override
    public void onLayoutManagerInitialized(@NonNull CardSliderLayoutManager lm) {
        this.lm = lm;

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
    public void updateView(@NonNull View view, float position) {
        final float scale;
        final float alpha;
        final float z;
        final float x;

        if (position < 0) {
            final float ratio = (float) lm.getDecoratedLeft(view) / activeCardLeft;
            scale = SCALE_LEFT + SCALE_CENTER_TO_LEFT * ratio;
            alpha = 0.1f + ratio;
            z = Z_CENTER_1 * ratio;
            x = 0;
        } else if (position < 0.5f) {
            scale = SCALE_CENTER;
            alpha = 1;
            z = Z_CENTER_1;
            x = 0;
        } else if (position < 1f) {
            final int viewLeft = lm.getDecoratedLeft(view);
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

            if (previewView != null) {
                final float prevViewScale;
                final float prevTransition;
                final int prevRight;

                final boolean isFirstRight = lm.getDecoratedRight(previewView) <= activeCardRight;
                if (isFirstRight) {
                    prevViewScale = SCALE_CENTER;
                    prevRight = activeCardRight;
                    prevTransition = 0;
                } else {
                    prevViewScale = ViewCompat.getScaleX(previewView);
                    prevRight = lm.getDecoratedRight(previewView);
                    prevTransition = ViewCompat.getTranslationX(previewView);
                }

                final float prevBorder = (cardWidth - cardWidth * prevViewScale) / 2;
                final float currentBorder = (cardWidth - cardWidth * SCALE_RIGHT) / 2;
                final float distance = (lm.getDecoratedLeft(view) + currentBorder) - (prevRight - prevBorder + prevTransition);

                final float transition = distance - cardsGap;
                x = -transition;
            } else {
                x = 0;
            }
        }

        ViewCompat.setScaleX(view, scale);
        ViewCompat.setScaleY(view, scale);
        ViewCompat.setZ(view, z);
        ViewCompat.setTranslationX(view, x);
        ViewCompat.setAlpha(view, alpha);

        previewView = view;
    }

    protected CardSliderLayoutManager getLayoutManager() {
        return lm;
    }

}
