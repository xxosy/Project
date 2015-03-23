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

public class SpeedmeterBar extends View implements SpeedChangeListener {
    private static final String TAG = Speedometer.class.getSimpleName();
    public static final float DEFAULT_MAX_SPEED = 100f; // Assuming this is km/h and you drive a super-car

    // Speedometer internal state
    private float mMaxSpeed;
    private float mCurrentSpeed;

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

    public SpeedmeterBar(Context context){
        super(context);
        Log.d(TAG,"Speedometer(Context) called");
    }

    public SpeedmeterBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG,"Speedometer(Context, AttributeSet) called");
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Speedometer,
                0, 0);
        try{
            mMaxSpeed = 100f;//a.getFloat(R.styleable.Speedometer_maxSpeed, 100f);
            mCurrentSpeed = 50f;//a.getFloat(R.styleable.Speedometer_currentSpeed, 0);
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
        onMarkPaint.setColor(ON_COLOR);
        onMarkPaint.setStrokeWidth(50f);//35f
        onMarkPaint.setShadowLayer(5f, 0f, 0f, ON_COLOR);
        onMarkPaint.setAntiAlias(true);

        offMarkPaint = new Paint(onMarkPaint);
        offMarkPaint.setColor(OFF_COLOR);
        offMarkPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offMarkPaint.setShadowLayer(0f, 0f, 0f, OFF_COLOR);

        scalePaint = new Paint(offMarkPaint);
        scalePaint.setStrokeWidth(2f);
        scalePaint.setTextSize(SCALE_SIZE);
        scalePaint.setShadowLayer(5f, 0f, 0f, Color.RED);
        scalePaint.setColor(SCALE_COLOR);

        readingPaint = new Paint(scalePaint);
        readingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offMarkPaint.setShadowLayer(3f, 0f, 0f, Color.WHITE);
        readingPaint.setTextSize(30f);//65f
        readingPaint.setTypeface(Typeface.SANS_SERIF);
        readingPaint.setColor(Color.WHITE);

        onPath = new Path();
        offPath = new Path();
    }

    public float getCurrentSpeed() {
        return mCurrentSpeed;
    }

    public void setCurrentSpeed(float mCurrentSpeed) {
        if(mCurrentSpeed > this.mMaxSpeed)
            this.mCurrentSpeed = mMaxSpeed;
        else if(mCurrentSpeed < 0)
            this.mCurrentSpeed = 0;
        else
            this.mCurrentSpeed = mCurrentSpeed;

        if(mCurrentSpeed>80){
            onMarkPaint.setColor(WARING_COLOR);
            readingPaint.setColor(WARING_COLOR);
        }else if (mCurrentSpeed>60){
            onMarkPaint.setColor(ON_COLOR);
            readingPaint.setColor(ON_COLOR);
        }else{
            onMarkPaint.setColor(Color.GREEN);
            readingPaint.setColor(Color.GREEN);
        }
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
        int widthSize = MeasureSpec.getSize(widthMeasureSpec)/2;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec)/2;

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);
        centerX = chosenDimension / 2;
        centerY = chosenDimension / 2;
        setMeasuredDimension(chosenDimension, chosenDimension);
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
        canvas.drawARGB(255, 0, 0, 0);
        //Log.d(TAG,"drawScaleBackground");
        offPath.reset();
        offPath.moveTo(centerX,centerY*2);
        for(int i = 0; i < mMaxSpeed*2; i+=12){//4
            offPath.lineTo(centerX,centerY*2-i);//addArc(oval, i, 2f);
            offPath.moveTo(centerX,centerY*2-(i+6));
        }
        canvas.drawPath(offPath, offMarkPaint);
    }

    private void drawScale(Canvas canvas){
        onPath.reset();
       // Log.d(TAG,String.valueOf(mMaxSpeed));
        onPath.moveTo(centerX,centerY*2);
        for(int i = 0; i < (mCurrentSpeed/mMaxSpeed)*200; i+=12){//4
            onPath.lineTo(centerX,centerY*2-i);//addArc(oval, i, 2f);
            onPath.moveTo(centerX,centerY*2-(i+6));
        }
        canvas.drawPath(onPath, onMarkPaint);
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