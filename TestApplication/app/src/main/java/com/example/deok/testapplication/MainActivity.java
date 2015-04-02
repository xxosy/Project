package com.example.deok.testapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    private ListViewAdapter logListAdapter = null;
    private TextView tvEngine;
    private TextView tvGenerator;
    private TextView tvBattery;
    private TextView tvMotor;

    FigureBar figureBar_Command;
    FigureBar figureBar_Engne;
    FigureBar figureBar_Generation;
    FigureBar figureBar_Charge;
    FigureBar figureBar_Discharge;
    FigureBar figureBar_Energy;
    FigureBar[] mFigureBar;
    FileOutputStream fos;
    FileInputStream fis;
    File file;
    String path;
    String fileName;
    File saveFile;
    private ActiveSet activeSet;
    ListView listView;
    FrameLayout frame;
    int count;

    //////////////////////////////////////////////////////////////////////////////////
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = "Main";
    private TextView txt_Result;

    private BluetoothService btService = null;
    @Override
    protected  void onStop(){
        super.onStop();
        btService.stop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate");
        FigureLayout engineStructLayout = new FigureLayout(this);
        SpeedmeterLayout speedmeterLayout = new SpeedmeterLayout(this);
        speedmeterLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        engineStructLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        RelativeLayout relative = (RelativeLayout)findViewById(R.id.relativelayout);
        RelativeLayout linearlayout = (RelativeLayout)findViewById(R.id.linearlayout1);
        relative.addView(speedmeterLayout);
        linearlayout.addView(engineStructLayout);

        RelativeLayout clickableArea= (RelativeLayout)findViewById(R.id.clickablearea);
///////////////////////////////////////////////////////////////////////////////
        path = getFilesDir().getAbsolutePath();
        fileName = "log_data.txt";
        file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        saveFile = new File(path+"/test.txt");
        fos = null;
        fis = null;
        ////////////////////////////////////////////////////////////////////////
        tvEngine = (TextView)findViewById(R.id.engine);
        tvGenerator = (TextView)findViewById(R.id.generator);
        tvBattery = (TextView)findViewById(R.id.battery);
        tvMotor = (TextView)findViewById(R.id.motor);
        activeSet = (ActiveSet)findViewById(R.id.activeset);

        //final SpeedmeterPackage speedmeterPackage = (SpeedmeterPackage)findViewById(R.id.SpeedometerPackage);
        final Context context = this.getApplicationContext();
        final Handler menuHandler= new Handler() {

            public void handleMessage(Message msg) {
                if(msg.arg1 == 1){
                    if(btService.getDeviceState()) {

                        btService.enableBluetooth();
                    } else {
                        finish();
                    }
                }else if(msg.arg1 == 2){
                    btService.stop();
                    Toast.makeText(MainActivity.this,"블루투스연결이 해제되었습니다.",Toast.LENGTH_SHORT).show();
                }else if(msg.arg1 == 3){
                    //speedmeterPackage.onModeChanged(true);
                }else if(msg.arg1 == 4) {
                    try {
                        String loadPath = path+"/test.txt";
                        fis = new FileInputStream(loadPath);
                        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis));
                        String content="", temp="";
                        int count = 0;
                        ArrayList<String> str = new ArrayList<String>();
                        LogViewDialog lvDialog = new LogViewDialog(MainActivity.this);
                        while( (temp = bufferReader.readLine()) != null ) {
                            str.add(temp);
                        }
                        lvDialog.setData(str.size(), str);
                        lvDialog.show();
                        Window window = lvDialog.getWindow();
                        WindowManager.LayoutParams wlp = window.getAttributes();
                        wlp.gravity = Gravity.LEFT;
                        window.setAttributes(wlp);
                        Toast.makeText(MainActivity.this,content,Toast.LENGTH_LONG).show();
                        fis.close();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(MainActivity.this, "Is Not LogFile",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        clickableArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeedmeterMenu dialog = new SpeedmeterMenu(MainActivity.this);
                dialog.setHandler(menuHandler);
                dialog.show();
            }
        });


        txt_Result = (TextView) findViewById(R.id.txt_result);
        //
   //     btn_Connect.setOnClickListener(this);
        final Random random = new Random();
        final Handler handler = new Handler() {
            char figure = 0;
            char figure2 =0;

            ByteBuffer mByteBuffer= ByteBuffer.allocate(1024);
            public void handleMessage(Message msg) {
                if(msg.arg1==1){
                    Toast.makeText(MainActivity.this,"연결에 실패하였습니다. 다시 시도하십시오.",Toast.LENGTH_SHORT).show();
                }
                if(msg.arg1 == -1){
                    Toast.makeText(MainActivity.this,"블루투스가 연결 되었습니다.",Toast.LENGTH_SHORT).show();
                }
                DataSet mDataSet;
                byte[] buffer = new byte[1];
                buffer[0]='Y';
                mDataSet = (DataSet)msg.obj;
                if(mDataSet!=null) {
                    figure = mDataSet.flag;
                    if (figure == 'S') {
                        figure2 = mDataSet.type;
                        if (figure2 == 'G') {
                            for (int i = 0; i < 5; i++)
                                readData(i,mDataSet.data.get(i));
                            mFigureBar[5].onSpeedChanged(mDataSet.data.get(2)-mDataSet.data.get(3)+mDataSet.data.get(4));

                        }else if(figure2 == 'L'){
                            activeSet.onStateChanged(mDataSet.data);

                        }else if(figure2 == 'D'){
                            float temp = (float)mDataSet.data.get(0)/1024*102;
                            float temp1= (float)mDataSet.data.get(1)/1024*102;
                            float temp2= (float)mDataSet.data.get(2)/1024*400;
                            float temp3= (float)mDataSet.data.get(3)/1024*102;
                            tvEngine.setText(String.valueOf((int)temp));
                            tvGenerator.setText(String.valueOf((int)temp1));
                            tvBattery.setText(String.valueOf((int)temp2));
                            tvMotor.setText(String.valueOf((int)temp3));

                        }else if(figure2 == 'A'){
                            try {
                                long now = System.currentTimeMillis();
                                Date date = new Date(now);
                                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                String strNow = sdfNow.format(date);
                            logListAdapter.addItem(mDataSet.msgStr);
                                fos = new FileOutputStream(saveFile,true);
                                String outStr=mDataSet.msgStr+" "+strNow+"\n";
                                fos.write(outStr.getBytes());
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mListView.setSelection(mAdapter.getCount()-1);

                        }else if(figure2 == 'M'){
                            mAdapter.addItem(mDataSet.msgStr);
                            listView.setSelection(mAdapter.getCount()-1);
                            logListAdapter.addItem("Warning :"+mDataSet.msgStr);
                            mListView.setSelection(mAdapter.getCount()-1);
                        }
                        btService.write(buffer);
                    }
                }
            }
        };

        if(btService == null) {
            btService = new BluetoothService(this, handler);//mHandler);
        }
        //////////////////////////////////////////////////////////////////////////////////////
        mFigureBar = new FigureBar[6];
        figureBar_Command = (FigureBar) findViewById(R.id.Speedometer);
        figureBar_Engne = (FigureBar) findViewById(R.id.Speedometer1);
        figureBar_Generation = (FigureBar)findViewById(R.id.Speedometer2);
        figureBar_Charge = (FigureBar)findViewById(R.id.Speedometer3);
        figureBar_Discharge = (FigureBar)findViewById(R.id.Speedometer4);
        figureBar_Energy = (FigureBar)findViewById(R.id.Speedometer6);
        mFigureBar[0] = figureBar_Command;
        mFigureBar[1] = figureBar_Engne;
        mFigureBar[2] = figureBar_Generation;
        mFigureBar[3] = figureBar_Charge;
        mFigureBar[4] = figureBar_Discharge;
        mFigureBar[5] = figureBar_Energy;

        listView = (ListView) findViewById(R.id.listView);
        mListView =(ListView) findViewById(R.id.loglist);
        mAdapter = new ListViewAdapter(this,0);
        logListAdapter = new ListViewAdapter(this,1);
        listView.setAdapter(mAdapter);
        mListView.setAdapter(logListAdapter);


        logListAdapter.addItem("System log data display");
        mAdapter.addItem("User information blank message");

        listView.setSelection(mAdapter.getCount()-1);
    }
    private class ViewHolder {
        public TextView mText;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();
        private int kind_of_adapter;
        public ListViewAdapter(Context mContext, int kind) {
            super();
            this.kind_of_adapter = kind;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.warning_message_window, null);
                holder.mText = (TextView) convertView.findViewById(R.id.list_item);
                if(kind_of_adapter == 0){}

                else if(kind_of_adapter == 1) {
                    holder.mText.setTextColor(Color.BLACK);
                    holder.mText.setTextSize(20f);
                }
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            ListData mData = mListData.get(position);
            holder.mText.setText(mData.mTitle);
            return convertView;
        }
        public void addItem(String mTitle){
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mTitle = mTitle;


            mListData.add(addInfo);
            dataChange();
        }
        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }
        public void sort(){
            Collections.sort(mListData, ListData.ALPHA_COMPARATOR);
            dataChange();
        }
        public void dataChange(){
            mAdapter.notifyDataSetChanged();
            logListAdapter.notifyDataSetChanged();
        }
    }


    public void readData(int i,int x){
        mFigureBar[i].onSpeedChanged(x);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////


    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    };


    @Override
    public void onClick(View v) {
        if(btService.getDeviceState()) {

            btService.enableBluetooth();
        } else {
            finish();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {

            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    btService.getDeviceInfo(data);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    btService.scanDevice();
                } else {

                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////
}

