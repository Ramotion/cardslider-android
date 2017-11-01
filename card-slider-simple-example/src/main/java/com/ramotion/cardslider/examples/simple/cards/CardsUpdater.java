package com.ramotion.cardslider.examples.simple.cards;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.view.View;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.DefaultViewUpdater;

public class CardsUpdater extends DefaultViewUpdater {

    @Override
    public void updateView(@NonNull View view, float position) {
        super.updateView(view, position);

        final CardView card = ((CardView)view);
        final View alphaView = card.getChildAt(1);
        final View imageView = card.getChildAt(0);

        if (position < 0) {
            final float alpha = ViewCompat.getAlpha(view);
            ViewCompat.setAlpha(view, 1f);
            ViewCompat.setAlpha(alphaView, 0.9f - alpha);
            ViewCompat.setAlpha(imageView, 0.3f + alpha);
        } else {
            ViewCompat.setAlpha(alphaView, 0f);
            ViewCompat.setAlpha(imageView, 1f);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            final CardSliderLayoutManager lm =  getLayoutManager();
            final float ratio = (float) lm.getDecoratedLeft(view) / lm.getActiveCardLeft();

            final float z;

            if (position < 0) {
                z = Z_CENTER_1 * ratio;
            } else if (position < 0.5f) {
                z = Z_CENTER_1;
            } else if (position < 1f) {
                z = Z_CENTER_2;
            } else {
                z = Z_RIGHT;
            }

            card.setCardElevation(Math.max(0, z));
        }
    }

}
