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

/**
 * Created by YoungWon on 2015-03-18.
 */
public class FigureBar extends View implements SpeedChangeListener {
    private static final String TAG = Speedometer.class.getSimpleName();
    public static final float DEFAULT_MAX_SPEED = 100f; // Assuming this is km/h and you drive a super-car

    // Speedometer internal state
    private float mMaxSpeed;
    private float mCurrentSpeed;
    private float mMaxValue;
    private float mMinValue;
    private float mNumOfMarking;
    private float mCurrentDisplayValue;
    // Scale drawing tools
    private Paint onMarkPaint;
    private Paint offMarkPaint;
    private Paint scalePaint;
    private Paint readingPaint;
    private Path onPath;
    private Path offPath;
    final RectF oval = new RectF();

    // Drawing colors
    private int ON_COLOR = Color.argb(255, 0xff, 0xA5, 0x00);
    private int WARING_COLOR = Color.argb(255,0xff,0x00,0x00);
    private int OFF_COLOR = Color.argb(255,0x3e,0x3e,0x3e);
    private int SCALE_COLOR = Color.argb(255, 255, 255, 255);
    private float SCALE_SIZE = 30f;
    private float READING_SIZE = 60f;

    // Scale configuration
    private float centerX;
    private float centerY;
    private float radius;

    public FigureBar(Context context){
        super(context);
        Log.d(TAG,"Speedometer(Context) called");
    }

    public FigureBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG,"Speedometer(Context, AttributeSet) called");
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Speedometer,
                0, 0);
        TypedArray b = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.FigureBar,
                0, 0);
        try{
            mMaxSpeed = 100f;//a.getFloat(R.styleable.Speedometer_maxSpeed, 100f);
            mCurrentSpeed = 50f;//a.getFloat(R.styleable.Speedometer_currentSpeed, 0);
            mMaxValue = b.getFloat(R.styleable.FigureBar_maxValue,100);
            mMinValue = b.getFloat(R.styleable.FigureBar_minValue,0);
            mNumOfMarking = b.getFloat(R.styleable.FigureBar_num_of_marking,4);
            ON_COLOR = a.getColor(R.styleable.Speedometer_onColor, ON_COLOR);
            OFF_COLOR = a.getColor(R.styleable.Speedometer_offColor, OFF_COLOR);
            SCALE_COLOR = a.getColor(R.styleable.Speedometer_scaleColor, SCALE_COLOR);
            SCALE_SIZE = a.getDimension(R.styleable.Speedometer_scaleTextSize, SCALE_SIZE);
            READING_SIZE = a.getDimension(R.styleable.Speedometer_readingTextSize, READING_SIZE);
        } finally{
            a.recycle();
        }
        initDrawingTools();
    }

    private void initDrawingTools(){
        onMarkPaint = new Paint();
        onMarkPaint.setStyle(Paint.Style.STROKE);
        onMarkPaint.setColor(Color.argb(255, 0xf0,0xf8,0xff));
        onMarkPaint.setStrokeWidth(50f);//35f
        onMarkPaint.setShadowLayer(5f, 0f, 0f, ON_COLOR);
        onMarkPaint.setAntiAlias(true);

        offMarkPaint = new Paint(onMarkPaint);
        offMarkPaint.setStrokeWidth(15f);
        offMarkPaint.setColor(OFF_COLOR);
        offMarkPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offMarkPaint.setShadowLayer(0f, 0f, 0f, OFF_COLOR);

        scalePaint = new Paint(offMarkPaint);
        scalePaint.setStrokeWidth(2f);
        scalePaint.setTextSize(SCALE_SIZE);
        scalePaint.setShadowLayer(5f, 0f, 0f, Color.RED);
        scalePaint.setColor(Color.GRAY);

        readingPaint = new Paint(scalePaint);
        readingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        readingPaint.setStrokeWidth(0.8f);
        offMarkPaint.setShadowLayer(3f, 0f, 0f, Color.WHITE);
        readingPaint.setTextSize(15f);//65f
        //readingPaint.setTypeface(Typeface.SANS_SERIF);
        readingPaint.setColor(Color.BLACK);

        onPath = new Path();
        offPath = new Path();
    }

    public float getCurrentSpeed() {
        return mCurrentSpeed;
    }

    public void setCurrentSpeed(float mCurrentSpeed) {
        /*if(mCurrentSpeed > this.mMaxSpeed)
            this.mCurrentSpeed = mMaxSpeed;
        else if(mCurrentSpeed < 0)
            this.mCurrentSpeed = 0;
        else
            this.mCurrentSpeed = mCurrentSpeed;*/
        this.mCurrentSpeed = mCurrentSpeed/1024*110;
        if(mMinValue<0)
            this.mCurrentDisplayValue = ((mCurrentSpeed/1024)*102)*2-100;
        else if(mMinValue==0)
            this.mCurrentDisplayValue = (mCurrentSpeed/1024)*110*(mMaxValue/100);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        Log.d(TAG, "Size changed to " + width + "x" + height);

        // Setting up the oval area in which the arc will be drawn
        if (width > height){
            radius = height/3;
        }else{
            radius = width/3;
        }
        oval.set(centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
//		Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec)*2;

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);
        centerX = chosenDimension/2;
        centerY = 0;
        setMeasuredDimension(100, 220);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPreferredSize();
        }
    }

    // in case there is no size specified
    private int getPreferredSize() {
        return 300;
    }

    @Override
    public void onDraw(Canvas canvas){

        drawScaleBackground(canvas);
        drawScale(canvas);
        //drawLegend(canvas);
        drawReading(canvas);
    }

    /**
     * Draws the segments in their OFF state
     * @param canvas
     */
    private void drawScaleBackground(Canvas canvas){

        //Log.d(TAG,"drawScaleBackground");
        offPath.reset();
        int minValue = (int) mMinValue;
        int maxValue = (int)mMaxValue;
        int markingValue = minValue;
        int plusValue = (int) ((maxValue-minValue)/(9-mNumOfMarking));


        int bigMarking = 0;
        for(int i = 210;i>0;i-=210/21){
            if(bigMarking%mNumOfMarking==0){
                offPath.moveTo(centerX,centerY+i);
                offPath.lineTo(centerX,centerY+(i+2));
                Path path = new Path();
                String message = String.format("%d", markingValue);
                float[] widths = new float["9999".length()];
                readingPaint.getTextWidths("9999", widths);
                float advance = 0;
                for(double width:widths)
                    advance += width;
                //Log.d(TAG,"advance: "+advance);
                path.moveTo(centerX + advance-15, centerY+i+7);
                path.lineTo(centerX + 2 * advance - 15, centerY + i + 7);
                markingValue+=plusValue;
                canvas.drawTextOnPath(message, path, 0f, 0f, readingPaint);
            }
            bigMarking++;
            offPath.moveTo(centerX-10,centerY+i);
            offPath.lineTo(centerX-10,centerY+(i+2));
        }
        offPath.moveTo(centerX-20,centerY+10);
        offPath.lineTo(centerX-20,centerY+212);//addArc(oval, i, 2f);

        canvas.drawPath(offPath, offMarkPaint);
    }

    private void drawScale(Canvas canvas){
        onPath.reset();
        // Log.d(TAG,String.valueOf(mMaxSpeed));
        onPath.moveTo(centerX-10,180-(mCurrentSpeed-16)*2-10);
        onPath.lineTo(centerX-10,180-(mCurrentSpeed-16)*2+10);//addArc(oval, i, 2f);
        Path path = new Path();
        String message = String.format("%d", (int)mCurrentDisplayValue);
        float[] widths = new float["9999".length()];
        readingPaint.getTextWidths("9999", widths);
        float advance = 0;
        for(double width:widths)
            advance += width;
        //Log.d(TAG,"advance: "+advance);
        path.moveTo(centerX -25, 180-(mCurrentSpeed-16)*2+5);
        path.lineTo(centerX + advance -25, 180-(mCurrentSpeed-16)*2+5);

        canvas.drawPath(onPath, onMarkPaint);
        canvas.drawTextOnPath(message, path, 0f, 0f, readingPaint);
    }

    private void drawLegend(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        Path circle = new Path();
        canvas.rotate(-180, centerX,centerY);
        double halfCircumference = radius * Math.PI;
        double increments = 20;
        for(int i = 0; i < this.mMaxSpeed; i += increments){
            circle.addCircle(centerX, centerY, radius, Path.Direction.CW);
            canvas.drawTextOnPath(String.format("%d", i),
                    circle,
                    (float) (i*halfCircumference/this.mMaxSpeed),
                    -30f,
                    scalePaint);
        }

        canvas.restore();
    }

    private void drawReading(Canvas canvas){
        Path path = new Path();
        String message = String.format("%d", (int)this.mCurrentSpeed);
        float[] widths = new float["99".length()];
        readingPaint.getTextWidths("99", widths);
        float advance = 0;
        for(double width:widths)
            advance += width;
        //Log.d(TAG,"advance: "+advance);
        path.moveTo(centerX + advance, centerY*2-6);
        path.lineTo(centerX + 2*advance, centerY*2-6);
        canvas.drawTextOnPath(message, path, 0f, 0f, readingPaint);
    }

    @Override
    public void onSpeedChanged(float newSpeedValue) {
        this.setCurrentSpeed(newSpeedValue);
        this.invalidate();
    }
}