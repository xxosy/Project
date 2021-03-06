package com.example.deok.testapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothService {
	// Debugging
	private static final String TAG = "BluetoothService";

	// Intent request code
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// RFCOMM Protocol
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private BluetoothAdapter btAdapter;

	private Activity mActivity;
	private Handler mHandler;

	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;

	private int mState;

	private static final int STATE_NONE = 0;
	private static final int STATE_LISTEN = 1;
	private static final int STATE_CONNECTING = 2;
	private static final int STATE_CONNECTED = 3;

	// Constructors
	public BluetoothService(Activity ac, Handler h) {
		mActivity = ac;
		mHandler = h;

		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	public boolean getDeviceState() {
		Log.i(TAG, "Check the Bluetooth support");

		if (btAdapter == null) {
			Log.d(TAG, "Bluetooth is not available");

			return false;

		} else {
			Log.d(TAG, "Bluetooth is available");

			return true;
		}
	}


	public void enableBluetooth() {
		Log.i(TAG, "Check the enabled Bluetooth");

		if (btAdapter.isEnabled()) {
			Log.d(TAG, "Bluetooth Enable Now");


			scanDevice();
		} else {

			Log.d(TAG, "Bluetooth Enable Request");

			Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
		}
	}


	public void scanDevice() {
		Log.d(TAG, "Scan Device");

		Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
		mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	public void getDeviceInfo(Intent data) {

		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);

		BluetoothDevice device = btAdapter.getRemoteDevice(address);

		Log.d(TAG, "Get Device Info \n" + "address : " + address);

		connect(device);
	}

	private synchronized void setState(int state) {
		Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;
	}
	public synchronized int getState() {
		return mState;
	}

	public synchronized void start() {
		Log.d(TAG, "start");

		if (mConnectThread == null) {

		} else {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread == null) {

		} else {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
	}

	public synchronized void connect(BluetoothDevice device) {
		Log.d(TAG, "connect to: " + device);

		if (mState == STATE_CONNECTING) {
			if (mConnectThread == null) {

			} else {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		if (mConnectedThread == null) {

		} else {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		mConnectThread = new ConnectThread(device);

		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		Log.d(TAG, "connected");

		if (mConnectThread == null) {

		} else {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread == null) {

		} else {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		setState(STATE_CONNECTED);
        Message msg = mHandler.obtainMessage();
        msg.arg1 = -1;
        mHandler.sendMessage(msg);
	}

	public synchronized void stop() {
		Log.d(TAG, "stop");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_NONE);
	}

	public void write(byte[] out) {
		ConnectedThread r;
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
            r.writeReturn(out);
		}
	}

	private void connectionFailed() {
        Message msg = mHandler.obtainMessage();
        msg.arg1 = 1;
        mHandler.sendMessage(msg);
        setState(STATE_LISTEN);
	}


	private void connectionLost() {
		setState(STATE_LISTEN);
        Message msg = mHandler.obtainMessage();
        msg.arg1 = 1;
        mHandler.sendMessage(msg);
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			/*
			 * / // Get a BluetoothSocket to connect with the given
			 * BluetoothDevice try { // MY_UUID is the app's UUID string, also
			 * used by the server // code tmp =
			 * device.createRfcommSocketToServiceRecord(MY_UUID);
			 * 
			 * try { Method m = device.getClass().getMethod(
			 * "createInsecureRfcommSocket", new Class[] { int.class }); try {
			 * tmp = (BluetoothSocket) m.invoke(device, 15); } catch
			 * (IllegalArgumentException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } catch (IllegalAccessException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } catch
			 * (InvocationTargetException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }
			 * 
			 * } catch (NoSuchMethodException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); } } catch (IOException e) { } /
			 */

			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			btAdapter.cancelDiscovery();
			try {

				mmSocket.connect();
				Log.d(TAG, "Connect Success");

			} catch (IOException e) {
				connectionFailed();
				Log.d(TAG, "Connect Fail");


				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG,
							"unable to close() socket during connection failure",
							e2);
				}

				BluetoothService.this.start();
				return;
			}

			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}

			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

            ByteBuffer mmByteBuffer = ByteBuffer.allocate(1024);
			while (true) {
				try {
                    Log.i(TAG,"START");
                    bytes = mmInStream.read(buffer);
                    DataSet mDataSet = new DataSet();
                    for(int s = 0;s<30;s++) {

                    }

                    if(buffer[bytes-1] !='#') {
                        mmByteBuffer.put(buffer, 0, bytes);
                    }
                    else{
                        ArrayList<Integer> data = new ArrayList<Integer>();
                        mmByteBuffer.put(buffer,0,bytes);

                        Message msg = mHandler.obtainMessage();
                        String msgWarning="No String";
                        if((char)mmByteBuffer.get(1)=='G') {
                            int result = 0;
                            for (int i = 2; (char) mmByteBuffer.get(i) != '#'; i++) {

                                if (i % 4 == 2)
                                    result += Integer.valueOf(String.valueOf((char) mmByteBuffer.get(i))) * 1000;
                                else if (i % 4 == 3)
                                    result += Integer.valueOf(String.valueOf((char) mmByteBuffer.get(i))) * 100;
                                else if (i % 4 == 0)
                                    result += Integer.valueOf(String.valueOf((char) mmByteBuffer.get(i))) * 10;
                                else if (i % 4 == 1) {
                                    result += Integer.valueOf(String.valueOf((char) mmByteBuffer.get(i)));
                                    data.add(result);

                                    result = 0;
                                }
                            }
                        }else if((char)mmByteBuffer.get(1)=='L'){
                            for(int i =2;(char)mmByteBuffer.get(i)!='#';i++){
                                data.add(Integer.valueOf(String.valueOf((char) mmByteBuffer.get(i))));
                            }
                        }else if((char)mmByteBuffer.get(1)=='D'){
                            int result = 0;
                            for (int i = 2; (char) mmByteBuffer.get(i) != '#'; i++) {

                                if (i % 4 == 2)
                                    result += Integer.valueOf(String.valueOf((char) mmByteBuffer.get(i))) * 1000;
                                else if (i % 4 == 3)
                                    result += Integer.valueOf(String.valueOf((char) mmByteBuffer.get(i))) * 100;
                                else if (i % 4 == 0)
                                    result += Integer.valueOf(String.valueOf((char) mmByteBuffer.get(i))) * 10;
                                else if (i % 4 == 1) {
                                    result += Integer.valueOf(String.valueOf((char) mmByteBuffer.get(i)));
                                    data.add(result);

                                    result = 0;
                                }
                            }
                        }else if((char)mmByteBuffer.get(1)=='M'){

                            byte[] str = new byte[mmByteBuffer.position()-2];
                            for(int i =2;mmByteBuffer.get(i) != 35; i++) {
                                str[i-2]= mmByteBuffer.get(i);
                            }
                            msgWarning = new String(str);
                        }else if((char)mmByteBuffer.get(1)=='A'){

                            byte[] str = new byte[mmByteBuffer.position()-2];
                            for(int i =2;mmByteBuffer.get(i) != 35; i++) {
                                str[i-2]= mmByteBuffer.get(i);
                            }
                            msgWarning = new String(str);
                        }
                        Log.d(TAG,String.valueOf((char)mmByteBuffer.get(1)));
                        Log.d(TAG,msgWarning);
                        mDataSet.setData((char)mmByteBuffer.get(0),(char)mmByteBuffer.get(1),data, (char)mmByteBuffer.get(23),msgWarning);
                        msg.obj = mDataSet;
                        mHandler.sendMessage(msg);
                        mmByteBuffer.clear();
                    }
//                  Message msg = mHandler.obtainMessage();
 //                 mHandler.sendMessage(msg);

				} catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                  break;
               }
            }
		}

		public void writeReturn(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
               } catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

}