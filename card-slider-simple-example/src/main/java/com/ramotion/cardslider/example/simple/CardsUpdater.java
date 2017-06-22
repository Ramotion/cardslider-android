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

    public CardsUpdater(CardSliderLayoutManager lm) {
        super(lm);
    }

    @Override
    public void onLayoutManagerInitialized() {
        super.onLayoutManagerInitialized();
    }

    @Override
    protected void onUpdateViewAlpha(@NonNull View view, float alpha) {
        final CardView card = ((CardView)view);
        final View alphaView = card.getChildAt(1);
        final View imageView = card.getChildAt(0);

        final boolean isLeftCard = alpha < 1;
        if (isLeftCard) {
            ViewCompat.setAlpha(alphaView, 0.9f - alpha);
            ViewCompat.setAlpha(imageView, 0.3f + alpha);
        } else {
            if (ViewCompat.getAlpha(alphaView) != 0) {
                ViewCompat.setAlpha(alphaView, 0f);
            }

            if (ViewCompat.getAlpha(imageView) != 1) {
                ViewCompat.setAlpha(imageView, 1f);
            }
        }
    }

}
