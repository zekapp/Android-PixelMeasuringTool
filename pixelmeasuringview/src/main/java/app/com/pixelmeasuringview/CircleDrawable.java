package app.com.pixelmeasuringview;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

/**
 * Created by Zeki Guler on 07,December,2015
 * ©2015 Appscore. All Rights Reserved
 */
public class CircleDrawable extends ShapeDrawable{

    private static final float INITIAL_HEIGHT   = 100.f;
    private static final float INITIAL_WIDTH    = 100.f;
    private static final float DEFAULT_STROKE_WIDTH = 2f;
    private static final float SELECTED_STROKE_WIDTH = 5f;

    private float mPosX;
    private float mPosY;
    private float mScaleFactor = 1.f;
    private float height;
    private float width;
    private  DashPathEffect mPathEffect;
    private boolean mIsSelected;

    public CircleDrawable(){
        super(new OvalShape());
        this.height = INITIAL_HEIGHT;
        this.width = INITIAL_WIDTH;
        this.mPosX = 0;
        this.mPosY = 0;
        this.mPathEffect = new DashPathEffect(new float[]{10.f,5.0f}, 0);
        getPaint().setStyle(Paint.Style.STROKE);
        getPaint().setColor(Color.WHITE);
        getPaint().setPathEffect(mPathEffect);
        getPaint().setStrokeWidth(DEFAULT_STROKE_WIDTH);
        setBounds(0, 0, (int)INITIAL_HEIGHT, (int)INITIAL_WIDTH);
    }

    public float getPosX() {
        return mPosX;
    }

    public void setPosX(float posX) {
        mPosX = posX;
    }

    public float getPosY() {
        return mPosY;
    }

    public void setPosY(float posY) {
        mPosY = posY;
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        mScaleFactor = scaleFactor;
        height = INITIAL_HEIGHT * scaleFactor;
        width  = INITIAL_WIDTH * scaleFactor;
        getPaint().setStrokeWidth(mIsSelected? DEFAULT_STROKE_WIDTH : SELECTED_STROKE_WIDTH);
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }


    public float getExactCenterX() {
        return getPosX() + getHeight()/2;
    }
    public float getExactCenterY() {
        return getPosY() + getHeight()/2;
    }

    public float getDiameter() {
        return getHeight() + getPaint().getStrokeWidth() * getScaleFactor();
    }

    public void setAsSelected(boolean isSelected) {
        mIsSelected = isSelected;
        getPaint().setStrokeWidth(isSelected ? SELECTED_STROKE_WIDTH: DEFAULT_STROKE_WIDTH);
        getPaint().setPathEffect(mPathEffect);
    }
}