package com.example.esc_printdemo.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.esc_printdemo.PrintContract;
import com.example.esc_printdemo.R;
import com.example.print_sdk.PrintUtil;
import com.example.print_sdk.util.CanvasUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CanvasActivity extends AppCompatActivity {

    private Bitmap bmp;
    private PrintContract printContract;
    private PrintUtil pUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_canvas);

        ImageView imageView=findViewById (R.id.imageview);
        try {
            pUtil=new PrintUtil ("GB2312");
            pUtil.setPrintEventListener (new PrintUtil.OnPrintEventListener () {
                @Override
                public void onPrintStatus(int state) {
                    switch (state) {
                        case 0:
                            Toast.makeText (CanvasActivity.this, getString (R.string.toast_print_success), Toast.LENGTH_SHORT).show ();
                            break;
                        case 1:
                            Toast.makeText (CanvasActivity.this, getString (R.string.toast_no_paper), Toast.LENGTH_SHORT).show ();
                            break;
                        case 2:
                            Toast.makeText (CanvasActivity.this, getString (R.string.toast_print_error), Toast.LENGTH_SHORT).show ();
                            break;

                    }
                }

                @Override
                public void onVersion(String version) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace ();
        }


        List<String> list=new ArrayList<String> ();
        list.add ("test test test");
        list.add ("test test test");
        list.add ("test test test");
        list.add ("test test test");
        list.add ("销售单：12345678333");
        bmp=CanvasUtil.createLeftImage ("P000001;S0202008001;10;5", list);

        imageView.setImageBitmap (bmp);
        printContract=new PrintContract (this, pUtil);
        printContract.printInit ();
        printContract.printEnableMark (true);

        Button btn_printer=findViewById (R.id.btn_printer);
        btn_printer.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                printContract.printImg (bmp, "39");
                printContract.printGoToNextMark ();
            }
        });


    }


}
