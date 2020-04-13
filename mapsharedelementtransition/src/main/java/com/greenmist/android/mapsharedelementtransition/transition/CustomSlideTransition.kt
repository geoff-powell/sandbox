package com.greenmist.android.mapsharedelementtransition.transition

import android.animation.*
import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.transition.*
import androidx.transition.SidePropagation
import androidx.transition.Slide.GravityFlag
import com.greenmist.android.mapsharedelementtransition.R
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class CustomSlideTransition : Visibility {

    private var mSlideCalculator = sCalculateBottom
    private var mSlideEdge = Gravity.BOTTOM
    var useViewAsBase = false

    private val getWidth: (View, ViewGroup) -> Int = { v, vg ->
        if (useViewAsBase) {
            v.width
        } else {
            vg.width
        }
    }

    private val getHeight: (View, ViewGroup) -> Int = { v, vg ->
        if (useViewAsBase) {
            v.height
        } else {
            vg.height
        }
    }

    private interface CalculateSlide {
        /** Returns the translation value for view when it goes out of the scene  */
        fun getGoneX(sceneRoot: ViewGroup, view: View, getWidth: (View, ViewGroup) -> Int): Float

        /** Returns the translation value for view when it goes out of the scene  */
        fun getGoneY(sceneRoot: ViewGroup, view: View, getHeight: (View, ViewGroup) -> Int): Float
    }

    private abstract class CalculateSlideHorizontal : CalculateSlide {

        override fun getGoneY(
            sceneRoot: ViewGroup,
            view: View,
            getHeight: (View, ViewGroup) -> Int
        ): Float {
            return view.translationY
        }
    }

    private abstract class CalculateSlideVertical : CalculateSlide {
        override fun getGoneX(
            sceneRoot: ViewGroup,
            view: View,
            getWidth: (View, ViewGroup) -> Int
        ): Float {
            return view.translationX
        }
    }

    /**
     * Constructor using the default [Gravity.BOTTOM]
     * slide edge direction.
     */
    constructor() {
        slideEdge = Gravity.BOTTOM
    }

    /**
     * Constructor using the provided slide edge direction.
     */
    constructor(slideEdge: Int) {
        this.slideEdge = slideEdge
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        val position = IntArray(2)
        view.getLocationOnScreen(position)
        transitionValues.values[PROPNAME_SCREEN_POSITION] = position
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)
        captureValues(transitionValues)
    }

    /**
     * Returns the edge that Views appear and disappear from.
     *
     * @return the edge of the scene to use for Views appearing and disappearing. One of
     * [android.view.Gravity.LEFT], [android.view.Gravity.TOP],
     * [android.view.Gravity.RIGHT], [android.view.Gravity.BOTTOM],
     * [android.view.Gravity.START], [android.view.Gravity.END].
     */
    /**
     * Change the edge that Views appear and disappear from.
     *
     * @param slideEdge The edge of the scene to use for Views appearing and disappearing. One of
     * [android.view.Gravity.LEFT], [android.view.Gravity.TOP],
     * [android.view.Gravity.RIGHT], [android.view.Gravity.BOTTOM],
     * [android.view.Gravity.START], [android.view.Gravity.END].
     */
    var slideEdge: Int
        get() = mSlideEdge
        @SuppressLint("RtlHardcoded")
        set(slideEdge) {
            mSlideCalculator = when (slideEdge) {
                Gravity.LEFT -> sCalculateLeft
                Gravity.TOP -> sCalculateTop
                Gravity.RIGHT -> sCalculateRight
                Gravity.BOTTOM -> sCalculateBottom
                Gravity.START -> sCalculateStart
                Gravity.END -> sCalculateEnd
                else -> throw IllegalArgumentException("Invalid slide direction")
            }
            mSlideEdge = slideEdge
            val propagation =
                SidePropagation()
            propagation.setSide(slideEdge)
            setPropagation(propagation)
        }

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (endValues == null) {
            return null
        }
        val position =
            endValues.values[PROPNAME_SCREEN_POSITION] as IntArray?
        val endX = view.translationX
        val endY = view.translationY
        val startX = mSlideCalculator.getGoneX(sceneRoot, view, getWidth)
        val startY = mSlideCalculator.getGoneY(sceneRoot, view, getHeight)
        return TranslationAnimationCreator.createAnimation(
            view, endValues, position!![0], position[1],
            startX, startY, endX, endY, sDecelerate, this
        )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null) {
            return null
        }
        val position =
            startValues.values[PROPNAME_SCREEN_POSITION] as IntArray?
        val startX = view.translationX
        val startY = view.translationY
        val endX = mSlideCalculator.getGoneX(sceneRoot, view, getWidth)
        val endY = mSlideCalculator.getGoneY(sceneRoot, view, getHeight)
        return TranslationAnimationCreator.createAnimation(
            view, startValues, position!![0], position[1],
            startX, startY, endX, endY, sAccelerate, this
        )
    }

    companion object {
        private val sDecelerate: TimeInterpolator = DecelerateInterpolator()
        private val sAccelerate: TimeInterpolator = AccelerateInterpolator()
        private const val PROPNAME_SCREEN_POSITION = "android:slide:screenPosition"
        private val sCalculateLeft: CalculateSlide = object : CalculateSlideHorizontal() {
            override fun getGoneX(
                sceneRoot: ViewGroup,
                view: View,
                getWidth: (View, ViewGroup) -> Int
            ): Float {
                return view.translationX - getWidth(view, sceneRoot)
            }
        }
        private val sCalculateStart: CalculateSlide = object : CalculateSlideHorizontal() {
            override fun getGoneX(
                sceneRoot: ViewGroup,
                view: View,
                getWidth: (View, ViewGroup) -> Int
            ): Float {
                val isRtl = (ViewCompat.getLayoutDirection(sceneRoot)
                        == ViewCompat.LAYOUT_DIRECTION_RTL)
                val x: Float
                x = if (isRtl) {
                    view.translationX + getWidth(view, sceneRoot)
                } else {
                    view.translationX - getWidth(view, sceneRoot)
                }
                return x
            }
        }
        private val sCalculateTop: CalculateSlide = object : CalculateSlideVertical() {
            override fun getGoneY(
                sceneRoot: ViewGroup,
                view: View,
                getHeight: (View, ViewGroup) -> Int
            ): Float {
                return view.translationY - getHeight(view, sceneRoot)
            }
        }
        private val sCalculateRight: CalculateSlide = object : CalculateSlideHorizontal() {
            override fun getGoneX(
                sceneRoot: ViewGroup,
                view: View,
                getWidth: (View, ViewGroup) -> Int
            ): Float {
                return view.translationX + getWidth(view, sceneRoot)
            }
        }
        private val sCalculateEnd: CalculateSlide = object : CalculateSlideHorizontal() {
            override fun getGoneX(
                sceneRoot: ViewGroup,
                view: View,
                getWidth: (View, ViewGroup) -> Int
            ): Float {
                val isRtl = (ViewCompat.getLayoutDirection(sceneRoot)
                        == ViewCompat.LAYOUT_DIRECTION_RTL)
                val x: Float
                x = if (isRtl) {
                    view.translationX - getWidth(view, sceneRoot)
                } else {
                    view.translationX + getWidth(view, sceneRoot)
                }
                return x
            }
        }
        private val sCalculateBottom: CalculateSlide = object : CalculateSlideVertical() {
            override fun getGoneY(
                sceneRoot: ViewGroup,
                view: View,
                getHeight: (View, ViewGroup) -> Int
            ): Float {
                return view.translationY + getHeight(view, sceneRoot)
            }
        }
    }
}

internal object TranslationAnimationCreator {
    /**
     * Creates an animator that can be used for x and/or y translations. When interrupted,
     * it sets a tag to keep track of the position so that it may be continued from position.
     *
     * @param view         The view being moved. This may be in the overlay for onDisappear.
     * @param values       The values containing the view in the view hierarchy.
     * @param viewPosX     The x screen coordinate of view
     * @param viewPosY     The y screen coordinate of view
     * @param startX       The start translation x of view
     * @param startY       The start translation y of view
     * @param endX         The end translation x of view
     * @param endY         The end translation y of view
     * @param interpolator The interpolator to use with this animator.
     * @return An animator that moves from (startX, startY) to (endX, endY) unless there was
     * a previous interruption, in which case it moves from the current position to (endX, endY).
     */
    fun createAnimation(
        view: View,
        values: TransitionValues,
        viewPosX: Int,
        viewPosY: Int,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        interpolator: TimeInterpolator?,
        transition: Transition
    ): Animator? {
        var startXPos = startX
        var startYPos = startY
        val terminalX = view.translationX
        val terminalY = view.translationY
        val startPosition = values.view.getTag(R.id.transition_position) as? IntArray
        if (startPosition != null) {
            startXPos = startPosition[0] - viewPosX + terminalX
            startYPos = startPosition[1] - viewPosY + terminalY
        }
        // Initial position is at translation startX, startY, so position is offset by that amount
        val startPosX = viewPosX + (startXPos - terminalX).roundToInt()
        val startPosY = viewPosY + (startYPos - terminalY).roundToInt()
        view.translationX = startXPos
        view.translationY = startYPos
        if (startXPos == endX && startYPos == endY) {
            return null
        }
        val anim = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, startXPos, endX),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, startYPos, endY)
        )
        val listener =
            TransitionPositionListener(
                view, values.view,
                startPosX, startPosY, terminalX, terminalY
            )
        transition.addListener(listener)
        anim.addListener(listener)
        anim.addPauseListener(listener)
        anim.interpolator = interpolator
        return anim
    }

    private class TransitionPositionListener internal constructor(
        private val mMovingView: View, private val mViewInHierarchy: View,
        startX: Int, startY: Int, terminalX: Float, terminalY: Float
    ) :
        AnimatorListenerAdapter(), Transition.TransitionListener {
        private val mStartX: Int = startX - mMovingView.translationX.roundToInt()
        private val mStartY: Int = startY - mMovingView.translationY.roundToInt()
        private var mTransitionPosition: IntArray?
        private var mPausedX = 0f
        private var mPausedY = 0f
        private val mTerminalX: Float = terminalX
        private val mTerminalY: Float = terminalY

        override fun onAnimationCancel(animation: Animator) {
            if (mTransitionPosition == null) {
                mTransitionPosition = IntArray(2)
            }
            mTransitionPosition!![0] =
                (mStartX + mMovingView.translationX).roundToInt()
            mTransitionPosition!![1] =
                (mStartY + mMovingView.translationY).roundToInt()
            mViewInHierarchy.setTag(R.id.transition_position, mTransitionPosition)
        }

        override fun onAnimationPause(animator: Animator) {
            mPausedX = mMovingView.translationX
            mPausedY = mMovingView.translationY
            mMovingView.translationX = mTerminalX
            mMovingView.translationY = mTerminalY
        }

        override fun onAnimationResume(animator: Animator) {
            mMovingView.translationX = mPausedX
            mMovingView.translationY = mPausedY
        }

        override fun onTransitionStart(transition: Transition) {}
        override fun onTransitionEnd(transition: Transition) {
            mMovingView.translationX = mTerminalX
            mMovingView.translationY = mTerminalY
            transition.removeListener(this)
        }

        override fun onTransitionCancel(transition: Transition) {}
        override fun onTransitionPause(transition: Transition) {}
        override fun onTransitionResume(transition: Transition) {}

        init {
            mTransitionPosition = mViewInHierarchy.getTag(R.id.transition_position) as? IntArray
            if (mTransitionPosition != null) {
                mViewInHierarchy.setTag(R.id.transition_position, null)
            }
        }
    }
}

@SuppressLint("RtlHardcoded")
class SidePropagation(
    private val getHeight: (View, ViewGroup) -> Int,
    private val getWidth: (View, ViewGroup) -> Int
) : VisibilityPropagation() {
    private var mPropagationSpeed = 3.0f
    private var mSide = Gravity.BOTTOM

    /**
     * Sets the side that is used to calculate the transition propagation. If the transitioning
     * View is visible in the start of the transition, then it will transition sooner when
     * closer to the side and later when farther. If the view is not visible in the start of
     * the transition, then it will transition later when closer to the side and sooner when
     * farther from the edge. The default is [Gravity.BOTTOM].
     *
     * @param side The side that is used to calculate the transition propagation. Must be one of
     * [Gravity.LEFT], [Gravity.TOP], [Gravity.RIGHT],
     * [Gravity.BOTTOM], [Gravity.START], or [Gravity.END].
     */
    fun setSide(@GravityFlag side: Int) {
        mSide = side
    }

    /**
     * Sets the speed at which transition propagation happens, relative to the duration of the
     * Transition. A `propagationSpeed` of 1 means that a View centered at the side
     * set in [.setSide] and View centered at the opposite edge will have a difference
     * in start delay of approximately the duration of the Transition. A speed of 2 means the
     * start delay difference will be approximately half of the duration of the transition. A
     * value of 0 is illegal, but negative values will invert the propagation.
     *
     * @param propagationSpeed The speed at which propagation occurs, relative to the duration
     * of the transition. A speed of 4 means it works 4 times as fast
     * as the duration of the transition. May not be 0.
     */
    fun setPropagationSpeed(propagationSpeed: Float) {
        require(propagationSpeed != 0f) { "propagationSpeed may not be 0" }
        mPropagationSpeed = propagationSpeed
    }

    override fun getStartDelay(
        sceneRoot: ViewGroup,
        transition: Transition,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Long {
        if (startValues == null && endValues == null) {
            return 0
        }
        var directionMultiplier = 1
        val epicenter = transition.epicenter
        val positionValues: TransitionValues?
        if (endValues == null || getViewVisibility(startValues) == View.VISIBLE) {
            positionValues = startValues
            directionMultiplier = -1
        } else {
            positionValues = endValues
        }
        val viewCenterX = getViewX(positionValues)
        val viewCenterY = getViewY(positionValues)
        val loc = IntArray(2)
        sceneRoot.getLocationOnScreen(loc)
        val left = loc[0] + sceneRoot.translationX.roundToInt()
        val top = loc[1] + sceneRoot.translationY.roundToInt()
        val right = left + sceneRoot.width
        val bottom = top + sceneRoot.height
        val epicenterX: Int
        val epicenterY: Int
        if (epicenter != null) {
            epicenterX = epicenter.centerX()
            epicenterY = epicenter.centerY()
        } else {
            epicenterX = (left + right) / 2
            epicenterY = (top + bottom) / 2
        }
        val distance = distance(
            sceneRoot, viewCenterX, viewCenterY, epicenterX, epicenterY,
            left, top, right, bottom
        ).toFloat()
        val maxDistance = getMaxDistance(sceneRoot).toFloat()
        val distanceFraction = distance / maxDistance
        var duration = transition.duration
        if (duration < 0) {
            duration = 300
        }
        return (duration * directionMultiplier / mPropagationSpeed * distanceFraction).roundToLong()
    }

    private fun distance(
        sceneRoot: View, viewX: Int, viewY: Int, epicenterX: Int, epicenterY: Int,
        left: Int, top: Int, right: Int, bottom: Int
    ): Int {
        val side: Int
        side = if (mSide == Gravity.START) {
            val isRtl = (ViewCompat.getLayoutDirection(sceneRoot)
                    == ViewCompat.LAYOUT_DIRECTION_RTL)
            if (isRtl) Gravity.RIGHT else Gravity.LEFT
        } else if (mSide == Gravity.END) {
            val isRtl = (ViewCompat.getLayoutDirection(sceneRoot)
                    == ViewCompat.LAYOUT_DIRECTION_RTL)
            if (isRtl) Gravity.LEFT else Gravity.RIGHT
        } else {
            mSide
        }
        var distance = 0
        when (side) {
            Gravity.LEFT -> distance = right - viewX + Math.abs(epicenterY - viewY)
            Gravity.TOP -> distance = bottom - viewY + Math.abs(epicenterX - viewX)
            Gravity.RIGHT -> distance = viewX - left + Math.abs(epicenterY - viewY)
            Gravity.BOTTOM -> distance = viewY - top + Math.abs(epicenterX - viewX)
        }
        return distance
    }

    private fun getMaxDistance(sceneRoot: ViewGroup): Int {
        return when (mSide) {
            Gravity.LEFT, Gravity.RIGHT, Gravity.START, Gravity.END -> sceneRoot.width
            else -> sceneRoot.height
        }
    }
}




