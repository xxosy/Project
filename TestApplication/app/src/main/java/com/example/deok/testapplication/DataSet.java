package com.example.deok.testapplication;

class DataSet{
    char flag;
    char type;
    int[] data;
    char finish;

    public void setData(char flag, char type, int[] data, char finish){
        this.flag = flag;
        this.type = type;
        this.data = data;
        this.finish = finish;
    }
}