package com.ramotion.cardslider.example.simple;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.DefaultViewUpdater;

public class CardsUpdater extends DefaultViewUpdater {

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

    public CardsUpdater(CardSliderLayoutManager lm) {
        super(lm);

        Log.d("D", "CardsUpdater initialized");
    }

    @Override
    public void onLayoutManagerInitialized() {
        super.onLayoutManagerInitialized();

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
                alpha = 0.8f - ratio;
                z = Z_CENTER_1 * ratio;
                x = 0;
            } else if (viewLeft < activeCardCenter) {
                scale = SCALE_CENTER;
                alpha = 0;
                z = Z_CENTER_1;
                x = 0;
            } else if (viewLeft < activeCardRight) {
                final float ratio = (float) (viewLeft - activeCardCenter) / (activeCardRight - activeCardCenter);
                scale = SCALE_CENTER - SCALE_CENTER_TO_RIGHT * ratio;
                alpha = 0;
                z = Z_CENTER_2;
                x = -Math.min(transitionRight2Center, transitionRight2Center * (viewLeft - transitionEnd) / transitionDistance);
            } else {
                scale = SCALE_RIGHT;
                alpha = 0;
                z = Z_RIGHT;

                if (prevView != null) {
                    final int prevRight = lm.getDecoratedRight(prevView);
                    final float prevBorder = (cardWidth - cardWidth * ViewCompat.getScaleX(prevView)) / 2;
                    final float prevTransition = ViewCompat.getTranslationX(prevView);
                    final float currentBorder = (cardWidth - cardWidth * ViewCompat.getScaleX(view)) / 2;
                    final float distance = (viewLeft + currentBorder) - (prevRight - prevBorder + prevTransition);

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

            final CardView card = ((CardView)view);
            final View alphaView = card.getChildAt(card.getChildCount() - 1);
            ViewCompat.setAlpha(alphaView, alpha);

            prevView = view;
        }
    }
}
