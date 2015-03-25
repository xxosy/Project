package com.example.deok.testapplication;

import java.util.ArrayList;

class DataSet{
    char flag;
    char type;
    ArrayList<Integer> data;
    char finish;
    String msgStr;

    public void setData(char flag, char type, ArrayList<Integer> data, char finish,String msgStr){
        this.flag = flag;
        this.type = type;
        this.data = data;
        this.finish = finish;
        this.msgStr = msgStr;
    }
}