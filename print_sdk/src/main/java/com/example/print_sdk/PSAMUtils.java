package com.example.print_sdk;

import com.example.print_sdk.util.ByteUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by moxiaomo
 * on 2020/8/3
 */
public class PSAMUtils {

    private OutputStreamWriter mWriter=null;
    private OutputStream mOutputStream=null;


    // Esc Data length
    private int mEscLength=0;
    // Esc Data buffer
    private byte[] mEscBuf=null;


    /**
     * init psam
     *
     * @throws IOException
     */
    public PSAMUtils(OutputStream outputStream) throws IOException {
        mWriter=new OutputStreamWriter (outputStream);
        mOutputStream=outputStream;
    }


    public void cmd(byte[] bs) throws IOException {
        mOutputStream.write (bs);
    }

    public void resetPsam(int type) throws IOException {
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x23);
        mWriter.write (0x50);
        mWriter.write (0x53);
        mWriter.write (0x41);
        mWriter.write (0x4D);
        if (type == 1) {
            mWriter.write (0x31);
            mWriter.write (0x00);
        } else if (type == 2) {
            mWriter.write (0x32);
            mWriter.write (0x00);
        }
        mWriter.flush ();
    }

    public void sendApdu(int type, String apduHex) throws IOException {
        mEscBuf=new byte[1024];
        mEscLength=0;
        byte[] cmd=ByteUtils.HexString2Bytes (apduHex);
        int tmpLength=(apduHex.length () / 2);
        if (type == 1) {
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x50;
            mEscBuf[mEscLength++]=0x53;
            mEscBuf[mEscLength++]=0x41;
            mEscBuf[mEscLength++]=0x4D;
            mEscBuf[mEscLength++]=0x31;
            mEscBuf[mEscLength++]=(byte) tmpLength;
            for (int i=0; i < tmpLength; i++) {
                mEscBuf[mEscLength++]=cmd[i];
            }
        } else if (type == 2) {
            mEscBuf[mEscLength++]=0x1B;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x23;
            mEscBuf[mEscLength++]=0x50;
            mEscBuf[mEscLength++]=0x53;
            mEscBuf[mEscLength++]=0x41;
            mEscBuf[mEscLength++]=0x4D;
            mEscBuf[mEscLength++]=0x31;
            mEscBuf[mEscLength++]=(byte) tmpLength;
            for (int i=0; i < tmpLength; i++) {
                mEscBuf[mEscLength++]=cmd[i];
            }
        }
        byte[] buffer=new byte[mEscLength];
        System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
        cmd (buffer);
    }


}
