package com.example.print_sdk;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.print_sdk.enums.ALIGN_MODE;
import com.example.print_sdk.enums.BARCODE_1D_TYPE;
import com.example.print_sdk.enums.MODE_ENLARGE;
import com.example.print_sdk.enums.UNDERLINE_MODE;
import com.example.print_sdk.util.BitmapToByteUtils;
import com.example.print_sdk.util.BitmapUtils;
import com.example.print_sdk.util.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static android.content.Context.MODE_PRIVATE;
import static com.example.print_sdk.SerialManager.Bytes2HexString;

/**
 * 打印工具类
 */
public class PrintUtil implements SerialManager.OnDataReceiveListener {

    private static final String TAG=PrintUtil.class.getName ();
    private static OutputStreamWriter mWriter=null;
    private static OutputStream mOutputStream=null;
    private static InputStream mInputStream=null;
    public final static int WIDTH_PIXEL=384;
    private OnPrintEventListener mListener=null;

    // Esc Data length
    private int mEscLength=0;
    // Esc Data buffer
    private byte[] mEscBuf=null;
    private String mEncoding="GB2312";
    public boolean check_paper=true;

    private Handler mHandler;


/***stvelzhang start ***/
    /**
     * 打印纸一行最大的字节
     */
    private static final int LINE_BYTE_SIZE = 32;

    private static final int LEFT_LENGTH = 20;

    private static final int RIGHT_LENGTH = 12;

    /**
     * 左侧汉字最多显示几个文字
     */
    private static final int LEFT_TEXT_MAX_LENGTH = 8;

    /**
     * 小票打印菜品的名称，上限调到8个字
     */
    public static final int MEAL_NAME_MAX_LENGTH = 8;

    private static OutputStream outputStream = null;

    public static OutputStream getOutputStream() {
        return outputStream;
    }

    public static void setOutputStream(OutputStream outputStream) {
        PrintUtil.outputStream = outputStream;
    }


/**********stvelzhang end *************/

    public PrintUtil() {
    }

    /**
     * init Pos
     *
     * @param encoding 编码
     * @throws IOException
     */
    public PrintUtil(String encoding) throws IOException {
        mHandler=new Handler (Looper.getMainLooper ());
        SerialManager.getClient ().open ();
        SerialManager.getClient ().setOnDataReceiveListener (this);
        SerialManager.getClient ().startReadThread ();
//        SerialManager.getClient ().getStatus();//stvelzhang
//        SerialManager.getClient ().getPrintStatus();//stvelzhang

        mWriter=new OutputStreamWriter (SerialManager.getClient ().getOutputStream (), encoding);
        mOutputStream=SerialManager.getClient ().getOutputStream ();
        mInputStream=SerialManager.getClient ().getInputStream ();
        this.mEncoding=encoding;
        initPrinter ();
        try {
            Thread.sleep (50);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }

    public void setSocket(BluetoothSocket socket, String encoding) throws IOException {
        mWriter=new OutputStreamWriter (socket.getOutputStream (), encoding);
        mOutputStream=socket.getOutputStream ();
        this.mEncoding=encoding;
        initPrinter ();
    }


    public void print(byte[] bs) throws IOException {
        if (!check_paper) {
            return;
        }
        mOutputStream.write (bs);
        mOutputStream.flush ();
    }

    public void printRawBytes(byte[] bytes) throws IOException {
        if (!check_paper) {
            return;
        }
        mOutputStream.write (bytes);
        mOutputStream.flush ();
    }

    /**
     * init printer
     *
     * @throws IOException
     */
    public void initPrinter() throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x40);
        mWriter.flush ();
    }

    /**
     * 获取打印状态
     * Get print status
     *
     * @throws IOException
     */
    public void printState() throws IOException {
        mWriter.write (0x1D);
        mWriter.write (0x61);
//        mWriter.write (0x22);// 压轴  切刀移出
        mWriter.write (0x26);// 压轴  缺纸  切刀移出
        mWriter.flush ();
        try {
            Thread.sleep (100);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }

    public void printAllState() throws IOException {
        mWriter.write (0x10);
        mWriter.write (0x04);
        mWriter.write (0x05); //58mm 支持n = 5 打印机全部状态
        mWriter.flush ();
        try {
            Thread.sleep (100);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }

    /**
     * 设置语言
     * language setting
     *
     * @param mode
     * @throws IOException
     */
    public void printLanguage(int mode) throws IOException {
        if (!check_paper) {
            return;
        }
        mEscBuf=new byte[64];
        mEscLength=0;
        // enable block mark
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x53;
        mEscBuf[mEscLength++]=0x4C;
        mEscBuf[mEscLength++]=0x41;
        mEscBuf[mEscLength++]=0x4E;
        mEscBuf[mEscLength++]=((byte) mode);
//        byte[] buffer=new byte[mEscLength];
//        System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
        print (mEscBuf);
    }

    /**
     * 设置浓度
     * Set encoding
     *
     * @param level
     * @throws IOException
     */
    public void printConcentration(int level) throws IOException {
        if (!check_paper) {
            return;
        }
        mEscBuf=new byte[64];
        mEscLength=0;
        // enable block mark
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x53;
        mEscBuf[mEscLength++]=0x54;
        mEscBuf[mEscLength++]=0x44;
        mEscBuf[mEscLength++]=0x50;
        mEscBuf[mEscLength++]=(byte) level;
        byte[] buffer=new byte[mEscLength];
        System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
        print (buffer);
    }

    /**
     * 设置编码
     * Set encoding
     *
     * @param encode
     * @throws IOException
     */
    public void printEncode(int encode) throws IOException {
        if (!check_paper) {
            return;
        }
        mEscBuf=new byte[64];
        mEscLength=0;
        // enable block mark
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x43;
        mEscBuf[mEscLength++]=0x44;
        mEscBuf[mEscLength++]=0x54;
        mEscBuf[mEscLength++]=0x59;
        if (encode == 1) {
            mEscBuf[mEscLength++]=(byte) 2;
        } else {
            mEscBuf[mEscLength++]=(byte) encode;
        }
        byte[] buffer=new byte[mEscLength];
        System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
        print (buffer);
    }

    public void printBackPaper(int param) throws IOException {
        if (!check_paper) {
            return;
        }
        mEscBuf=new byte[64];
        mEscLength=0;
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x23;
        mEscBuf[mEscLength++]=0x54;
        mEscBuf[mEscLength++]=0x45;
        mEscBuf[mEscLength++]=0x41;
        mEscBuf[mEscLength++]=0x52;
        mEscBuf[mEscLength++]=(byte) param;
        mEscBuf[mEscLength++]=(byte) 0x00;
        byte[] buffer=new byte[mEscLength];
        System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
        print (buffer);
    }


    /****************************stvelzhang start ************************************************/
    /**
     * 打印文字
     *
     * @param text 要打印的文字
     */
    public  void printTextRevo(String text) {
        try {
            byte[] data = text.getBytes("gbk");
            mOutputStream.write(data, 0, data.length);
            mOutputStream.flush();
        } catch (IOException e) {
            //Toast.makeText(this.context, "发送失败！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 设置打印格式
     *
     * @param command 格式指令
     */
    public  void selectCommand(byte[] command) {
        try {
            mOutputStream.write(command);
            mOutputStream.flush();
        } catch (IOException e) {
            //Toast.makeText(this.context, "发送失败！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 复位打印机
     */
    public static final byte[] RESET = {0x1b, 0x40};

    /**
     * 左对齐
     */
    public static final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};

    /**
     * 中间对齐
     */
    public static final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};

    /**
     * 右对齐
     */
    public static final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};

    /**
     * 选择加粗模式
     */
    public static final byte[] BOLD = {0x1b, 0x45, 0x01};

    /**
     * 取消加粗模式
     */
    public static final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};

    /**
     * 宽高加倍
     */
    public static final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};

    /**
     * 宽加倍
     */
    public static final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};

    /**
     * 高加倍
     */
    public static final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};

    /**
     * 字体不放大
     */
    public static final byte[] NORMAL = {0x1d, 0x21, 0x00};

    public static final byte[] BLACKWHITE = { 0x1d, 0x42, 0x01 }; //选择黑白反显
    /**
     * 设置默认行间距
     */
    public static final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};

    /**
     * 设置行间距
     */
//	public static final byte[] LINE_SPACING = {0x1b, 0x32};//{0x1b, 0x33, 0x14};  // 20的行间距（0，255）


//	final byte[][] byteCommands = {
//			{ 0x1b, 0x61, 0x00 }, // 左对齐
//			{ 0x1b, 0x61, 0x01 }, // 中间对齐
//			{ 0x1b, 0x61, 0x02 }, // 右对齐
//			{ 0x1b, 0x40 },// 复位打印机
//			{ 0x1b, 0x4d, 0x00 },// 标准ASCII字体
//			{ 0x1b, 0x4d, 0x01 },// 压缩ASCII字体
//			{ 0x1d, 0x21, 0x00 },// 字体不放大
//			{ 0x1d, 0x21, 0x11 },// 宽高加倍
//			{ 0x1b, 0x45, 0x00 },// 取消加粗模式
//			{ 0x1b, 0x45, 0x01 },// 选择加粗模式
//			{ 0x1b, 0x7b, 0x00 },// 取消倒置打印
//			{ 0x1b, 0x7b, 0x01 },// 选择倒置打印
//			{ 0x1d, 0x42, 0x00 },// 取消黑白反显
//			{ 0x1d, 0x42, 0x01 },// 选择黑白反显
//			{ 0x1b, 0x56, 0x00 },// 取消顺时针旋转90°
//			{ 0x1b, 0x56, 0x01 },// 选择顺时针旋转90°
//	};

    /**
     * 打印两列
     *
     * @param leftText  左侧文字
     * @param rightText 右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public static String printTwoData(String leftText, String rightText) {
        StringBuilder sb = new StringBuilder();
        int leftTextLength = getBytesLength(leftText);
        int rightTextLength = getBytesLength(rightText);
        sb.append(leftText);

        // 计算两侧文字中间的空格
        int marginBetweenMiddleAndRight = LINE_BYTE_SIZE - leftTextLength - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        sb.append(rightText);
        return sb.toString();
    }

    /**
     * 打印三列
     *
     * @param leftText   左侧文字
     * @param middleText 中间文字
     * @param rightText  右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public static String printThreeData(String leftText, String middleText, String rightText) {
        StringBuilder sb = new StringBuilder();
        // 左边最多显示 LEFT_TEXT_MAX_LENGTH 个汉字 + 两个点
        if (leftText.length() > LEFT_TEXT_MAX_LENGTH) {
            leftText = leftText.substring(0, LEFT_TEXT_MAX_LENGTH) + "..";
        }
        int leftTextLength = getBytesLength(leftText);
        int middleTextLength = getBytesLength(middleText);
        int rightTextLength = getBytesLength(rightText);

        sb.append(leftText);
        // 计算左侧文字和中间文字的空格长度
        int marginBetweenLeftAndMiddle = LEFT_LENGTH - leftTextLength - middleTextLength / 2;

        for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
            sb.append(" ");
        }
        sb.append(middleText);

        // 计算右侧文字和中间文字的空格长度
        int marginBetweenMiddleAndRight = RIGHT_LENGTH - middleTextLength / 2 - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }

        // 打印的时候发现，最右边的文字总是偏右一个字符，所以需要删除一个空格
        sb.delete(sb.length() - 1, sb.length()).append(rightText);
        return sb.toString();
    }

    /**
     * 获取数据长度
     *
     * @param msg
     * @return
     */
    @SuppressLint("NewApi")
    private static int getBytesLength(String msg) {
        return msg.getBytes(Charset.forName("GB2312")).length;
    }

    /**
     * 格式化菜品名称，最多显示MEAL_NAME_MAX_LENGTH个数
     *
     * @param name
     * @return
     */
    public static String formatMealName(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        if (name.length() > MEAL_NAME_MAX_LENGTH) {
            return name.substring(0, 8) + "..";
        }
        return name;
    }
    /**********************************stvelzhang end *********************************************/


    /**
     * 文本强调模式
     * Text emphasis mode
     *
     * @param bold
     */
    public void printTextBold(boolean bold) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x45);
        /*if (bold) {
            mWriter.write (0x31);
        } else {
            mWriter.write (0x30);
        }*/
        mWriter.write (0x31);
        mWriter.flush ();
    }

    /**
     * 设置字体大小
     * Set font size
     *
     * @param mode
     * @throws IOException
     */
    public void printFontSize(MODE_ENLARGE mode) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1D);
        mWriter.write (0x21);
        mWriter.write (mode.Get ());
        mWriter.flush ();
    }

    public void printCutPaper() throws IOException {
        mWriter.write (0x1D);
        mWriter.write (0x56);
        mWriter.write (0x01);
        mWriter.flush ();
        try {
            Thread.sleep (100);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }

    /**
     * 打印换行
     * Print line break
     *
     * @return length 需要打印的空行数 Number of blank lines to be printed
     * @throws IOException
     */
    public void printLine(int lineNum) throws IOException {
        if (!check_paper) {
            return;
        }
        for (int i=0; i < lineNum; i++) {
            mWriter.write ("\n");
        }
        mWriter.flush ();
    }

    /**
     * 打印换行(只换一行)
     * Print line breaks (only line breaks)
     *
     * @throws IOException
     */
    public void printLine() throws IOException {
        printLine (1);
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     * Print blank (a tab position, about 4 Chinese characters)
     *
     * @param length 需要打印空白的长度 Need to print the length of the blank,
     * @throws IOException
     */
    public void printTabSpace(int length) throws IOException {
        if (!check_paper) {
            return;
        }
        for (int i=0; i < length; i++) {
            mWriter.write ("\t");
        }
        mWriter.flush ();
    }

    /**
     * 绝对打印位置
     * Absolute print position
     *
     * @return
     * @throws IOException
     */
    public byte[] setLocation(int offset) throws IOException {
        byte[] bs=new byte[4];
        bs[0]=0x1B;
        bs[1]=0x24;
        bs[2]=(byte) (offset % 256);
        bs[3]=(byte) (offset / 256);
        return bs;
    }

    public byte[] getGbk(String stText) throws IOException {
        byte[] returnText=stText.getBytes (mEncoding); // Must be placed in try
        return returnText;
    }

    private int getStringPixLength(String str) {
        int pixLength=0;
        char c;
        for (int i=0; i < str.length (); i++) {
            c=str.charAt (i);
            if (isChinese (c)) {
                pixLength+=24;
            } else {
                pixLength+=12;
            }
        }
        return pixLength;
    }

    // 判断一个字符是否是中文
    public boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    public boolean isChinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray ()) {
            if (isChinese (c))
                return true;// 有一个中文字符就返回
        }
        return false;
    }

    public int getOffset(String str) {
        return WIDTH_PIXEL - getStringPixLength (str);
    }

    /**
     * 打印文字
     * Print text
     *
     * @param text
     * @throws IOException
     */
    public void printText(String text) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (text);
        mWriter.flush ();
    }

    /**
     * 对齐0:左对齐，1：居中，2：右对齐
     * Alignment 0: Left alignment, 1: Center, 2: Right alignment
     */
    public void printAlignment(ALIGN_MODE alignment) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x61);
        mWriter.write (alignment.Get ());
        mWriter.flush ();
    }
    public void printLeftMargin() throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1D);
        mWriter.write (0x4C);
        mWriter.write (0x10);
        mWriter.write (0x01);

        mWriter.flush ();
    }



    /**
     * 下划线0:关，1:开，2：大下划线
     * Alignment 0: Left alignment, 1: Center, 2: Right alignment
     */
    public void printUnderLine(UNDERLINE_MODE underlineMode) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x2D);
        mWriter.write (underlineMode.Get ());
        mWriter.flush ();
    }

    public void printLineHeightSet(int heightSize) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x33);
        mWriter.write (heightSize);
        mWriter.flush ();
    }

    public void printLineHeightOff() throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x32);
        mWriter.flush ();
    }

    public void printColor(boolean isblack) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x72);
        if (!isblack) {
            mWriter.write (0x01);
        } else {
            mWriter.write (0x00);
        }
        mWriter.flush ();
    }


    /**
     * 反白 0:取消字符反白打印，1:设置字符反白打印
     * • When the LSB of n is 0, white/black reverse mode is turned off.
     * • When the LSB of n is 1, white/black reverse mode is turned on
     */
    public void printWhiteBlackReverse(boolean reverse) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1D);
        mWriter.write (0x42);
        if (reverse) {
            mWriter.write (0x31);
        } else {
            mWriter.write (0x30);
        }
        mWriter.flush ();
    }

    /**
     * 重叠 0：取消重叠，1:设置重叠
     * Turns double-strike mode on or off.
     * • When the LSB of n is 0, double-strike mode is turned off.
     * • When the LSB of n is 1, double-strike mode is turned on.
     */
    public void printDoubleStrike(boolean double_strike) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x47);

        if (double_strike) {
            mWriter.write (0x01);
        } else {
            mWriter.write (0x00);
        }


        mWriter.flush ();
    }


    /**
     * 重叠 0：取消重叠，1:设置重叠
     * Turns double-strike mode on or off.
     * • When the LSB of n is 0, double-strike mode is turned off.
     * • When the LSB of n is 1, double-strike mode is turned on.
     */
    public void printCharFont(boolean charfont) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x47);
        if (charfont) {
            mWriter.write (0x01);
        } else {
            mWriter.write (0x00);
        }
        mWriter.flush ();
    }



    public void printMarginLeft(int Param) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1D);
        mWriter.write (0x4C);
        mWriter.write ((byte) Param);
        mWriter.write ((byte) (Param >> 8));
        mWriter.flush ();
    }

    /**
     * Large Text
     * 大文字
     *
     * @param text
     * @throws IOException
     */
    public void printLargeText(String text) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1b);
        mWriter.write (0x21);
        //mWriter.write (48);
        //mWriter.write(0x80);
        mWriter.write(0x30);
        mWriter.write (text);
        mWriter.write (0x1b);
        mWriter.write (0x21);
        mWriter.write (0);
        mWriter.flush ();
    }


    /**
     * 开启一票一证
     * Open one ticket and one certificate
     *
     * @param bool
     * @throws IOException
     */
    public void printEnableCertificate(boolean bool) throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x23);
        mWriter.write (0x46);
        mWriter.write (0x54);
        mWriter.write (0x4B);
        mWriter.write (0x54);
        if (bool) {
            mWriter.write (0x31);
        } else {
            mWriter.write (0x30);
        }
        mWriter.flush ();
    }

    /**
     * 小票开始
     * Small ticket start
     *
     * @param number
     * @throws IOException
     */
    public void printStartNumber(int number) throws IOException {
        if (!check_paper) {
            return;
        }
        byte[] topByte=new byte[]{0x1D, 0x23, 0x53};
        byte[] endByte=ByteUtils.little_intToByte (number);
        byte[] senByte=ByteUtils.addBytes (topByte, endByte);
        mOutputStream.write (senByte);
        mOutputStream.flush ();
    }

    /**
     * 小票结尾
     * End of ticket
     *
     * @throws IOException
     */
    public void printEndNumber() throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1D);
        mWriter.write (0x23);
        mWriter.write (0x45);
        mWriter.flush ();
    }


    /**
     * printer barcode
     *
     * @param text
     * @param Height
     * @param Width  1-4
     * @throws IOException
     */
    public void printBarcode(String text, int Height, int Width) throws IOException {
        if (!check_paper) {
            return;
        }
        int dataLen=text.getBytes (mEncoding).length;
        mWriter.write (0x1D);
        mWriter.write ("h");
        mWriter.write (Height);

        mWriter.write (0x1D);
        mWriter.write ("w");
        mWriter.write (Width);

        mWriter.write (0x1D);
        mWriter.write ("k");
        mWriter.write ((byte) BARCODE_1D_TYPE.CODE128.Get ());
        mWriter.write (dataLen);
        mWriter.write (text);
        mWriter.flush ();
    }

    /**
     * printer barcode
     *
     * @param text
     * @param Height
     * @param Width  1-4
     * @throws IOException
     */
    public void printBarcodeEx(String text, int Height, int Width) throws IOException {
        if (!check_paper) {
            return;
        }

        int dataLen=text.getBytes (mEncoding).length;

        mWriter.write (0x1D);
        mWriter.write (0x48);
        mWriter.write (0x01);

        mWriter.write (0x1D);
        mWriter.write (0x77);
//        mWriter.write (Width);
        mWriter.write (0x02);

        mWriter.write (0x1D);
        mWriter.write (0x68);
        mWriter.write (Height);

        mWriter.write (0x1D);
        mWriter.write (0x6B);
        mWriter.write ((byte) BARCODE_1D_TYPE.CODE128.Get ());
        mWriter.write (dataLen);
        mWriter.write (text);
        mWriter.flush ();
    }

    public  void  esc_code128_print(String CodeText) throws IOException {
        int dataLen=0;
        try {
            dataLen=CodeText.getBytes ("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return ;
        }
        mEscBuf=new byte[64];
        mEscLength=0;
        mEscBuf[mEscLength++]=0x1D; // GS
        mEscBuf[mEscLength++]=0x6B; // k
        mEscBuf[mEscLength++]=(byte) BARCODE_1D_TYPE.CODE128.Get (); // 24;
        mEscBuf[mEscLength++]=(byte) dataLen;// n
        //esc_text_print (CodeText + "\n");
        // mEscBuf[mEscLength++] = 0x00;

        byte[] SourceTextGBK_Bytes=null;
        try {
            SourceTextGBK_Bytes=CodeText.getBytes ("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
        int SourceGBK_Length=SourceTextGBK_Bytes.length;
        System.arraycopy (SourceTextGBK_Bytes, 0, mEscBuf, mEscLength, SourceGBK_Length);
        mEscLength+=SourceGBK_Length;

        byte[] buffer=new byte[mEscLength];
        System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);

        print (buffer);

    }

    public synchronized int esc_text_print(String Text) {

        byte[] SourceTextGBK_Bytes=null;
        try {
            SourceTextGBK_Bytes=Text.getBytes ("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
        int SourceGBK_Length=SourceTextGBK_Bytes.length;
        System.arraycopy (SourceTextGBK_Bytes, 0, mEscBuf, mEscLength, SourceGBK_Length);
        mEscLength+=SourceGBK_Length;
        return mEscLength;
    }

    public void printBarcode2(Bitmap bitmap) throws IOException {
        if (!check_paper) {
            return;
        }
        byte[] bmpByteArray=BitmapToByteUtils.draw2PxPoint (bitmap);
        mOutputStream.write (bmpByteArray, 0, bmpByteArray.length);
        mOutputStream.flush ();
    }

    /**
     * printer QR
     *
     * @param text
     * @param height
     * @param width  384
     * @throws IOException
     */
    public void printQR(String text, int height, int width) throws IOException {
        if (!check_paper) {
            return;
        }
        Log.e("stvel", "printQR---mEncoding: " + mEncoding);
        String s_gbk=new String (text.getBytes (), mEncoding);
        Bitmap bitmap=BitmapUtils.encode2dAsBitmap (s_gbk, width, height, 2);
        byte[] bmpByteArray=BitmapToByteUtils.draw2PxPoint (bitmap);
        //    Log.i (TAG, "printQR: "+ByteUtils.Bytes2HexString (bmpByteArray));
        mOutputStream.write (bmpByteArray, 0, bmpByteArray.length);
        mOutputStream.flush ();
    }

    /**
     * 启用黑标检测
     * Enable black mark detection
     *
     * @param bool
     * @throws IOException
     */
    public void printEnableMark(boolean bool) throws IOException {
        if (!check_paper) {
            return;
        }
        mEscBuf=new byte[64];
        mEscLength=0;
        // enable block mark
        mEscBuf[mEscLength++]=0x1F;
        mEscBuf[mEscLength++]=0x1B;
        mEscBuf[mEscLength++]=0x1F;
        mEscBuf[mEscLength++]=(byte) 0x80;
        mEscBuf[mEscLength++]=0x04;
        mEscBuf[mEscLength++]=0x05;
        mEscBuf[mEscLength++]=0x06;
        if (bool) {
            mEscBuf[mEscLength++]=0x44;
        } else {
            mEscBuf[mEscLength++]=0x66;
        }
        byte[] buffer=new byte[mEscLength];
        System.arraycopy (mEscBuf, 0, buffer, 0, mEscLength);
        print (buffer);
    }

    /**
     * 转到下一个黑色标记
     * Go to next black mark
     *
     * @throws IOException
     */
    public void printGoToNextMark() throws IOException {
        if (!check_paper) {
            return;
        }
        mEscBuf=new byte[2];
        mEscLength=0;
        // check mark
        mEscBuf[mEscLength++]=0x1D;
        mEscBuf[mEscLength++]=0x0C;
        print (mEscBuf);
    }

    /**
     * 打印功能列表
     * Print function list
     *
     * @throws IOException
     */
    public void printFeatureList() throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x46);
        mWriter.flush ();
    }

    /**
     * 重置打印机
     * reset printer
     *
     * @throws IOException
     */
    public void resetPrint() throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x23);
        mWriter.write (0x52);
        mWriter.write (0x54);
        mWriter.write (0x46);
        mWriter.write (0x41);
        mWriter.flush ();
    }

    /**
     * 获取打印固件版本
     *
     * @throws IOException
     */
    public void getVersion() throws IOException {
        if (!check_paper) {
            return;
        }
        mWriter.write (0x1D);
        mWriter.write (0x49);
        mWriter.write (0x41);
        mWriter.flush ();
    }


    public void printTwoColumn(String title, String content) throws IOException {
        int iNum=0;
        byte[] byteBuffer=new byte[100];
        byte[] tmp;

        tmp=getGbk (title);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=setLocation (getOffset (content));
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=getGbk (content);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);

        print (byteBuffer);
    }

    public void printThreeColumn(String left, String middle, String right) throws IOException {
        int iNum=0;
        byte[] byteBuffer=new byte[200];
        byte[] tmp=new byte[0];

        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=getGbk (left);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        int pixLength=getStringPixLength (left) % WIDTH_PIXEL;
        if (pixLength > WIDTH_PIXEL / 2 || pixLength == 0) {
            middle="\n\t\t" + middle;
        }

        tmp=setLocation (192);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=getGbk (middle);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=setLocation (getOffset (right));
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=getGbk (right);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);

        print (byteBuffer);
    }


    public void printDashLine() throws IOException {
        printText ("--------------------------------");
    }

    public void printBitmap(Bitmap bmp) throws IOException {
        // bmp=BitmapUtils.compressPic (bmp);
        byte[] bmpByteArray=BitmapToByteUtils.draw2PxPoint (bmp);
        printRawBytes (bmpByteArray);
    }

    public void printBitmap2(Bitmap bmp) throws IOException {
        // bmp=BitmapUtils.compressPic (bmp);
        printImage (bmp);
    }

    /**
     * Name：printImage，Print the monochrome bitmap
     *
     * @param bitmap
     */
    public void printImage(Bitmap bitmap) {
        try {
            int width=bitmap.getWidth ();
            int height=bitmap.getHeight ();
            byte[] data=new byte[]{0x1B, 0x33, 0x00};//Set the row spacing to 0.
            mOutputStream.write (data, 0, data.length);
            mOutputStream.flush ();
            data[0]=(byte) 0x00;
            data[1]=(byte) 0x00;
            data[2]=(byte) 0x00;    //reset parameters

            int pixelColor;

            // ESC * m nL nH bitmap
            byte[] escBmp=new byte[]{0x1B, 0x2A, 0x00, 0x00, 0x00};
            escBmp[2]=(byte) 0x21;
            //nL, nH
            escBmp[3]=(byte) (width % 256);
            escBmp[4]=(byte) (bitmap.getWidth () / 256);

            // print each line
            for (int i=0; i < (height / 24) + 1; i++) {
                mOutputStream.write (escBmp, 0, escBmp.length);
                mOutputStream.flush ();
                for (int j=0; j < width; j++) {
                    for (int k=0; k < 24; k++) {
                        if (((i * 24) + k) < height) {
                            pixelColor=bitmap.getPixel (j, (i * 24) + k);
                            if (pixelColor != -1) {
                                data[k / 8]+=(byte) (128 >> (k % 8));
                            }
                        }
                    }
                    mOutputStream.write (data, 0, data.length);
                    mOutputStream.flush ();
                    //reset parameters
                    data[0]=(byte) 0x00;
                    data[1]=(byte) 0x00;
                    data[2]=(byte) 0x00;

                }
                //out
//                byte[] byte_send1=new byte[3];
//                byte_send1[0]=0x1B;
//                byte_send1[1]=0x4A;
//                byte_send1[2]=0x00;
                mOutputStream.write ("\n".getBytes ());
                mOutputStream.flush ();
            }
            Thread.sleep (1100);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }


    public void closeDev() {
        SerialManager.getClient ().close ();
    }


    // ================================================================ 串口接收数据部分 ===================================================

    @Override
    public void onDataReceive(final byte[] buffer, final int size) {
        Log.d("stvel", "onDataReceive: " + Bytes2HexString(buffer) + "---size:" + size);
        mHandler.post (new Runnable () {
            @Override
            public void run() {
                if (buffer[0] == 0x39) {
                    try {
                        String version=new String (buffer, 0, buffer.length, "ISO-8859-1");
                        mListener.onVersion (version);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace ();
                    }
//                } else if (buffer[2] == 0x0C) {
                } else if ( (size == 7) &&  buffer[6] == 0x04) {//stvelzhang
                    try {
                        Log.e (TAG, "run: no_paper");
                        Log.e ("stvel", "run: no_paper");
                        check_paper=false;
                        mListener.onPrintStatus (1); // no_paper
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
//                } else if (buffer[2] == 0x00) {
                } else if (buffer[6] == 0x00) {
                    check_paper=true;
                } else if (buffer[1] == 0x40) {
                    try {
                        Log.e (TAG, "run: error");
                        mListener.onPrintStatus (2); // error
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                } else if (size == 7) {
                    try {
                        Log.e (TAG, "run: success");
                        mListener.onPrintStatus (0); // success
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }else if(size > 7){
                    mListener.onPrintStatus (3); // remove no paper status
                }
            }
        });
    }


    public interface OnPrintEventListener {

        void onPrintStatus(int state);

        void onVersion(String version);
    }


    public void setPrintEventListener(OnPrintEventListener printEventListener) {
        this.mListener=printEventListener;
    }
}