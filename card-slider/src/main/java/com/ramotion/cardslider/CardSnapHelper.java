package com.ramotion.cardslider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.security.InvalidParameterException;

/**
 * Extended {@link LinearSnapHelper} that works <b>only</b> with {@link CardSliderLayoutManager}.
 */
public class CardSnapHelper extends LinearSnapHelper {

    /**
     * Attaches the {@link CardSnapHelper} to the provided RecyclerView, by calling
     * {@link RecyclerView#setOnFlingListener(RecyclerView.OnFlingListener)}.
     * You can call this method with {@code null} to detach it from the current RecyclerView.
     *
     * @param recyclerView The RecyclerView instance to which you want to add this helper or
     *                     {@code null} if you want to remove SnapHelper from the current
     *                     RecyclerView.
     *
     * @throws IllegalArgumentException if there is already a {@link RecyclerView.OnFlingListener}
     * attached to the provided {@link RecyclerView}.
     *
     * @throws InvalidParameterException if provided RecyclerView has LayoutManager which is not
     * instance of CardSliderLayoutManager
     *
     */
    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
        if (recyclerView != null && !(recyclerView.getLayoutManager() instanceof CardSliderLayoutManager)) {
            throw new InvalidParameterException("LayoutManager must be instance of CardSliderLayoutManager");
        }
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        return ((CardSliderLayoutManager)layoutManager).getTopView();
    }

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView)
    {
        final CardSliderLayoutManager lm = (CardSliderLayoutManager)layoutManager;
        final int[] borders = lm.getCardBorders();
        final int[] out = new int[] {0, 0};
        final int viewLeft = lm.getDecoratedLeft(targetView);

        if (viewLeft < borders[1]) {
            out[0] = viewLeft - borders[0];
        } else {
            out[0] = viewLeft - borders[2];
        }

        return out;
    }
}
