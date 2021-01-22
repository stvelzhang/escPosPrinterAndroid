package com.example.esc_printdemo;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.print_sdk.enums.UNDERLINE_MODE;
import com.example.print_sdk.util.BitmapUtils;
import com.example.print_sdk.util.ByteUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by moxiaomo
 * on 2020/4/1
 */
public class PrintContract {

    private String TAG=PrintContract.class.getName ();


    private Context mContext;
    private PrintUtil pUtil;
    private SharedPreferences.Editor mEditor;
    private String  mConnLevel;
    private boolean mHeightDouble = false;
    private boolean mWidthDouble = false;
    private boolean mTextBold = false;
    private boolean mUnderLine = false;
    private boolean mWhiteBlackReverse = false;
    private boolean mLineHeight = false;
    private String  mLintHeightValue = "30";


    public PrintContract() {
    }

    public PrintContract(Context context, PrintUtil printUtil) {
        this.mContext=context;
        this.pUtil=printUtil;
        mEditor = mContext.getSharedPreferences("PrintStyleSet", MODE_PRIVATE).edit();
    }


    public void printInit() {
        try {
            pUtil.printEnableCertificate (true);
            pUtil.printEnableMark (true);
            pUtil.printLanguage (15);
            pUtil.printEncode (3);
            pUtil.getVersion ();
            SharedPreferences getPrintStyle = mContext.getSharedPreferences("PrintStyleSet", MODE_PRIVATE);

            mConnLevel = getPrintStyle.getString("connLevel", "25");
            mWidthDouble = getPrintStyle.getString("wdouble", "false").equals("true") ? true: false;
            mHeightDouble = getPrintStyle.getString("hdouble", "false").equals("true") ? true: false;
            mUnderLine = getPrintStyle.getString("underline", "false").equals("true") ? true: false;
            mWhiteBlackReverse = getPrintStyle.getString("reverseBW", "false").equals("true") ? true: false;
            mTextBold = getPrintStyle.getString("textbold", "false").equals("true") ? true: false;
            mLineHeight = getPrintStyle.getString("lineHeight", "false").equals("true") ? true: false;
            mLintHeightValue = getPrintStyle.getString("lineHeightValue", "30");


        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void printBarcodeEx(String text) {
        try {
            Bitmap bitmap = BitmapUtils.createBarcode (mContext,text,400,100,true,1);

            pUtil.printBarcode2 (bitmap);
            pUtil.printLine (2);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void printLabel(int number, String con) {
        try {
            pUtil.printEnableMark (true);
            pUtil.printState ();
            pUtil.printAllState();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (mConnLevel));
            if (!mWidthDouble && !mHeightDouble) {
                pUtil.printFontSize(MODE_ENLARGE.NORMAL);
            }else if(mWidthDouble && mHeightDouble){
                pUtil.printFontSize (MODE_ENLARGE.HEIGHT_WIDTH_DOUBLE);
            }else if(!mWidthDouble && mHeightDouble){
                pUtil.printFontSize (MODE_ENLARGE.HEIGHT_DOUBLE);
            }else {
                pUtil.printFontSize (MODE_ENLARGE.WIDTH_DOUBLE);
            }

            if(mLineHeight){
                pUtil.printLineHeightSet(Integer.valueOf(mLintHeightValue));
            }
            pUtil.printUnderLine(UNDERLINE_MODE.UNDERLINE_OFF);
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
            if (mTextBold){
                pUtil.printTextBold(true);
            }else {
                pUtil.printTextBold(false);
            }

            pUtil.printText ("The credentials of cashier");

            pUtil.printLine(3);
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
            pUtil.printText (" hello world! ");
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
            pUtil.printEndNumber ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void printLabel2(int number, String con) {
        try {
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (mConnLevel));
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


    public void printTextTempTest(int number) {
        Log.e("stvel", "printText----number:" + number +"---con:" + mConnLevel +
                "---mWidthDouble:" + mWidthDouble +
                "---mHeightDouble:" + mHeightDouble +
                "---mTextBold:" + mTextBold +
                "---mWhiteBlackReverse:" + mWhiteBlackReverse +
                "---mUnderLine:" + mUnderLine );

        try {
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (mConnLevel));

            if (!mWidthDouble && !mHeightDouble) {
                pUtil.printFontSize(MODE_ENLARGE.NORMAL);
            }else if(mWidthDouble && mHeightDouble){
                pUtil.printFontSize (MODE_ENLARGE.HEIGHT_WIDTH_DOUBLE);
            }else if(!mWidthDouble && mHeightDouble){
                pUtil.printFontSize (MODE_ENLARGE.HEIGHT_DOUBLE);
            }else {
                pUtil.printFontSize (MODE_ENLARGE.WIDTH_DOUBLE);
            }

            if (mTextBold){
                pUtil.printTextBold (true);
                pUtil.printDoubleStrike(true);
            } else{
                pUtil.printTextBold (false);
                pUtil.printDoubleStrike(false);
            }


            pUtil.printAlignment (ALIGN_MODE.ALIGN_RIGHT); // 对齐方式

            pUtil.printDoubleStrike(true);
            if(mLineHeight){
                pUtil.printLineHeightSet(Integer.valueOf(mLintHeightValue));
            }
            if(mUnderLine){
                pUtil.printUnderLine(UNDERLINE_MODE.UNDERLINE_LARGE); //下划线
            }else {
                pUtil.printUnderLine(UNDERLINE_MODE.UNDERLINE_OFF);
            }

            pUtil.printCharFont(true);
            if (mWhiteBlackReverse) {
                pUtil.printWhiteBlackReverse(true);
            }else {
                pUtil.printWhiteBlackReverse(false);
            }
//            pUtil.printText ("MY test printer");

            pUtil.printText (SystemUtils.LanguageChange (mContext));
//            pUtil.printText ("MY test printer \nWelcome \nMode of payment \n \n \n -------------------\n \n \n \n ***************");


            pUtil.printEndNumber ();

        } catch (IOException e) {

        }
    }

    public void printText(int number, String con) {
        Log.e("stvel", "printText----number:" + number +"---con:" + con);

        try {
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (con));

            pUtil.printFontSize (MODE_ENLARGE.NORMAL);
            //pUtil.printFontSize (MODE_ENLARGE.HEIGHT_WIDTH_DOUBLE);
            pUtil.printTextBold (true); // 是否加粗

//            pUtil.printAlignment (ALIGN_MODE.ALIGN_RIGHT); // 对齐方式

            pUtil.printAlignment (ALIGN_MODE.ALIGN_RIGHT); // 对齐方式

            pUtil.printDoubleStrike(true);
            pUtil.printUnderLine(UNDERLINE_MODE.UNDERLINE_LARGE); //下划线
            pUtil.printCharFont(true);
            pUtil.printWhiteBlackReverse(true);
//            pUtil.printText ("MY test printer");

            pUtil.printText (SystemUtils.LanguageChange (mContext));
//            pUtil.printText ("MY test printer \nWelcome \nMode of payment \n \n \n -------------------\n \n \n \n ***************");

            //pUtil.printText ("150 m³");
            /*pUtil.printTextBold (false); // 关闭加粗
            pUtil.printFontSize (MODE_ENLARGE.NORMAL); // 字体大小
            pUtil.printLine ();
            pUtil.printDashLine ();
            pUtil.printLine (2);
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
            pUtil.printBarcode ("123456", 80, 2);
            pUtil.printQR ("1234456", 200, 200);
            pUtil.printLine (3);*/
            pUtil.printEndNumber ();

        } catch (IOException e) {

        }
    }

    public void printTextRevo(int number, String con) {
        Log.e("stvel", "printText----number:" + number +"---con:" + con);

        try {
            pUtil.printState ();
            pUtil.printStartNumber (number);

            pUtil.printConcentration (Integer.valueOf (con));

            pUtil.printLine();
            pUtil.printAlignment (ALIGN_MODE.ALIGN_LEFT); //一定要在输出text之前填写才有效果
            pUtil.printLargeText("stvelzhang");

            pUtil.printUnderLine(UNDERLINE_MODE.UNDERLINE_LARGE);
            pUtil.printFontSize(MODE_ENLARGE.NORMAL);
            pUtil.printColor(true);
            pUtil.printWhiteBlackReverse(true);
            pUtil.printTextBold(true);
            pUtil.printDoubleStrike(true);
            pUtil.printLeftMargin();

            pUtil.printLine();
            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);
            pUtil.printText ("MY test printer \nWelcome \nMode of payment \n \n \n -------------------\n \n ");

            pUtil.printLine (3);
            pUtil.printAlignment (ALIGN_MODE.ALIGN_RIGHT);
            pUtil.printText (" right right");

            pUtil.printLine(3);
            pUtil.printEndNumber ();

        } catch (IOException e) {

        }
    }

    public void printTempTest(){
        pUtil.selectCommand(PrintUtil.RESET);
        pUtil.selectCommand(PrintUtil.LINE_SPACING_DEFAULT);
        pUtil.selectCommand(PrintUtil.ALIGN_CENTER);
        pUtil.printTextRevo("gourmet restauran\n\n");
        pUtil.selectCommand(PrintUtil.DOUBLE_HEIGHT_WIDTH);
        pUtil.printTextRevo("table number: Table 1\n\n");
        pUtil.selectCommand(PrintUtil.NORMAL);
        pUtil.selectCommand(PrintUtil.ALIGN_LEFT);
        pUtil.printTextRevo(PrintUtil.printTwoData("order number", "201507161515\n"));
        pUtil.printTextRevo(PrintUtil.printTwoData("ordering time", "2016-02-16 10:46\n"));
        pUtil.printTextRevo(PrintUtil.printTwoData("serving time", "2016-02-16 11:46\n"));
        pUtil.printTextRevo(PrintUtil.printTwoData("number:2people", "cashier: Zhang San\n"));

        pUtil.printTextRevo("--------------------------------\n");
        pUtil.selectCommand(PrintUtil.BOLD);
        pUtil.printTextRevo(PrintUtil.printThreeData("project", "quantity", "amount\n"));
        pUtil.printTextRevo("--------------------------------\n");
        pUtil.selectCommand(PrintUtil.BOLD_CANCEL);
        pUtil.printTextRevo(PrintUtil.printThreeData("miantiao", "1", "0.00\n"));
        pUtil.printTextRevo(PrintUtil.printThreeData("mifan", "1", "6.00\n"));
        pUtil.printTextRevo(PrintUtil.printThreeData("kaoji", "1", "26.00\n"));
        pUtil.printTextRevo(PrintUtil.printThreeData("liucai", "1", "226.00\n"));
        pUtil.printTextRevo(PrintUtil.printThreeData("niuroumian", "1", "2226.00\n"));
        pUtil.printTextRevo(PrintUtil.printThreeData("niuroumian", "888", "98886.00\n"));

        pUtil.printTextRevo("--------------------------------\n");
        pUtil.printTextRevo(PrintUtil.printTwoData("total", "53.50\n"));
        pUtil.printTextRevo(PrintUtil.printTwoData("erasure", "3.50\n"));
        pUtil.printTextRevo("--------------------------------\n");
        pUtil.printTextRevo(PrintUtil.printTwoData("yinshou", "50.00\n"));
        pUtil.printTextRevo("--------------------------------\n");

        pUtil.selectCommand(PrintUtil.ALIGN_LEFT);
        pUtil.selectCommand(PrintUtil.BLACKWHITE);
        pUtil.printTextRevo("beizhu: buyao la,buyao xiangcai");
        pUtil.printTextRevo("\n\n\n\n\n");
    }

    public void printQR(String text, int number, String con) {
        try {
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (mConnLevel));

            pUtil.printAlignment (ALIGN_MODE.ALIGN_CENTER);

            pUtil.printQR (text, 200, 200);
            pUtil.printLine (3);
            pUtil.printEndNumber ();
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    public void printBarcode(String text, int number, String con) {
        try {
            pUtil.printState ();
            pUtil.printAllState();
            pUtil.printStartNumber (number);
            pUtil.printEndNumber ();
            /*Bitmap bitmap = BitmapUtils.createBarcode (mContext,text,400,100,false,1);
            pUtil.printState ();
            pUtil.printStartNumber (number);
            pUtil.printConcentration (Integer.valueOf (mConnLevel));
            pUtil.printLineHeightOff();
            pUtil.printBarcode2 (bitmap);
            pUtil.printLine (3);
            pUtil.printEndNumber ();*/
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void printImg(Bitmap bitmap, String con) {
        try {
            pUtil.printState ();
            pUtil.printConcentration (Integer.valueOf (mConnLevel));
            pUtil.printLineHeightSet(30);
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
