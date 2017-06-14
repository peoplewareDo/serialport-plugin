package android_serialport_api;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import hdx.HdxUtil;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.BitmapFactory;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import android.util.Log;

public class SerialPortPrinter extends CordovaPlugin {

    private static final String TAG = "SerialPortPrinterPlugin";
    private SerialPort serialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private StringBuffer mReception = new StringBuffer();
    private ReadThread mReadThread = null;    
    private WakeLock lock;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        
		PowerManager pm = (PowerManager) cordova.getActivity().getApplicationContext().getSystemService(Context.POWER_SERVICE);
		lock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);        

    }
    
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    protected void onDataReceived(final byte[] buffer, final int size) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (mReception != null) {
                        mReception.append(new String(buffer, 0, size));
                    }
                    Log.e(TAG, "onReceived= " + buffer);
                    Log.e(TAG, "onReceived= " + mReception);
                }
        });
    }

    private void sendCommand(OutputStream mOutputStream, int... command) {
		try {
			for (int i = 0; i < command.length; i++) {
				mOutputStream.write(command[i]);
				// Log.e(TAG,"command["+i+"] = "+Integer.toHexString(command[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    private boolean open_con(String message) throws JSONException, SecurityException, IOException, InvalidParameterException {

        try {
                //File file = new File(HdxUtil.GetPrinterPort());
                //serialPort = new SerialPort(file, 115200, 0);
                //mOutputStream = serialPort.getOutputStream();
                //mInputStream  = serialPort.getInputStream();
                //mOutputStream.write(new String(text).getBytes());
                
                //sendCommand(mOutputStream, 0x1b,0x76);
                //Thread.sleep(500);
                mOutputStream.write(message.getBytes("CP1252"));
                //sendCommand(mOutputStream, 0x0a);
                //sendCommand(mOutputStream, 0x1b, 0x4a, 0x180);
                //Thread.sleep(1500);
        //} catch (InterruptedException ex) {                        
        //        ex.printStackTrace();                 
        } catch (IOException ex) {
                ex.printStackTrace();
                return false;
        } finally {
                //serialPort.close();
                //serialPort = null;
                //mOutputStream.close();
        }
        return true;
    }

    @Override
    public boolean execute(String action, final JSONArray data, final CallbackContext callbackContext) throws JSONException {
                
        final String message = data.getString(0);
        final String sleep = data.length() > 1 ? args.getInt(1) : 1000;        
                
        if (action.equals("open")) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    HdxUtil.SwitchSerialFunction(HdxUtil.SERIAL_FUNCTION_PRINTER);
                    HdxUtil.SetPrinterPower(1);
                    //Thread.sleep(500);
                    lock.acquire();
                    try {           
                        File file = new File(HdxUtil.GetPrinterPort());
                        serialPort = new SerialPort(file, 115200, 0);
                        mOutputStream = serialPort.getOutputStream();
                        mInputStream = serialPort.getInputStream();
                        mReadThread = new ReadThread();                        
                        mReadThread.start();                        
                    } catch (IOException ex) {                        
                        ex.printStackTrace();
                        callbackContext.error(1);
                    //} catch (InterruptedException ex) {                        
                    //    ex.printStackTrace(); 
                    //    callbackContext.error(1);
                    //} catch (JSONException ex) {
                    //    ex.printStackTrace(); 
                    //    callbackContext.error(1);                                              
                    }
                    callbackContext.success(); // Thread-safe.
                }
            });     

            return true;
        } else if (action.equals("close")) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    
                    try {
                        Thread.sleep(sleep);
                        serialPort.close();
                        serialPort = null;
                        mOutputStream.close();
                        mInputStream.close();
                        HdxUtil.SetPrinterPower(0);
                        callbackContext.success(); // Thread-safe.

                        if (mReadThread != null) {
                            mReadThread.interrupt();
                        }                        
                    } catch (IOException ex) {                        
                        ex.printStackTrace();
                        callbackContext.error(1);
                    } catch (InterruptedException ex) {                        
                        ex.printStackTrace(); 
                        callbackContext.error(1);
                    } finally {
                        lock.release();
                    }                 
                }
            });     

            return true;

        } else if (action.equals("println")) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    //HdxUtil.SwitchSerialFunction(HdxUtil.SERIAL_FUNCTION_PRINTER);
                    //HdxUtil.SetPrinterPower(1);
                    
                    try {            
                        Thread.sleep(sleep);   
                        open_con(message);
                        //Thread.sleep(1000);   
                    } catch (IOException ex) {                        
                        ex.printStackTrace();
                        callbackContext.error(1);
                    } catch (InterruptedException ex) {                        
                        ex.printStackTrace(); 
                        callbackContext.error(1);
                    } catch (JSONException ex) {
                        ex.printStackTrace();   
                        callbackContext.error(1);                                            
                    } finally {
                        //HdxUtil.SetPrinterPower(0);                        
                    }
                    callbackContext.success(); // Thread-safe.
                }
            });     

            return true;
        } else if (action.equals("sendCommand")) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    //HdxUtil.SwitchSerialFunction(HdxUtil.SERIAL_FUNCTION_PRINTER);
                    //HdxUtil.SetPrinterPower(1);
                    final int len = data.getString(0).length();
                    int[] commands = new int[len];
                    
                    try {     
                        for (int i=0; i<len; i++){ 
                            commands[i] = data.getInt(i);
                        }                                
                        //Thread.sleep(50);   
                        sendCommand(mOutputStream, commands);
                    // } catch (IOException ex) {                        
                    //     ex.printStackTrace();
                    //     callbackContext.error(1);
                    // } catch (InterruptedException ex) {                        
                    //      ex.printStackTrace(); 
                    //      callbackContext.error(1);
                    } catch (JSONException ex) {
                        ex.printStackTrace();   
                        callbackContext.error(1);                                            
                    } finally {
                        //HdxUtil.SetPrinterPower(0);
                    }
                    callbackContext.success(); // Thread-safe.
                }
            });              
             return true;
        } else if (action.equals("printImage")) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                
                Bitmap bitmap = null;//BitmapFactory.decodeResource(this.cordova.getActivity().getResources(), R.drawable.image);
                int startx = 10;

                public void run() {
                    byte[] start2 = { 0x1D, 0x76, 0x30, 0x30, 0x00, 0x00, 0x01, 0x00 };

                    int width = bitmap.getWidth() + startx;
                    int height = bitmap.getHeight();
                    Log.e(TAG,"width:  "+width+" height :"+height);
                    if (width > 384)
                        width = 384;
                    int tmp = (width + 7) / 8;
                    byte[] data_image = new byte[tmp];
                    byte xL = (byte) (tmp % 256);
                    byte xH = (byte) (tmp / 256);
                    start2[4] = xL;
                    start2[5] = xH;
                    start2[6] = (byte) (height % 256);
                    
                    start2[7] = (byte) (height / 256);
                    
                    try {
                        mOutputStream.write(start2);
                    } catch (IOException ex) {                        
                            ex.printStackTrace();
                            callbackContext.error(1);
                    }
                    for (int i = 0; i < height; i++) {

                        for (int x = 0; x < tmp; x++)
                            data_image[x] = 0;
                        for (int x = startx; x < width; x++) {
                            int pixel = bitmap.getPixel(x - startx, i);
                            if (Color.red(pixel) == 0 || Color.green(pixel) == 0
                                    || Color.blue(pixel) == 0) {
                                // 高位在左，所以使用128 右移
                                data_image[x / 8] += 128 >> (x % 8);// (byte) (128 >> (y % 8));
                            }
                        }
                        
                        //while ((printer_status & 0x13) != 0) {
                        //    Log.e(TAG, "printer_status=" + printer_status);
                        try {
                                Thread.sleep(50);
                                mOutputStream.write(data_image);
                        } catch (InterruptedException ex) {
                                ex.printStackTrace();
                                callbackContext.error(1);                            
                        } catch (IOException ex) {                        
                                ex.printStackTrace();
                                callbackContext.error(1);
                        }
                        //}
                        
                        /*
                        * try { Thread.sleep(5); } catch (InterruptedException e) { }
                        */
                    }                    

                }
            });                
            return true;
        } else {
            return false;
        }
    }
}
