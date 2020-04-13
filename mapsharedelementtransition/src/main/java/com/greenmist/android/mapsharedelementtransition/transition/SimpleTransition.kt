package com.greenmist.android.mapsharedelementtransition.transition

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.transition.R
import androidx.transition.TransitionValues
import androidx.transition.Visibility

class SimpleTransition : Visibility {

    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onDisappear(
        sceneRoot: ViewGroup?,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        return createAnimation(view)
    }

    override fun onAppear(
        sceneRoot: ViewGroup?,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        return createAnimation(view)
    }

    private fun setTransitionAlpha(view: View, alpha: Float) {
        val savedAlpha = view.getTag(R.id.save_non_transition_alpha) as? Float
        if (savedAlpha != null) {
            view.alpha = savedAlpha * alpha
        } else {
            view.alpha = alpha
        }
    }

    private fun clearNonTransitionAlpha(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.setTag(R.id.save_non_transition_alpha, null)
        }
    }

    private fun createAnimation(
        view: View?
    ): Animator? {
        setTransitionAlpha(view!!, 1f)
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    clearNonTransitionAlpha(view)
                }
            })
        }
    }
}