package com.ramotion.cardslider;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.security.InvalidParameterException;

/**
 * Extended {@link LinearSnapHelper} that works <b>only</b> with {@link CardSliderLayoutManager}.
 */
public class CardSnapHelper extends LinearSnapHelper {

    private RecyclerView recyclerView;

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

        this.recyclerView = recyclerView;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        final CardSliderLayoutManager lm = (CardSliderLayoutManager) layoutManager;

        final int itemCount = lm.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;

        final PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
        if (vectorForEnd == null) {
            return RecyclerView.NO_POSITION;
        }

        final int distance = calculateScrollDistance(velocityX, velocityY)[0];
        int deltaJump;

        if (distance > 0) {
            deltaJump = (int) Math.floor(distance / lm.getCardWidth());
        } else {
            deltaJump = (int) Math.ceil(distance / lm.getCardWidth());
        }

        final int deltaSign = Integer.signum(deltaJump);
        deltaJump = deltaSign * Math.min(3, Math.abs(deltaJump));

        if (vectorForEnd.x < 0) {
            deltaJump = -deltaJump;
        }

        if (deltaJump == 0) {
            return RecyclerView.NO_POSITION;
        }

        final int currentPosition = lm.getActiveCardPosition();
        int targetPos = currentPosition + deltaJump;

        if (targetPos < 0) {
            targetPos = 0;
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1;
        }

        return targetPos;
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
        final int viewLeft = lm.getDecoratedLeft(targetView);
        final int activeCardLeft = lm.getActiveCardLeft();
        final int activeCardCenter = lm.getActiveCardLeft() + lm.getCardWidth() / 2;
        final int activeCardRight = lm.getActiveCardLeft() + lm.getCardWidth();

        int[] out = new int[] {0, 0};
        if (viewLeft < activeCardCenter) {
            final int targetPos = lm.getPosition(targetView);
            final int activeCardPos = lm.getActiveCardPosition();
            if (targetPos != activeCardPos) {
                out[0] = -(activeCardPos - targetPos) * lm.getCardWidth();
            } else {
                out[0] = viewLeft - activeCardLeft;
            }
        } else {
            out[0] = viewLeft - activeCardRight;
        }

        if (out[0] != 0) {
            recyclerView.smoothScrollBy(out[0], 0, new AccelerateInterpolator());
        }

        return new int[] {0, 0};
    }

    @Nullable
    @Override
    protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager layoutManager) {
        return ((CardSliderLayoutManager)layoutManager).getSmoothScroller(recyclerView);
    }

}
