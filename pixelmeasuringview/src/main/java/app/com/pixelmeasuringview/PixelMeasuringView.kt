package app.com.pixelmeasuringview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView

import java.util.ArrayList

/**
 * Created by Zeki Guler on 07,December,2015
 * ©2015 Appscore. All Rights Reserved
 */
class PixelMeasuringView : ImageView {
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mLastTouchX: Float = 0.toFloat()
    private var mLastTouchY: Float = 0.toFloat()
    private var mActivePointerId = INVALID_POINTER_ID
    private var mCallback: PixelMeasuringCallback? = null

    private val mCircles = ArrayList<CircleDrawable>()
    private var mSelectedCircle: CircleDrawable? = null
    private var isCircleLocked = false
    private var isCircleMoving = false
    private var mRuler: Ruler? = null
    private var isMeasureTextVisible = false
    private var isLineAllwaysActive = true

    private val closestCircle: CircleDrawable
        get() {
            var curX: Float
            var curY: Float
            var minDis = java.lang.Double.MAX_VALUE

            var closestCircle = mCircles[0]

            unSelectAll()

            for (circle in mCircles) {
                curX = circle.posX
                curY = circle.posY

                val dist = Math.sqrt(((curX - mLastTouchX) * (curX - mLastTouchX) + (curY - mLastTouchY) * (curY - mLastTouchY)).toDouble())

                circle.setAsSelected(false)
                if (minDis > dist) {
                    closestCircle = circle
                    minDis = dist
                }
            }

            closestCircle.setAsSelected(true)
            return closestCircle
        }

    constructor(context: Context) : super(context) {
        init(context, false, true, Color.BLACK, Color.BLACK)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.InstantMeasuringView)

        val isTextVisible = a.getBoolean(R.styleable.InstantMeasuringView_is_measure_text_visible, false)
        val isLineAllwaysActive = a.getBoolean(R.styleable.InstantMeasuringView_is_line_all_visible, false)
        val cirlesColor = a.getColor(R.styleable.InstantMeasuringView_circles_color, Color.BLACK)
        val rulerColor = a.getColor(R.styleable.InstantMeasuringView_ruler_color, Color.BLACK)

        a.recycle()

        init(context, isTextVisible, isLineAllwaysActive, cirlesColor, rulerColor)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.InstantMeasuringView, defStyle, 0)

        val isTextVisible = a.getBoolean(R.styleable.InstantMeasuringView_is_measure_text_visible, false)
        val isLineAllwaysActive = a.getBoolean(R.styleable.InstantMeasuringView_is_line_all_visible, false)
        val cirlesColor = a.getColor(R.styleable.InstantMeasuringView_circles_color, Color.BLACK)
        val rulerColor = a.getColor(R.styleable.InstantMeasuringView_ruler_color, Color.BLACK)

        a.recycle()
        init(context, isTextVisible, isLineAllwaysActive, cirlesColor, rulerColor)
    }

    private fun init(context: Context, visible: Boolean, active: Boolean, cirleColor: Int, rulerColor: Int) {

        this.isMeasureTextVisible = visible
        this.isLineAllwaysActive = active


        mRuler = Ruler(rulerColor)

        val c1 = CircleDrawable(cirleColor)
        c1.posX = 100f
        c1.posY = 400f
        mCircles.add(c1)

        val c2 = CircleDrawable(cirleColor)
        c2.posX = 400f
        c2.posY = 600f
        c2.scaleFactor = 1.7f
        mCircles.add(c2)

        drawLineBetweenCircles()

        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
    }

    fun setCallback(callback: PixelMeasuringCallback) {
        this.mCallback = callback
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {

        // if circle locked by user do not create any event.
        if (isCircleLocked) return false

        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector!!.onTouchEvent(ev)

        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {

            MotionEvent.ACTION_DOWN -> {

                val x = ev.x
                val y = ev.y

                mLastTouchX = x
                mLastTouchY = y

                mActivePointerId = ev.getPointerId(0)
                mSelectedCircle = closestCircle
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(mActivePointerId)
                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector!!.isInProgress) {

                    val dx = x - mLastTouchX
                    val dy = y - mLastTouchY

                    mSelectedCircle!!.posX = mSelectedCircle!!.posX + dx
                    mSelectedCircle!!.posY = mSelectedCircle!!.posY + dy

                    Log.d("ACTION_MOVE", " dx : $dx dy: $dy")
                    invalidate()
                }

                mLastTouchX = x
                mLastTouchY = y

                drawLineBetweenCircles()

                isCircleMoving = true
            }

            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
                isCircleMoving = false
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
                isCircleMoving = false
                invalidate()
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = ev.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastTouchX = ev.getX(newPointerIndex)
                    mLastTouchY = ev.getY(newPointerIndex)
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                }
            }
        }

        return true
    }

    private fun drawLineBetweenCircles() {
        if (mCircles.size < 2)
            return

        calculateDistance()

        val r1 = mCircles[0].diameter / 2
        val x1 = mCircles[0].exactCenterX
        val y1 = mCircles[0].exactCenterY

        val r2 = mCircles[1].diameter / 2
        val x2 = mCircles[1].exactCenterX
        val y2 = mCircles[1].exactCenterY

        mRuler!!.path = getLineStartEndPointBetweenCircles(r1, x1, y1, r2, x2, y2)

    }

    /**
     * This function returns the start and end points of the shortest path
     * between two circles.
     *
     * @param r1: radius of circle 1
     * @param x1: x coordinate of the the circle's center
     * @param y1: y coordinate of the the circle's center
     * @param r2: radius of circle 2
     * @param x2: x coordinate of the the circle's center
     * @param y2: y coordinate of the the circle's center
     *
     * @return : 2d float array
     * first  row gives the touch point's coordinate of the circle-1
     * second row gives the touch point's coordinate of the circle-2
     *
     * [x1'][y1']
     * [x2'][y2']
     */
    private fun getLineStartEndPointBetweenCircles(r1: Float, x1: Float, y1: Float, r2: Float, x2: Float, y2: Float): Array<FloatArray> {

        val p = Array(2) { FloatArray(2) }

        val d = centerToCenterDist().toFloat()

        // x1'
        p[0][0] = (r1 * x2 + x1 * (d - r1)) / d

        // y1'
        p[0][1] = (r1 * y2 + y1 * (d - r1)) / d

        // x2'
        p[1][0] = (r2 * x1 + x2 * (d - r2)) / d

        // y2'
        p[1][1] = (r2 * y1 + y2 * (d - r2)) / d


        return p

    }

    private fun calculateDistance() {
        if (mCircles.size < 2)
            return

        var distance = centerToCenterDist()
        val sumOfDiameters = sumOfDiameters()

        distance -= sumOfDiameters / 2

        mRuler!!.setLength(distance)

        if (mCallback != null)
            mCallback!!.distanceBetweenCircles(distance.toFloat())
    }

    private fun centerToCenterDist(): Double {
        val centers = findCentersOfCircles()
        return Math.sqrt(calculateDifference(centers))
    }

    private fun sumOfDiameters(): Double {
        var sum = 0.0
        for (c in mCircles) {
            sum += c.diameter.toDouble()
        }
        return sum
    }

    private fun calculateDifference(centers: Array<FloatArray>): Double {
        // (x1 - x2) * (x1 - x2)
        val dx2 = ((centers[0][0] - centers[1][0]) * (centers[0][0] - centers[1][0])).toDouble()
        // (y1 - y2) * (y1 - y2)
        val dy2 = ((centers[0][1] - centers[1][1]) * (centers[0][1] - centers[1][1])).toDouble()

        return dx2 + dy2
    }

    private fun findCentersOfCircles(): Array<FloatArray> {
        val centers = Array(mCircles.size) { FloatArray(2) }

        for ((i, c) in mCircles.withIndex()) {
            centers[i][0] = c.posX + c.width / 2
            centers[i][1] = c.posY + c.height / 2
        }

        return centers
    }

    private fun unSelectAll() {
        for (c in mCircles) {
            c.setAsSelected(false)
        }
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        for (circle in mCircles) {
            canvas.save()
            canvas.translate(circle.posX, circle.posY)
            canvas.scale(circle.scaleFactor, circle.scaleFactor)
            circle.draw(canvas)
            canvas.restore()
        }

        // stop complain Ide
        val ruler = mRuler?:return

        if (ruler.path != null) {

            if (isLineAllwaysActive || !isCircleMoving) {
                val p = ruler.path!!
                canvas.drawLine(p[0][0], p[0][1], p[1][0], p[1][1], ruler.linePaint)

                if (isMeasureTextVisible) {
                    val c = ruler.centerOfRuler
                    canvas.drawText(ruler.length, c[0], c[1], ruler.rulerTextPaint)
                }

            }
        }
    }

    override fun setBackground(background: Drawable) {
        super.setBackground(background)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor = mSelectedCircle!!.scaleFactor
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))
            mSelectedCircle!!.scaleFactor = mScaleFactor
            invalidate()
            return true
        }
    }

    companion object {

        private const val INVALID_POINTER_ID = -1
    }
}
