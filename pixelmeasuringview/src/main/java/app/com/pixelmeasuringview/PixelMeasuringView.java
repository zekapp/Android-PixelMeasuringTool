package app.com.pixelmeasuringview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Zeki Guler on 07,December,2015
 * ©2015 Appscore. All Rights Reserved
 */
public class PixelMeasuringView extends ImageView {

    private static final int INVALID_POINTER_ID = -1;
    private ScaleGestureDetector mScaleDetector;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private PixelMeasuringCallback mCallback = null;

    private ArrayList<CircleDrawable> mCircles = new ArrayList<>();
    private CircleDrawable mSelectedCircle;
    private boolean isCirleLocked = false;
    private boolean isCircleMoving = false;
    private Ruler mRuler;
    private boolean isMeasureTextVisible = false;
    private boolean isLineAllwaysActive = true;

    public PixelMeasuringView(Context context) {
        super(context);
        init(context, false, true, Color.BLACK,Color.BLACK);
    }

    public PixelMeasuringView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InstantMeasuringView);

        boolean isTextVisible           =  a.getBoolean (R.styleable.InstantMeasuringView_is_measure_text_visible, false);
        boolean isLineAllwaysActive     =  a.getBoolean (R.styleable.InstantMeasuringView_is_line_all_visible, false);
        int cirlesColor                 =  a.getColor   (R.styleable.InstantMeasuringView_circles_color, Color.BLACK);
        int rulerColor                  =  a.getColor   (R.styleable.InstantMeasuringView_ruler_color, Color.BLACK);

        a.recycle();

        init(context,isTextVisible, isLineAllwaysActive, cirlesColor,rulerColor );
    }

    public PixelMeasuringView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InstantMeasuringView, defStyle, 0);

        boolean isTextVisible           =  a.getBoolean (R.styleable.InstantMeasuringView_is_measure_text_visible, false);
        boolean isLineAllwaysActive     =  a.getBoolean (R.styleable.InstantMeasuringView_is_line_all_visible, false);
        int cirlesColor                 =  a.getColor   (R.styleable.InstantMeasuringView_circles_color, Color.BLACK);
        int rulerColor                  =  a.getColor   (R.styleable.InstantMeasuringView_ruler_color, Color.BLACK);

        a.recycle();
        init(context,isTextVisible, isLineAllwaysActive, cirlesColor,rulerColor);
    }

    private void init(Context context, boolean visible, boolean active, int cirleColor, int rulerColor) {

        this.isMeasureTextVisible = visible;
        this.isLineAllwaysActive = active;


        mRuler = new Ruler(rulerColor);

        CircleDrawable c1 = new CircleDrawable(cirleColor);
        c1.setPosX(100);
        c1.setPosY(400);
        mCircles.add(c1);

        CircleDrawable c2 = new CircleDrawable(cirleColor);
        c2.setPosX(400);
        c2.setPosY(600);
        c2.setScaleFactor(1.7f);
        mCircles.add(c2);

        drawLineBetweenCircles();

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setCallback(PixelMeasuringCallback callback){
        this.mCallback = callback;
    }

    public boolean lockCircles(){
        return isCirleLocked = !isCirleLocked;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        // if circle locked by user do not create any event.
        if (isCirleLocked) return false;

        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: {

                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                mActivePointerId = ev.getPointerId(0);
                mSelectedCircle  = getClosestCircle();
                invalidate();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {

                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mSelectedCircle.setPosX(mSelectedCircle.getPosX() + dx);
                    mSelectedCircle.setPosY(mSelectedCircle.getPosY() + dy);

                    Log.d("ACTION_MOVE" ," dx : " + dx + " dy: " + dy );
                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                drawLineBetweenCircles();

                isCircleMoving = true;
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                isCircleMoving = false;
                invalidate();
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                isCircleMoving = false;
                invalidate();
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    private void drawLineBetweenCircles() {
        if (mCircles.size() < 2)
            return;

        calculateDistance();

        float r1 = mCircles.get(0).getDiameter() / 2;
        float x1 = mCircles.get(0).getExactCenterX();
        float y1 = mCircles.get(0).getExactCenterY();

        float r2 = mCircles.get(1).getDiameter() / 2;
        float x2 = mCircles.get(1).getExactCenterX();
        float y2 = mCircles.get(1).getExactCenterY();

        mRuler.setPath(getLineStartEndPointBetweenCircles(r1,x1,y1,r2,x2,y2));

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
     *  @return : 2d float array
     *  first  row gives the touch point's coordinate of the circle-1
     *  second row gives the touch point's coordinate of the circle-2
     *
     *          [x1'][y1']
     *          [x2'][y2']
     * */
    private float[][] getLineStartEndPointBetweenCircles(float r1, float x1, float y1, float r2, float x2, float y2) {

        float[][] p = new float[2][2];

        float d =  (float)centerToCenterDist();

        // x1'
        p[0][0] = (r1 * x2 + x1 * (d - r1)) / d;

        // y1'
        p[0][1] = (r1 * y2 + y1 * (d - r1)) / d ;

        // x2'
        p[1][0] = (r2 * x1 + x2 * (d - r2)) / d;

        // y2'
        p[1][1] = (r2 * y1 + y2 * (d - r2)) / d;


        return p;

    }

    private void calculateDistance() {
        if (mCircles.size() < 2)
            return;

        double distance = centerToCenterDist();
        double sumOfDiameters = sumOfDiameters();

        distance = distance - (sumOfDiameters / 2);

        mRuler.setLength(distance);

        if(mCallback != null)
            mCallback.distanceBetweenCircles((float)distance);
    }

    private double centerToCenterDist() {
        float[][] centers = findCentersOfCircles();
        return Math.sqrt(calculateDifference(centers));
    }

    private double sumOfDiameters() {
        double sum  =0;
        for (CircleDrawable c : mCircles){
            sum += c.getDiameter();
        }
        return sum;
    }

    private double calculateDifference(float[][] centers) {
        // (x1 - x2) * (x1 - x2)
        double dx2 =  (centers[0][0] - centers[1][0]) * (centers[0][0] - centers[1][0]);
        // (y1 - y2) * (y1 - y2)
        double dy2 =  (centers[0][1] - centers[1][1]) * (centers[0][1] - centers[1][1]);

        return dx2 + dy2;
    }

    private float[][] findCentersOfCircles() {
        float[][] centers = new float[mCircles.size()][2];

        int i =0;
        for (CircleDrawable c : mCircles){
            centers[i][0] = c.getPosX() + c.getWidth() / 2;
            centers[i][1] = c.getPosY() + c.getHeight() / 2;
            i++;
        }

        return centers;
    }

    private CircleDrawable getClosestCircle() {
        float curX;
        float curY;
        double minDis = Double.MAX_VALUE;

        CircleDrawable closestCircle = mCircles.get(0);

        unSelectAll();

        for (CircleDrawable circle : mCircles){
            curX = circle.getPosX();
            curY = circle.getPosY();

            double dist = Math.sqrt((curX - mLastTouchX) * (curX - mLastTouchX) + (curY - mLastTouchY) * (curY - mLastTouchY));

            circle.setAsSelected(false);
            if (minDis > dist) {
                closestCircle = circle;
                minDis = dist;
            }
        }

        closestCircle.setAsSelected(true);
        return closestCircle;
    }

    private void unSelectAll() {
        for (CircleDrawable c : mCircles){
            c.setAsSelected(false);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        for (CircleDrawable circle : mCircles){
            canvas.save();
            canvas.translate(circle.getPosX(), circle.getPosY());
            canvas.scale(circle.getScaleFactor(), circle.getScaleFactor());
            circle.draw(canvas);
            canvas.restore();
        }

        if (mRuler.getPath() != null){

            if(isLineAllwaysActive || !isCircleMoving){
                float[][] p = mRuler.getPath();
                canvas.drawLine(p[0][0],p[0][1],p[1][0],p[1][1], mRuler.getLinePaint());

                if (isMeasureTextVisible){
                    float[] c = mRuler.getCenterOfRuler();
                    canvas.drawText(mRuler.getLength(),c[0],c[1], mRuler.getRulerTextPaint());
                }

            }
        }
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = mSelectedCircle.getScaleFactor();
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            mSelectedCircle.setScaleFactor(mScaleFactor);
            invalidate();
            return true;
        }
    }
}
