package com.example.esc_printdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.esc_printdemo.util.SystemUtils;
import com.example.print_sdk.EscUtils;
import com.example.print_sdk.PSAMUtils;
import com.example.print_sdk.PrintUtil;
import com.example.print_sdk.SerialManager;
import com.example.print_sdk.enums.ALIGN_MODE;
import com.example.print_sdk.enums.BARCODE_1D_TYPE;
import com.example.print_sdk.enums.MODE_ENLARGE;
import com.example.print_sdk.util.BitmapUtils;
import com.example.print_sdk.util.ByteUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by moxiaomo
 * on 2020/4/1
 */
public class PrintContract {

    private String TAG=PrintContract.class.getName ();


    private Context mContext;
    private PrintUtil pUtil;

    public PrintContract() {
    }

    public PrintContract(Context context, PrintUtil printUtil) {
        this.mContext=context;
        this.pUtil=printUtil;
    }


    public void printInit() {
        try {
            pUtil.printEnableCertificate (true);
            pUtil.printEnableMark (false);
            pUtil.printLanguage (15);
            pUtil.printEncode (3);
            pUtil.getVersion ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void printLabel(int number, String con) {
        try {
            pUtil.printEnableMark (true);
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (con));
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);

            pUtil.printTextBold (true);
            pUtil.printText ("The credentials of cashier");
            pUtil.printLine ();
            pUtil.printAlignment (ALIGN_MODE.ALIGN_LEFT);
            pUtil.printTextBold (false);
            pUtil.printDashLine();
            pUtil.printText ("The operator:admin");
            pUtil.printLine ();
            pUtil.printText ("The receipt number: 1234567890");
            pUtil.printLine ();
            pUtil.printText ("Total(RMB): 3900");
            pUtil.printLine ();
            pUtil.printText ("Time   : 2021-01-18");
            pUtil.printLine ();
            pUtil.printDashLine();
            pUtil.printLine ();
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
            pUtil.printLine ();
            pUtil.printBarcode ("0262102000123#200831_08135#0", 80, 1);
            pUtil.printLine ();
            pUtil.printGoToNextMark ();
            pUtil.printEndNumber ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void printLabel2(int number, String con) {
        try {
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (con));
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);

            pUtil.printTextBold (true);
            pUtil.printText ("The credentials of cashier");
            pUtil.printLine ();
            pUtil.printAlignment (ALIGN_MODE.ALIGN_LEFT);
            //pUtil.printTextBold (false);
            pUtil.printDashLine();
            pUtil.printText ("物料名称:弹簧式安全阀");
            pUtil.printLine ();
            pUtil.printText ("规格型号:A48Y-PN100 DN150 V P54");
            pUtil.printLine ();
            pUtil.printText ("压力等级:5-8.4MPa温度范围450C-54");
            pUtil.printLine ();
            pUtil.printText ("供应商:");
            pUtil.printLine ();
            pUtil.printText ("仓库:");
            pUtil.printLine ();
            pUtil.printText ("数量:2.0台");
            pUtil.printLine ();
            pUtil.printText ("入库日期:2020-08-31");
            pUtil.printLine ();
            pUtil.printDashLine();
            pUtil.printLine ();
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
            pUtil.printLine ();
            pUtil.printBarcode ("0262102000123#200831_08135#0", 80, 1);
            pUtil.printLine ();
            pUtil.printGoToNextMark ();
            pUtil.printEndNumber ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }


    public void printLabel3(int number, String con) {
        try {
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (con));
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);

            pUtil.printTextBold (true);
            pUtil.printDashLine();
            pUtil.printLine ();
            pUtil.printText ("入场小票");
            pUtil.printLine ();
            pUtil.printAlignment (ALIGN_MODE.ALIGN_LEFT);
            //pUtil.printTextBold (false);
            pUtil.printDashLine();
            pUtil.printFontSize (MODE_ENLARGE.HEIGHT_WIDTH_DOUBLE);
            pUtil.printText ("车牌：粤B569CU");
            pUtil.printLine ();
            pUtil.printFontSize (MODE_ENLARGE.NORMAL);
            pUtil.printText ("路段:23145465");
            pUtil.printLine ();
            pUtil.printText ("车位名：NP-3");
            pUtil.printLine ();
            pUtil.printText ("车型：2131");
            pUtil.printLine ();
            pUtil.printFontSize (MODE_ENLARGE.HEIGHT_WIDTH_DOUBLE);
            pUtil.printText ("类型：普通卡");
            pUtil.printLine ();
            pUtil.printFontSize (MODE_ENLARGE.NORMAL);
            pUtil.printText ("入场时间：2020-09-02 17:33:58");
            pUtil.printLine ();
            pUtil.printText ("入场时间：2020-09-02 17:33:58");
            pUtil.printLine ();
            pUtil.printText ("停车有效时长:1分");
            pUtil.printLine ();
            pUtil.printText ("入场押金：0元");
            pUtil.printLine ();
            pUtil.printText ("应收金额：0元");
            pUtil.printLine ();
            pUtil.printFontSize (MODE_ENLARGE.HEIGHT_WIDTH_DOUBLE);
            pUtil.printText ("补交:0元");
            pUtil.printLine ();
            pUtil.printFontSize (MODE_ENLARGE.NORMAL);
            pUtil.printText ("收费前余额：755元");
            pUtil.printLine ();
            pUtil.printText ("现在的余额：755元");
            pUtil.printLine ();
            pUtil.printText ("收费员ID:002");
            pUtil.printLine ();
            pUtil.printText ("支付方式：现金");
            pUtil.printLine ();
            pUtil.printText ("小票类型：出场");
            pUtil.printLine ();
            pUtil.printDashLine();
            pUtil.printLine ();
            pUtil.printText ("温馨提示：车主您好！你所缴纳的费用为城市道路资源占用费。。。。。。");
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
            pUtil.printLine ();
            pUtil.printQR ("0262102000123#200831_08135#0", 200, 200);
            pUtil.printLine ();
            pUtil.printEndNumber ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }


    public void printText(int number, String con) {
        Log.e("stvel", "printText----number:" + number +"---con:" + con);

        try {
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (con));

            pUtil.printFontSize (MODE_ENLARGE.NORMAL);
            pUtil.printTextBold (true); // 是否加粗
            pUtil.printAlignment (ALIGN_MODE.ALIGN_LEFT); // 对齐方式
            pUtil.printText (SystemUtils.LanguageChange (mContext));
            //pUtil.printText ("150 m³");
            pUtil.printTextBold (false); // 关闭加粗
            pUtil.printFontSize (MODE_ENLARGE.NORMAL); // 字体大小
            pUtil.printLine ();
            pUtil.printDashLine ();
            pUtil.printLine (2);
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
            pUtil.printBarcode ("123456", 80, 2);
            pUtil.printQR ("1234456", 200, 200);
            pUtil.printLine (3);
            pUtil.printEndNumber ();

        } catch (IOException e) {

        }
    }


    public void printQR(String text, int number, String con) {
        try {
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (con));
            pUtil.printQR (text, 200, 200);
            pUtil.printLine (3);
            pUtil.printEndNumber ();
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public void printBarcode(String text, int number, String con) {
        try {
            Bitmap bitmap = BitmapUtils.createBarcode (mContext,"JM202009100001001",400,100,true,1);
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (con));
            pUtil.printBarcode2 (bitmap);
            pUtil.printLine (3);
            pUtil.printEndNumber ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void printImg(Bitmap bitmap, String con) {
        try {
            pUtil.printState ();
            pUtil.printConcentration (Integer.valueOf (con));
            pUtil.printBitmap (bitmap);
            pUtil.printLine (3);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    public void printFeatureList() {
        try {
            pUtil.printState ();
            pUtil.printFeatureList ();
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public void printThai() {
        try {
            pUtil.printState ();
            pUtil.printConcentration (25);
            pUtil.printFontSize (MODE_ENLARGE.NORMAL);
            pUtil.printAlignment (ALIGN_MODE.ALIGN_LEFT); // 对齐方式
            pUtil.printText ("หลังจากการจัดกลุ่มหมายเลขในรูปภาพดิจิทัลที่เขียนด้วยลายมือ");
            pUtil.printLine ();
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }


    public void setLanguage(int mode) {
        try {
            pUtil.printState ();
            pUtil.printLanguage (mode);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void printEnableMark(boolean bool) {
        try {
            pUtil.printState ();
            pUtil.printEnableMark (bool);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void printGoToNextMark() {
        try {
            pUtil.printState ();
            pUtil.printGoToNextMark ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void resetPrint() {
        try {
            pUtil.printState ();
            pUtil.resetPrint ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void closeDev() {
        pUtil.closeDev ();
    }


}
