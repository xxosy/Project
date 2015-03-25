package com.example.deok.testapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by YoungWon on 2015-03-18.
 */
public class ActiveSet extends View {
    private static final String TAG = Speedometer.class.getSimpleName();
    private class Position{
        int x;
        int y;
    }
    ArrayList<Integer> state;
    Position[] mPosition;
    private float mCurrentFigure;
    private float mMaxFigure;
    private int ON_COLOR;
    private int OFF_COLOR;
    // Scale drawing tools
    private Paint onMarkPaint;
    private Paint offMarkPaint;
    private Path offPath;
    final RectF oval = new RectF();

    // Drawing colors
    private int NOT_ACTIVE_COLOR =Color.argb(255, 0xff,0x00,0x00);
    private int ACTIVE_COLOR = Color.argb(255, 0x00,0x64,0x00);

    // Scale configuration
    private float centerX;
    private float centerY;

    public ActiveSet(Context context){
        super(context);
        Log.d(TAG,"Speedometer(Context) called");
    }

    public ActiveSet(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ActiveSet,
                0, 0);
        try{
            mMaxFigure = a.getFloat(R.styleable.ActiveSet_maxFigure, 100f);
            mCurrentFigure = a.getFloat(R.styleable.ActiveSet_currentFigure, 0);
            ACTIVE_COLOR = a.getColor(R.styleable.ActiveSet_activeColor, ACTIVE_COLOR);
            NOT_ACTIVE_COLOR = a.getColor(R.styleable.ActiveSet_notActiveColor, NOT_ACTIVE_COLOR);
        } finally{
            a.recycle();
        }

        state = new ArrayList<Integer>();

        for(int i = 0;i<9;i++){
            if(i%2==0)
                state.add(0);
            else
                state.add(1);
        }

        initPositon();
        initDrawingTools();
    }
    private void initPositon(){
        mPosition = new Position[9];
        for(int i =0;i<mPosition.length;i++){
            mPosition[i] = new Position();
        }
        mPosition[0].x = 150;
        mPosition[0].y = 275;
        mPosition[1].x = 390;
        mPosition[1].y = 148;
        mPosition[2].x = 390;
        mPosition[2].y = 275;
        mPosition[3].x = 655;
        mPosition[3].y = 275;
        mPosition[4].x = 627;
        mPosition[4].y = 78;
        mPosition[5].x = 777;
        mPosition[5].y = 78;
        mPosition[6].x = 910;
        mPosition[6].y = 148;
        mPosition[7].x = 910;
        mPosition[7].y = 275;
        mPosition[8].x = 1188;
        mPosition[8].y = 54;
    }
    private void initDrawingTools(){
        onMarkPaint = new Paint();
        onMarkPaint.setStyle(Paint.Style.STROKE);
        onMarkPaint.setColor(NOT_ACTIVE_COLOR);
        onMarkPaint.setStrokeWidth(20f);//35f
        onMarkPaint.setAntiAlias(true);

        offMarkPaint = new Paint(onMarkPaint);
        offMarkPaint.setStrokeWidth(20f);
        offMarkPaint.setColor(ACTIVE_COLOR);
        offMarkPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        offPath = new Path();
    }

    public float getCurrentSpeed() {
        return mCurrentFigure;
    }

    public void setCurrentState(ArrayList<Integer> state) {
        this.state = state;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        Log.d(TAG, "Size changed to " + width + "x" + height);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        centerX = 0;
        centerY = 0;
        setMeasuredDimension(1280, 342);
    }

    @Override
    public void onDraw(Canvas canvas){
        drawScaleBackground(canvas);
    }

    private void drawScaleBackground(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            offPath.reset();
            if (state.get(i) == 0) {
                onMarkPaint.setStrokeWidth(20);
                offPath.moveTo(mPosition[i].x, mPosition[i].y);
                offPath.lineTo(mPosition[i].x - 20, mPosition[i].y);
                canvas.drawPath(offPath, onMarkPaint);

            } else if (state.get(i) == 1) {
                offPath.moveTo(mPosition[i].x, mPosition[i].y);
                offPath.lineTo(mPosition[i].x - 20, mPosition[i].y);
                canvas.drawPath(offPath, offMarkPaint);
            }
        }
        if(state.get(8)==0) {
        offPath.reset();
        for (int j = 0; j < 22; j++) {
            offPath.moveTo(1188, 54);
            offPath.lineTo(1189 + j, 54);
            onMarkPaint.setStrokeWidth(22 - j);
            canvas.drawPath(offPath, onMarkPaint);
            }
        }else if(state.get(8)==1){
            offPath.reset();
            for (int j = 0; j < 22; j++) {
                offPath.moveTo(1070, 55);
                offPath.lineTo(1069 -j, 55);
                onMarkPaint.setStrokeWidth(22 - j);
                canvas.drawPath(offPath, onMarkPaint);
            }
        }
    }

    public void onStateChanged(ArrayList<Integer> state) {
        this.setCurrentState(state);
        this.invalidate();
    }
}