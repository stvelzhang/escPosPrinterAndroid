package com.example.esc_printdemo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esc_printdemo.R;
import com.example.print_sdk.PSAMUtils;
import com.example.print_sdk.SerialManager;
import com.example.print_sdk.util.ByteUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class PsamActivity extends AppCompatActivity implements View.OnClickListener, SerialManager.OnDataReceiveListener {

    private static final String TAG=PsamActivity.class.getName ();


    private TextView show, status, cu;
    private EditText msg;
    private Button po1, po2, cmd, pof;

    private Handler handler=new Handler () {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage (msg);
            switch (msg.what) {
                case 1:
                    Bundle bundle=msg.getData ();
                    byte[] datas=bundle.getByteArray ("BYTE");
                    Integer size=bundle.getInt ("SIZE");
                    Log.e (TAG, "onDataReceive: " + ByteUtils.Bytes2HexString (datas));
                    if (datas.length != 0 && datas[0] != 0x70) {
                        show.setText (ByteUtils.Bytes2HexString (datas));
                        status.setText (getString (R.string.txt_success));
                        if (psamId == 1) {
                            cu.setText (getString (R.string.txt_select_card1));
                        } else {
                            cu.setText (getString (R.string.txt_select_card2));
                        }
                    } else {
                        try {
                            byte[] data=new byte[size];
                            System.arraycopy (data, 0, datas, 0, size);
                            String str=new String (datas, "ISO-8859-1");
                            status.setText (str);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace ();
                        }
                    }
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_psam);

        Toolbar toolbar_pasm=findViewById (R.id.toolbar_pasm);
        toolbar_pasm.setTitle ("");
        setSupportActionBar (toolbar_pasm);

        cu=findViewById (R.id.textView_b);
        show=findViewById (R.id.textView_v);
        status=findViewById (R.id.textView_s);
        msg=findViewById (R.id.editText_msg);

        cu.setText (getString (R.string.txt_select));
        show.setText ("");
        status.setText ("");
        msg.setText ("008400000008");

        po1=findViewById (R.id.button_p1);
        po2=findViewById (R.id.button_p2);
        cmd=findViewById (R.id.button_cmd);
        pof=findViewById (R.id.button_off);

        po1.setOnClickListener (this);
        po2.setOnClickListener (this);
        cmd.setOnClickListener (this);
        pof.setOnClickListener (this);



    }

    int psamId=1;
    byte[] bytes=new byte[]{0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x30, 0x00};

    @Override
    public void onClick(View view) {
        switch (view.getId ()) {
            case R.id.button_p1:
                resetPsam (1);

                psamId=1;
                break;
            case R.id.button_p2:
                resetPsam (2);
                psamId=2;
                break;
            case R.id.button_cmd:
                if (TextUtils.isEmpty (show.getText ().toString ())){
                    Toast.makeText (this, getString (R.string.toast_01), Toast.LENGTH_SHORT).show ();
                    return;
                }
                String hex=msg.getText ().toString ().trim ();
                sendApdu (psamId, hex);
                show.setText (hex);
                status.setText (getString (R.string.txt_success_execute));
                //status.setText (getString (R.string.txt_exec_cmd));
                break;
            case R.id.button_off:
                status.setText ("");
                show.setText ("");
                cu.setText (getString (R.string.txt_select));
                psamId=1;
                break;
        }
    }

    public void resetPsam(int type) {
        try {
            PSAMUtils psamUtils=new PSAMUtils (SerialManager.getClient ().getOutputStream ());
            psamUtils.resetPsam (type);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void sendApdu(int type, String hex) {
        try {
            PSAMUtils psamUtils=new PSAMUtils (SerialManager.getClient ().getOutputStream ());
            psamUtils.sendApdu (type, hex);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }


    @Override
    protected void onResume() {
        super.onResume ();
        SerialManager.getClient ().open ();
        SerialManager.getClient ().setOnDataReceiveListener (this);
    }

    @Override
    protected void onPause() {
        super.onPause ();

    }

    @Override
    protected void onStop() {
        super.onStop ();
        SerialManager.getClient ().close ();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();

    }

    @Override
    public void onDataReceive(byte[] buffer, int size) {
        Log.e (TAG, "onDataReceive: " + ByteUtils.Bytes2HexString (buffer));
        if (bytes[0] != 0x2D && bytes[0] != 0x73) {
            if (size > 0) {
                Message message=new Message ();
                message.what=1;
                Bundle bundle=new Bundle ();
                bundle.putInt ("SIZE", size);
                bundle.putByteArray ("BYTE", buffer);
                message.setData (bundle);
                handler.sendMessage (message);
            }
        }
    }
}
