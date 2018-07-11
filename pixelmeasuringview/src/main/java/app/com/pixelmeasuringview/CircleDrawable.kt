package app.com.pixelmeasuringview

import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape

/**
 * Created by Zeki Guler on 07,December,2015
 * ©2015 Appscore. All Rights Reserved
 */
class CircleDrawable(color: Int) : ShapeDrawable(OvalShape()) {

    var posX: Float = 0.toFloat()
    var posY: Float = 0.toFloat()
    var scaleFactor = 1f
        set(scaleFactor) {
            field = scaleFactor
            height = INITIAL_HEIGHT * scaleFactor
            width = INITIAL_WIDTH * scaleFactor
            paint.strokeWidth = if (mIsSelected) DEFAULT_STROKE_WIDTH else SELECTED_STROKE_WIDTH
        }
    var height: Float = 0.toFloat()
        private set
    var width: Float = 0.toFloat()
        private set
    private val mPathEffect: DashPathEffect
    private var mIsSelected: Boolean = false


    val exactCenterX: Float
        get() = posX + height / 2
    val exactCenterY: Float
        get() = posY + height / 2

    val diameter: Float
        get() = height + paint.strokeWidth * scaleFactor

    init {
        this.height = INITIAL_HEIGHT
        this.width = INITIAL_WIDTH
        this.posX = 0f
        this.posY = 0f
        this.mPathEffect = DashPathEffect(floatArrayOf(10f, 5.0f), 0f)
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.pathEffect = mPathEffect
        paint.strokeWidth = DEFAULT_STROKE_WIDTH
        setBounds(0, 0, INITIAL_HEIGHT.toInt(), INITIAL_WIDTH.toInt())
    }

    fun setAsSelected(isSelected: Boolean) {
        mIsSelected = isSelected
        paint.strokeWidth = if (isSelected) SELECTED_STROKE_WIDTH else DEFAULT_STROKE_WIDTH
        paint.pathEffect = mPathEffect
    }

    companion object {

        private const val INITIAL_HEIGHT = 100f
        private const val INITIAL_WIDTH = 100f
        private const val DEFAULT_STROKE_WIDTH = 2f
        private const val SELECTED_STROKE_WIDTH = 5f
    }
}