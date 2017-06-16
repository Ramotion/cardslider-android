package com.ramotion.cardslider;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Card transformer interface for {@link CardSliderLayoutManager}.
 */
public abstract class ViewUpdater {

    protected final CardSliderLayoutManager lm;

    public ViewUpdater(CardSliderLayoutManager lm) {
        this.lm = lm;
    }

    public void onLayoutManagerInitialized() {}

    abstract public int getActiveCardPosition();

    abstract @Nullable public View getTopView();

    abstract public void updateView();

}
