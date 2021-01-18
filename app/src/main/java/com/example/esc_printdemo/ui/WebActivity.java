package com.example.esc_printdemo.ui;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.esc_printdemo.R;
import com.example.print_sdk.PrintUtil;
import com.example.print_sdk.SerialManager;
import com.example.print_sdk.util.CanvasUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by moxiaomo
 * on 2020/7/27
 */
public class WebActivity extends Activity {


    WebView webview;
    private Bitmap bmp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_web);
        webview = findViewById (R.id.webview);


        initWeb ();
        String lang = Locale.getDefault().getLanguage();
        String filename = "home.html";
        if (lang.equals("en")) {
            filename = "home.en.html";
        }
        webview.loadUrl ("file:///android_asset/"+filename);
    }


    public void initWeb() {
        // Android 与 html 的之间的交互需要通过 JS，所以setJavaScriptEnabled（true）
        JsInterface jt=new JsInterface ();
        webview.getSettings ().setJavaScriptCanOpenWindowsAutomatically (true);//设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings ().setJavaScriptEnabled (true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings ().setCacheMode (WebSettings.LOAD_NO_CACHE);

        // //设置自适应屏幕，两者合用
        webview.getSettings ().setLoadWithOverviewMode (true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings ().setUseWideViewPort (true);
        //缩放操作
        webview.getSettings ().setSupportZoom (true);//是否可以缩放，默认true
        webview.getSettings ().setBuiltInZoomControls (true);//是否显示缩放按钮，默认false
        webview.getSettings ().setDisplayZoomControls (true);
        // 其他操作
        webview.getSettings ().setAppCacheEnabled (true);//是setJavaScriptEnabled否使用缓存
        webview.getSettings ().setAllowFileAccess (true);
        webview.getSettings ().setDomStorageEnabled (true);//DOM Storage
        webview.getSettings ().setDatabaseEnabled (true);
        webview.getSettings ().setLayoutAlgorithm (WebSettings.LayoutAlgorithm.NARROW_COLUMNS);


        //设置js和android通信桥梁接口 JavaScriptBridge 对应映射字段-- > jsInterface;
        webview.addJavascriptInterface (jt, "Android");


        // 监听webview浏览器客户端
        webview.setWebChromeClient (new WebChromeClient ());
        webview.setWebViewClient (new WebViewClient () {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError (view, handler, error);
                handler.proceed ();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted (view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl (url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished (view, url);
                sendData ();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError (view, errorCode, description, failingUrl);
            }

        });
    }


    @Override
    public void onResume() {
        super.onResume ();
    }

    /**
     * Java调用javaScript
     * 　document.getElementById("content").innerHTML=src;
     */
    private void sendData() {
        Log.e ("TAG", "sendData: ");
        webview.post (new Runnable () {
            @Override
            public void run() {
                String base64=createBitmap ();
                String js="javascript:onSaveCallback('" + base64 + "')";
                webview.evaluateJavascript (js, new ValueCallback<String> () {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.e ("JavaAndJSActivity", "status ===" + s);
                    }
                });
            }
        });
    }

    public String createBitmap() {
        List<String> list=new ArrayList<String> ();
        list.add (getString (R.string.txt_07));
        list.add ("test web");
        list.add ("test web");
        list.add ("test web");
        bmp=CanvasUtil.createLeftImage (getString (R.string.txt_07), list);
        return bitmaptoString (bmp);
    }

    public String bitmaptoString(Bitmap bitmap) {
        // 将Bitmap转换成Base64字符串
        StringBuffer string=new StringBuffer ();
        ByteArrayOutputStream bStream=new ByteArrayOutputStream ();

        try {
            bitmap.compress (Bitmap.CompressFormat.PNG, 100, bStream);
            bStream.flush ();
            bStream.close ();
            byte[] bytes=bStream.toByteArray ();
            string.append (Base64.encodeToString (bytes, Base64.NO_WRAP));
        } catch (IOException e) {
            e.printStackTrace ();
        }
        System.out.println ("string.." + string.length ());
        return string.toString ();
    }






    int number=1000000001; // 初始化票据流水号 Initial bill serial number

    class JsInterface {

        @JavascriptInterface
        public void getBitmap() {
            try {
                PrintUtil printUtil = new PrintUtil ("GB2312");
                printUtil.printImage (bmp);
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }

    }

}
