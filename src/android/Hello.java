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

public class Hello extends CordovaPlugin {

    private SerialPort serialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;

    private void sendCommand(OutputStream mOutputStream, int... command) {
		try {
			for (int i = 0; i < command.length; i++) {
				mOutputStream.write(command[i]);
				// Log.e(TAG,"command["+i+"] = "+Integer.toHexString(command[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// / sleep(1);
	}
    
    public boolean open_con(String message) throws JSONException, SecurityException, IOException, InvalidParameterException {

        try {
                File file = new File (HdxUtil.GetPrinterPort());
                serialPort = new SerialPort(file, 115200, 0);
                mOutputStream = serialPort.getOutputStream();
                //mInputStream  = serialPort.getInputStream();
                //mOutputStream.write(new String(text).getBytes());
                
                sendCommand(mOutputStream, 0x1b,0x76);
                Thread.sleep(500);
                mOutputStream.write(new String(message).getBytes());
                sendCommand(0x0a);
                
        } catch (InterruptedException ex) {                        
            ex.printStackTrace();                 
        } catch (IOException ex) {
                ex.printStackTrace();
                return false;
        } finally {
                serialPort.close();
                serialPort = null;
                mOutputStream.close();
        }
        return true;
    }

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
                
        final String message = data.getString(0);
        
        if (action.equals("greet")) {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    HdxUtil.SwitchSerialFunction(HdxUtil.SERIAL_FUNCTION_PRINTER);
                    HdxUtil.SetPrinterPower(1);
                    
                    try {            
                        Thread.sleep(500);   
                        open_con(message);
                    } catch (IOException ex) {                        
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {                        
                        ex.printStackTrace(); 
                    } catch (JSONException ex) {
                        ex.printStackTrace();                                               
                    } finally {
                        HdxUtil.SetPrinterPower(0);
                    }
                    callbackContext.success(); // Thread-safe.
                }
            });            

            return true;
        } else {
            return false;
        }
    }
}
