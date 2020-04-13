package com.greenmist.android.sandbox

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import com.caverock.androidsvg.SVGImageView
import com.github.chrisbanes.photoview.PhotoViewAttacher

class ZoomSvgImageView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SVGImageView(context, attrs, defStyleAttr) {

    private val attacher: PhotoViewAttacher = PhotoViewAttacher(this)

    init {
        super.setScaleType(ScaleType.MATRIX)
        attacher.maximumScale = 100f
        attacher.minimumScale = 1f
    }

    override fun getScaleType(): ScaleType? {
        return attacher.scaleType
    }

    override fun getImageMatrix(): Matrix? {
        return attacher.imageMatrix
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        attacher.setOnLongClickListener(l)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        attacher.setOnClickListener(l)
    }

    override fun setScaleType(scaleType: ScaleType) {
        attacher.scaleType = scaleType
    }

    fun getMinimumScale(): Float {
        return attacher.minimumScale
    }

    fun getMediumScale(): Float {
        return attacher.mediumScale
    }

    fun getMaximumScale(): Float {
        return attacher.maximumScale
    }

    fun getScale(): Float {
        return attacher.scale
    }
}