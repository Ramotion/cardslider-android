package com.ramotion.cardslider

import android.view.View
import androidx.core.view.ViewCompat
import com.ramotion.cardslider.CardSliderLayoutManager.ViewUpdater

/**
 * Default implementation of [CardSliderLayoutManager.ViewUpdater]
 */
open class DefaultViewUpdater : ViewUpdater {
    private var cardWidth = 0
    private var activeCardLeft = 0
    private var activeCardRight = 0
    private var activeCardCenter = 0
    private var cardsGap = 0f
    private var transitionEnd = 0
    private var transitionDistance = 0
    private var transitionRight2Center = 0f
    protected var layoutManager: CardSliderLayoutManager? = null
        private set
    private var previewView: View? = null
    override fun onLayoutManagerInitialized(lm: CardSliderLayoutManager) {
        layoutManager = lm
        cardWidth = lm.cardWidth
        activeCardLeft = lm.activeCardLeft
        activeCardRight = lm.activeCardRight
        activeCardCenter = lm.activeCardCenter
        cardsGap = lm.cardsGap
        transitionEnd = activeCardCenter
        transitionDistance = activeCardRight - transitionEnd
        val centerBorder = (cardWidth - cardWidth * SCALE_CENTER) / 2f
        val rightBorder = (cardWidth - cardWidth * SCALE_RIGHT) / 2f
        val right2centerDistance = activeCardRight + centerBorder - (activeCardRight - rightBorder)
        transitionRight2Center = right2centerDistance - cardsGap
    }

    override fun updateView(view: View, position: Float) {
        val scale: Float
        val alpha: Float
        val z: Float
        val x: Float
        if (position < 0) {
            val ratio = (layoutManager?.getDecoratedLeft(view)?.toFloat() ?: 0f) / activeCardLeft
            scale = SCALE_LEFT + SCALE_CENTER_TO_LEFT * ratio
            alpha = 0.1f + ratio
            z = Z_CENTER_1 * ratio
            x = 0f
        } else if (position < 0.5f) {
            scale = SCALE_CENTER
            alpha = 1f
            z = Z_CENTER_1.toFloat()
            x = 0f
        } else if (position < 1f) {
            val viewLeft = layoutManager?.getDecoratedLeft(view) ?: 0
            val ratio = (viewLeft - activeCardCenter).toFloat() / (activeCardRight - activeCardCenter)
            scale = SCALE_CENTER - SCALE_CENTER_TO_RIGHT * ratio
            alpha = 1f
            z = Z_CENTER_2.toFloat()
            x = if (Math.abs(transitionRight2Center) < Math.abs(transitionRight2Center * (viewLeft - transitionEnd) / transitionDistance)) {
                -transitionRight2Center
            } else {
                -transitionRight2Center * (viewLeft - transitionEnd) / transitionDistance
            }
        } else {
            scale = SCALE_RIGHT
            alpha = 1f
            z = Z_RIGHT.toFloat()
            if (previewView != null && layoutManager != null) {
                val prevViewScale: Float
                val prevTransition: Float
                val prevRight: Int
                val isFirstRight = layoutManager!!.getDecoratedRight(previewView!!) <= activeCardRight
                if (isFirstRight) {
                    prevViewScale = SCALE_CENTER
                    prevRight = activeCardRight
                    prevTransition = 0f
                } else {
                    prevViewScale = previewView?.scaleX ?: 0f
                    prevRight = layoutManager!!.getDecoratedRight(previewView!!)
                    prevTransition = previewView?.translationX ?: 0f
                }
                val prevBorder = (cardWidth - cardWidth * prevViewScale) / 2
                val currentBorder = (cardWidth - cardWidth * SCALE_RIGHT) / 2
                val distance = layoutManager!!.getDecoratedLeft(view) + currentBorder - (prevRight - prevBorder + prevTransition)
                val transition = distance - cardsGap
                x = -transition
            } else {
                x = 0f
            }
        }
        view.scaleX = scale
        view.scaleY = scale
        ViewCompat.setZ(view, z)
        view.translationX = x
        view.alpha = alpha
        previewView = view
    }

    companion object {
        const val SCALE_LEFT = 0.65f
        const val SCALE_CENTER = 0.95f
        const val SCALE_RIGHT = 0.8f
        const val SCALE_CENTER_TO_LEFT = SCALE_CENTER - SCALE_LEFT
        const val SCALE_CENTER_TO_RIGHT = SCALE_CENTER - SCALE_RIGHT
        const val Z_CENTER_1 = 12
        const val Z_CENTER_2 = 16
        const val Z_RIGHT = 8
    }
}
