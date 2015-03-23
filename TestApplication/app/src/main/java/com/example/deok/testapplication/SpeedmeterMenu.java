package com.example.deok.testapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;



/**
 * Created by YoungWon on 2015-03-17.
 */
public class SpeedmeterMenu extends Dialog implements View.OnClickListener {
    Button btnConnect;
    Button btnConnectFree;
    Button btnBatteryMode;
    Button btnMotorMode;
    Handler handler;

    public SpeedmeterMenu(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.speedmeter_menu_dialog);
        btnConnect = (Button)findViewById(R.id.button_connect);
        btnConnect.setOnClickListener(this);
        btnConnectFree = (Button)findViewById(R.id.button_free);
        btnConnectFree.setOnClickListener(this);
        btnBatteryMode = (Button)findViewById(R.id.button_battery);
        btnBatteryMode.setOnClickListener(this);
        btnMotorMode = (Button)findViewById(R.id.button_motor);
        btnMotorMode.setOnClickListener(this);

    }
    public void setHandler(Handler handler){
        this.handler = handler;
    }
    @Override
    public void onClick(View v) {
        if(v == btnConnect){
            Message msg = handler.obtainMessage();
            msg.arg1 = 1;
            handler.sendMessage(msg);
            dismiss();
        }else if(v == btnConnectFree){
            Message msg = handler.obtainMessage();
            msg.arg1 = 2;
            handler.sendMessage(msg);
            dismiss();
        }else if(v == btnBatteryMode){
            Message msg = handler.obtainMessage();
            msg.arg1 = 3;
            handler.sendMessage(msg);
            dismiss();
        }else if(v == btnMotorMode) {
            Message msg = handler.obtainMessage();
            msg.arg1 = 4;
            handler.sendMessage(msg);
            dismiss();
        }
    }
}
