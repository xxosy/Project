package com.example.youngwon.electronictester.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.youngwon.electronictester.R;

import java.util.ArrayList;

public class LineChart extends View implements LineChartValueChangeListener {
    private static final String TAG = LineChart.class.getSimpleName();
    private ArrayList<Integer> dataArray;
    private int view_width;
    private int view_height;
    private Paint onMarkPaint;
    private Paint offMarkPaint;
    private Paint scalePaint;
    private Paint readingPaint;
    private Path onPath;
    private Path offPath;
    private int timeGab;

    // Drawing colors
    private int LINE_COLOR = Color.argb(255, 0xff, 0xA5, 0x00);
    private int WARING_COLOR = Color.argb(255,0xff,0x00,0x00);
    private int OFF_COLOR = Color.argb(255,0xff,0x00,0x00);
    private int SCALE_COLOR = Color.argb(255, 255, 255, 255);
    private float SCALE_SIZE = 10f;
    private float READING_SIZE = 60f;

    // Scale configuration
    private float centerX;
    private float centerY;

    public LineChart(Context context){
        super(context);
        Log.d(TAG,"Speedometer(Context) called");
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        dataArray = new ArrayList<Integer>();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Speedometer,
                0, 0);
        try{
            LINE_COLOR = a.getColor(R.styleable.Speedometer_onColor, LINE_COLOR);
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
        onMarkPaint.setColor(LINE_COLOR);
        onMarkPaint.setStrokeWidth(1f);//35f

        onMarkPaint.setAntiAlias(true);

        offMarkPaint = new Paint(onMarkPaint);
        offMarkPaint.setColor(OFF_COLOR);
        offMarkPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        scalePaint = new Paint(offMarkPaint);
        scalePaint.setStrokeWidth(1f);
        scalePaint.setTextSize(SCALE_SIZE);
        scalePaint.setShadowLayer(5f, 0f, 0f, Color.RED);
        scalePaint.setColor(SCALE_COLOR);

        readingPaint = new Paint(scalePaint);
        readingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        readingPaint.setTextSize(35f);//65f
        readingPaint.setTypeface(Typeface.SANS_SERIF);
        readingPaint.setColor(Color.BLACK);

        onPath = new Path();
        offPath = new Path();
    }

    public float getCurrentSpeed() {
        return 0;
    }

    public void setCurrentSpeed(int mCurrentSpeed) {
        if(dataArray.size()>100){
            dataArray.remove(0);
        }
        dataArray.add(mCurrentSpeed);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        Log.d(TAG, "Size changed to " + width + "x" + height);
        view_width = width;
        view_height = height;
        timeGab = view_width/100;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

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
        drawLineBackground(canvas);
        drawLine(canvas);
        drawReading(canvas);
    }

    private void drawLineBackground(Canvas canvas){
        canvas.drawARGB(0, 0, 0, 0);
        // Log.d(TAG,"drawScaleBackground");
        offPath.reset();

        offPath.moveTo(0,centerY);
        offPath.lineTo(view_width,centerY);
        offPath.moveTo(timeGab*10,0);
        offPath.lineTo(timeGab*10,2*centerY);

        canvas.drawPath(offPath, offMarkPaint);
    }

    private void drawLine(Canvas canvas){

        offPath.reset();
        offPath.moveTo(10*timeGab,centerY);

        for(int i =0;i<dataArray.size();i++){
            offPath.lineTo((i+10)*timeGab,centerY-dataArray.get(i));
            offPath.moveTo((i+10)*timeGab,centerY-dataArray.get(i));
        }
        canvas.drawPath(offPath, onMarkPaint);
    }

    private void drawReading(Canvas canvas){

        String message = String.format("%d", 0);
        for(int i=0;i<2*centerY;i+=100){
            Path path = new Path();
            message = String.format("%d", 500-i);
            float[] widths = new float[message.length()];
            readingPaint.getTextWidths(message, widths);
            float advance = 0;
            for(double width:widths)
                advance += width;
            // Log.d(TAG,"advance: "+advance);
            path.moveTo(5*timeGab - advance/2, i);
            path.lineTo(5*timeGab + advance/2, i);
            canvas.drawTextOnPath(message, path, 0f, 0f, readingPaint);
        }


    }

    @Override
    public void onValueChanged(int newSpeedValue) {
        this.setCurrentSpeed(newSpeedValue);
        this.invalidate();
    }

}