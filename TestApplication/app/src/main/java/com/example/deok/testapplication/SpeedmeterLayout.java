package com.example.deok.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * Created by YoungWon on 2015-03-16.
 */
public class SpeedmeterLayout extends RelativeLayout{
    public SpeedmeterLayout(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate( R.layout.speedmeter, null );
        addView(relativeLayout);

    }
    @Override
    public void onDraw(Canvas canvas){

    }

    public SpeedmeterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
}
