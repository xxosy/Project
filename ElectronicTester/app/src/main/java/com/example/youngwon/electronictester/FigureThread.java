package com.example.youngwon.electronictester;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Random;

/**
 * Created by idyoung1 on 2015-03-05.
 */
public class FigureThread extends Thread {
    Random random;
    int figure;
    Handler mHandler;
    FigureThread(Handler mHandler){
        random = new Random();
        figure = 0;
        this.mHandler = mHandler;
    }
    public void run(){

        while(true) {
            try {
                figure = random.nextInt(300) -150;
                Thread.sleep(50);
                Log.i("test", String.valueOf(figure));
                Message msg = mHandler.obtainMessage();
                msg.arg1 = figure;
                mHandler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public int getFigure(){
        return figure;
    }
}
