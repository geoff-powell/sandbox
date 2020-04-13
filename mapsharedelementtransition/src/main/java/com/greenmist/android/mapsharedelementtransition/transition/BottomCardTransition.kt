package com.greenmist.android.mapsharedelementtransition.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.transition.Transition
import androidx.transition.TransitionValues

class BottomCardTransition : Transition {

    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getTransitionProperties(): Array<String>? {
        return TRANSITION_PROPERTIES
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        transitionValues.values[PROPNAME_TRANSLATION_Y] = transitionValues.view.translationY
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        val anyTransitionValues = startValues ?: endValues ?: return null

        val halfHeight = 0.5f * anyTransitionValues.view.measuredHeight
        var start = halfHeight
        var end = 0f
        if (startValues != null && endValues == null) {
            start = 0f
            end = halfHeight
        }

        return ObjectAnimator.ofFloat(anyTransitionValues.view, View.TRANSLATION_Y, start, end)
    }

    companion object {
        private const val PROPNAME_TRANSLATION_Y = "TitleCollapsingTransition:translation_y"
        private val TRANSITION_PROPERTIES = arrayOf(PROPNAME_TRANSLATION_Y)
    }
}