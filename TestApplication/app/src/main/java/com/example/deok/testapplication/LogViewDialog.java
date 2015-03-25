package com.example.deok.testapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * Created by YoungWon on 2015-03-17.
 */
public class LogViewDialog extends Dialog {
    ListView listView;
    Handler handler;
    String[] data;
    Context context;
    public LogViewDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.log_data_view);
        listView = (ListView)findViewById(R.id.log_data_list);
        this.context = context;

    }
    public void setData(int size, ArrayList<String> str){
        data = new String[size];
        for(int i = 0;i<size;i++){
            data[i] = str.get(i);
            ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, data);
            listView.setAdapter(adapter);
        }
    }
    public void setHandler(Handler handler){
        this.handler = handler;
    }

}