package app.com.pixelmeasuringview;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import java.text.NumberFormat;

/**
 * Created by Zeki Guler on 10,December,2015
 * ©2015 Appscore. All Rights Reserved
 */
public class Ruler  {

    private final Paint mTextPaint;
    private final Paint mLinePaint;
    private final NumberFormat mNumberFormat;
    private float[][] mStartEndPoints = null;
    private double mLength = 0;

    public Ruler(){
        super();

        mNumberFormat = NumberFormat.getInstance();
        mNumberFormat.setMaximumFractionDigits(3);

        // initLinePaint
        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(2.f);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{10.f,5.0f}, 5));

        //init textPaint
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
    }

    public void setPath(float[][] linePoints) {
        mStartEndPoints = linePoints;
    }

    public float[][] getPath() {
        return mStartEndPoints;
    }

    public Paint getLinePaint() {
        return mLinePaint;
    }

    public float[] getCenterOfRuler(){
        float[] center = new float[]{0.f,0.f};
        if (mStartEndPoints == null) return center;

        float x1 = mStartEndPoints[0][0];
        float y1 = mStartEndPoints[0][1];
        float x2 = mStartEndPoints[1][0];
        float y2 = mStartEndPoints[1][1];

        center[0] = (x1 + x2) / 2;
        center[1] = (y1 + y2) / 2;

        return center;

    }

    public void setLength(double length) {
        mLength = length;
    }

    public String getLength() {
        return mNumberFormat.format(mLength) + " px";
    }

    public Paint getRulerTextPaint() {
        return mTextPaint;
    }
}
