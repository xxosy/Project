package com.example.deok.testapplication;

/**
 * Created by deok on 2015-03-20.
 */
public class DisplayData {
    int engine;
    int generator;
    int battery;
    int motor;

    public DisplayData(){}

    public void setData(int engine, int generator, int battery, int motor){
        this.engine = engine;
        this.generator = generator;
        this.battery = battery;
        this.motor = motor;
    }
}
