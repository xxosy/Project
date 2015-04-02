package com.example.deok.testapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by YoungWon on 2015-03-16.
 */
public class FigureLayout extends RelativeLayout {
    public FigureLayout(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate( R.layout.enginestruct, null );
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(relativeLayout);
    }
    public FigureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
}
