package com.example.print_sdk;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import com.lcserial.www.SerialPort;
import com.lcserial.www.SerialPortTool;

/**
 * Created by moxiaomo
 * on 2020/4/1
 */
public class SerialManager {

    private final static String TAG="SerialManager";

//    public static final String PATH="/dev/ttyS2";
    public static final String PATH="/dev/ttyS0";
//    public static final int BAUTRATE=115200;
    public static final int BAUTRATE=230400;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    protected SerialPortTool serialPortTool;
    protected SerialPort mSerialPort;

    private ReadThread mReadThread;
    private boolean isStop=false;
    private OnDataReceiveListener onDataReceiveListener=null;

    private static final String DEV_PRINTER_BOOT = "/sys/printer_pin/printer/boot";
    private static final String DEV_PRINTER_PWR = "/sys/printer_pin/printer/pwr_en";
    private File mPrinterBoot = new File(DEV_PRINTER_BOOT);
    private File mPrinterPwr = new File(DEV_PRINTER_PWR);

    private enum Singleton {
        INSTANCE;

        private final SerialManager client;

        Singleton() {
            client=new SerialManager ();
        }

        private SerialManager getInstance() {
            return client;
        }
    }

    public static SerialManager getClient() {

        return Singleton.INSTANCE.getInstance ();
    }

    public interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener=dataReceiveListener;
    }

    /**
     * Name：open Function:open printer
     *
     * @return true：sucessful；false：failed
     */
    public boolean open() {
        if (!poweron ())
            return false;
        boolean bRet=true;
        if (serialPortTool == null)
            serialPortTool=new SerialPortTool ();
        try {
            mSerialPort=serialPortTool.getSerialPort (PATH, BAUTRATE);
            mOutputStream=mSerialPort.getOutputStream ();

            Log.d("stvel", "open----mOutputStream: " + mOutputStream);
            mInputStream=mSerialPort.getInputStream ();
            bRet=true;
        } catch (SecurityException e) {

        } catch (IOException e) {

        } catch (InvalidParameterException e) {

        } catch (Exception e) {

        }
        return bRet;
    }

    public void startReadThread(){
        mReadThread=new ReadThread ();
        isStop=false;
        mReadThread.start ();
    }

    /**
     * Name：close; Function:close printer
     */
    public void close() {
        isStop=true;
        if (mReadThread != null) {
            mReadThread.interrupt ();
        }
        if (serialPortTool != null)
            serialPortTool.closeSerialPort ();
        mSerialPort=null;
        poweroff ();
    }

    /***
     * Name：getStatus，get printer status
     *
     * @return 0:normal ；-1： unknown state；-2：No paper；-3：Printer overheating
     */
    public int getStatus() {
        Log.d("stvel", "getStatus: ");
        int iRet=0;
//        byte[] cmd={0x1D, 0x61, 0x22};
        byte[] cmd={0x1D, 0x61, 0x26}; //stvelzhang show print allstatus 58mm
        byte[] buffer=new byte[10];
        try {
            mOutputStream.write (cmd);
            Thread.sleep (10);
            int loop=10;
            while (loop-- > 0) {
                int readBytes=0;
                if (mInputStream.available () > 0) {
                    Thread.sleep (10);
                    readBytes=mInputStream.read (buffer);
                }
                if (buffer[0] == 0x73) {
                    continue;
                }
                if (readBytes > 0) {
//                    if (buffer[2] == 0x0C) {
                    if (buffer[6] == 0x04) { //stvelzhang
                        iRet=-2;
                    }
                    if (buffer[1] == 0x40) {
                        iRet=-3;
                    }
                    break;
                }
                Thread.sleep (5);
            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            Log.e (TAG, "getStatus: ");
        } catch (IOException e) {
            e.printStackTrace ();
            Log.e (TAG, "getStatus: ");
        }
        Log.e (TAG, "onDataReceive: " + Bytes2HexString (buffer));
        Log.e("stvel", "getStatus---onDataReceive: " + Bytes2HexString (buffer));
        return iRet;
    }

/*
*stvelzhang
 */
    public int getPrintStatus() {
        Log.d("stvel", "SerialManager ----getPrintStatus: ");
        int iRet=0;
//        byte[] cmd={0x1D, 0x61, 0x22};
//        byte[] cmd={0x1D, 0x72, 0x01}; //stvelzhang show print allstatus 58mm
        byte[] cmd={0x10, 0x04, 0x04}; //stvelzhang show print allstatus 58mm
        byte[] buffer=new byte[10];
        try {
            mOutputStream.write (cmd);
            Thread.sleep (10);
            int loop=10;
            while (loop-- > 0) {
                int readBytes=0;
                if (mInputStream.available () > 0) {
                    Thread.sleep (10);
                    readBytes=mInputStream.read (buffer);
                }
                if (buffer[0] == 0x73) {
                    continue;
                }
                if (readBytes > 0) {
                    if (buffer[2] == 0x0C) {
                        iRet=-2;
                    }
                    if (buffer[1] == 0x40) {
                        iRet=-3;
                    }
                    break;
                }
                Thread.sleep (5);
            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            Log.e (TAG, "getStatus: ");
        } catch (IOException e) {
            e.printStackTrace ();
            Log.e (TAG, "getStatus: ");
        }
        Log.e (TAG, "onDataReceive: " + Bytes2HexString (buffer));
        Log.e("stvel", "getPrintStatus---onDataReceive: " + Bytes2HexString (buffer));
        return iRet;
    }



    public static String Bytes2HexString(byte[] b) {
        String ret="";

        for (int i=0; i < b.length; ++i) {
            String hex=Integer.toHexString (b[i] & 255);
            if (hex.length () == 1) {
                hex='0' + hex;
            }

            ret=ret + " " + hex.toUpperCase ();
        }
        return ret;
    }

    /**
     * Name：escCommand
     * Function:send ESC command
     *
     * @param cmd
     * @return
     */
    public boolean escCommand(String cmd) {
        if (cmd == null || cmd.length () == 0)
            return false;

        byte[] buffer=cmd.getBytes ();
        return escCommand (buffer);
    }

    public boolean escCommand(byte[] cmd) {
        Log.e (TAG, "escCommand: " + Bytes2HexString (cmd));
        boolean bRet=true;
        try {
            if (cmd != null && cmd.length > 0) {
                mOutputStream.write (cmd);
            }
        } catch (Exception ex) {
            bRet=false;
        }
        return bRet;
    }

    public boolean escCommand(byte[] cmd, int count) {
        boolean bRet=true;
        try {
            if (cmd != null && cmd.length > 0) {
                mOutputStream.write (cmd, 0, count);
            }
        } catch (Exception ex) {
            bRet=false;
            Log.e ("escCommand", ex.getLocalizedMessage ());
        }
        return bRet;
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

    public OutputStream getOutputStream() {
        Log.d("stvel", "getOutputStream----mOutputStream: " + mOutputStream);
        return mOutputStream;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run ();
            while (!isStop && !isInterrupted ()) {
                int size;
                try {
                    if (mInputStream == null)
                        return;
                    int ret=mInputStream.available ();
                    Thread.sleep (15);
                    byte[] buffer=new byte[ret];
                    if (ret > 0) {
                        size=mInputStream.read (buffer);
                        if (null != onDataReceiveListener) {
                            Log.e (TAG, "run: "+buffer);
                            Log.e ("stvel",  "serialManager----ReadThread----run: "+Bytes2HexString(buffer));
                            onDataReceiveListener.onDataReceive (buffer, size);
                        }
                    }
                    Thread.sleep (15);
                } catch (Exception e) {
                    e.printStackTrace ();
                    return;
                }
            }
        }
    }

    private boolean poweron() {
        Log.d("stvel", "poweron: here");
        boolean bRet=true;
       /* try {
            FileWriter localFileWriterOn=new FileWriter (new File ("/proc/gpiocontrol/set_sam"));
            localFileWriterOn.write ("1");
            localFileWriterOn.close ();
            Thread.sleep (300);
        } catch (Exception e) {
            e.printStackTrace ();
            bRet=false;
        }*/
        updateDevStatus(true);
        return bRet;
    }

    private void poweroff() {
        updateDevStatus(false);
        /*try {
            FileWriter localFileWriterOff=new FileWriter (new File ("/proc/gpiocontrol/set_sam"));
            localFileWriterOff.write ("0");
            localFileWriterOff.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }*/
    }


    private void updateDevStatus(boolean enable) {
        Log.d("stvel", "updateDevStatus---enable: " + enable);
        setDevState(mPrinterBoot, enable);
        setDevState(mPrinterPwr, enable);
    }

    private void setDevState(File dev, boolean enable) {

        String devPath = dev.getPath();
        String state = enable ? "1" : "0";
        Log.d("stvel", "setDevState---try to update " + devPath + " with value " + state);


            if (dev.exists()) {
                try {
                    FileWriter filewriter = new FileWriter(dev);
                    filewriter.write(state);
                    filewriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("stvel", "setDevState---Dev " + devPath + " is not exist");
            }

    }


}
