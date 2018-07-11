package app.com.pixelmeasuringview

import android.graphics.DashPathEffect
import android.graphics.Paint

import java.text.NumberFormat

/**
 * Created by Zeki Guler on 10,December,2015
 * ©2015 Appscore. All Rights Reserved
 */
class Ruler(color: Int) {

    val rulerTextPaint: Paint
    val linePaint: Paint
    private val mNumberFormat: NumberFormat = NumberFormat.getInstance()
    var path: Array<FloatArray>? = null
    private var mLength = 0.0

    val centerOfRuler: FloatArray
        get() {
            val center = floatArrayOf(0f, 0f)
            if (path == null) return center

            val x1 = path!![0][0]
            val y1 = path!![0][1]
            val x2 = path!![1][0]
            val y2 = path!![1][1]

            center[0] = (x1 + x2) / 2
            center[1] = (y1 + y2) / 2

            return center

        }

    val length: String
        get() = mNumberFormat.format(mLength) + " px"

    init {

        mNumberFormat.maximumFractionDigits = 3

        // initLinePaint
        linePaint = Paint()
        linePaint.strokeWidth = 2f
        linePaint.color = color
        linePaint.style = Paint.Style.STROKE
        linePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 5.0f), 5f)

        //init textPaint
        rulerTextPaint = Paint()
        rulerTextPaint.color = color
        rulerTextPaint.textSize = 41f
    }

    fun setLength(length: Double) {
        mLength = length
    }
}
