package com.example.deok.testapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by YoungWon on 2015-03-12.
 */
public class SpeedmeterPackage extends View implements ModeChangeListener{
    private Speedometer speedmeter;
    private Speedometer speedmeter_1;
    private Speedometer speedmeter_2;
    private Speedometer speedmeter_3;
    private Speedometer speedmeter_4;
    private Speedometer speedmeter_5;
    private static boolean BATTERY_MODE = true;
    private static boolean MOTOR_MODE = false;

    private boolean mode;

    private Paint onMarkPaint;
    private int ON_COLOR = Color.argb(255, 0xff, 0xA5, 0x00);
    Path path;
    public SpeedmeterPackage(Context context) {
        super(context);
    }

    public SpeedmeterPackage(Context context, AttributeSet attrs) {
        super(context, attrs);
       // LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        mode = BATTERY_MODE;

        path = new Path();


        onMarkPaint = new Paint();
        onMarkPaint.setStyle(Paint.Style.STROKE);
        onMarkPaint.setColor(Color.WHITE);

        onMarkPaint.setStrokeWidth(8f);//35f
        onMarkPaint.setShadowLayer(5f, 0f, 0f, Color.WHITE);
        onMarkPaint.setAntiAlias(true);
    }
    @Override
    public void onDraw(Canvas canvas){

    }
    static public void setSpeedmeterXs(float x1, float x2,float x3,float x4,float x5,float x6){

    }
    static public void setSpeedmeterYs(float y1, float y2,float y3,float y4,float y5,float y6){

    }

    @Override
    public void onModeChanged(boolean state) {
        mode = state;
        this.invalidate();
    }
}
