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

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

public class Hello extends CordovaPlugin {

    private SerialPort serialPort = null;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        File file = new File ("/dev/","ttyS");

        try {
            serialPort = new SerialPort(file, 115200, 2);
            mOutputStream = serialPort.getOutputStream();
            mInputStream  = serialPort.getInputStream();
            //mOutputStream.write(new String("Teste").getBytes());
            mOutputStream.write('\n');

        } catch (IOException ex) {
          ex.printStackTrace();
        }

        if (action.equals("greet")) {

            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);

            return true;

        } else {

            return false;

        }
    }
}
