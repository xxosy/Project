package com.example.deok.testapplication;

import java.util.Random;

/**
 * Created by idyoung1 on 2015-03-05.
 */
public class FigureThread extends Thread {
    Random random;
    int figure;
    FigureThread(){
        random = new Random();
        figure = 0;
    }
    public void run(){

        while(true) {
            try {
                figure = random.nextInt(100) + 1;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public int getFigure(){
        return figure;
    }
}
