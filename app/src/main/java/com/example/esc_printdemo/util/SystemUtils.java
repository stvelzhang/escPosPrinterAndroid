package com.example.esc_printdemo.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

public class SystemUtils {


    public static boolean isZh(Context context) {
        Locale locale = Locale.getDefault();
        //>=24 is Android 7.0 or high
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        }
        String language = locale.getLanguage() + "-" + locale.getCountry();
        Log.e("stvel", "isZh: " + language);

        if (language.startsWith ("zh"))
            return true;
        else
            return false;

        /*Locale locale=context.getResources ().getConfiguration ().locale;
        String language=locale.getLanguage ();
        if (language.endsWith ("zh"))
            return true;
        else
            return false;*/
        //return true;
    }


    public static String LanguageChange(Context context) {
        String str="";
        if (!isZh (context)) {
            str=textEnContent ();
        } else {
            str=textZhContent ();
        }
        return str;
    }

    public static String textEnContent() {
        StringBuilder sb=new StringBuilder ();

        sb.append ("  The credentials of cashier      ");
        sb.append ("\n");
        sb.append ("Time   : ");
        sb.append ("2021-01-15     16:00");
        sb.append ("\n");
        sb.append ("The operator:admin");
        sb.append ("\n");
        sb.append ("The receipt number: 1234567890");
        sb.append ("\n");
        sb.append ("Number  Quantity  Price  Discount  Subtotal");
        sb.append ("\n");
        sb.append ("-----------");
        sb.append ("\n");
        sb.append ("AM126   1  1200  0   1200");
        sb.append ("\n");
        sb.append ("AM127   1  1300  0   1300");
        sb.append ("\n");
        sb.append ("AM128   1  1400  0   1400");
        sb.append ("\n");

        sb.append ("-----------");
        sb.append ("\n");
        sb.append ("Total sales: 3 ");
        sb.append ("\n");
        sb.append ("Total(RMB): 3900");
        sb.append ("\n");
        sb.append ("Actual amount(RMB): 3900");
        sb.append ("\n");
        sb.append ("Change(RMB): 0");
        sb.append ("\n");

        sb.append ("-----------");
        sb.append ("\n");
        sb.append ("Mode of payment: WeChat Pay ");
        sb.append ("\n");
        sb.append ("Welcome to come again! ");
        sb.append ("\n");
        sb.append ("Please keep the receipt.");
        sb.append ("\n");
        sb.append ("\n");
        sb.append ("\n");
        return sb.toString ();
    }

    public static String textZhContent() {
        StringBuilder sb=new StringBuilder ();

        sb.append ("        收 银 凭 据            ");
        sb.append ("\n");
        sb.append ("时间   : ");
        sb.append ("2021-01-15     16:00");
        sb.append ("\n");
        sb.append ("操作员:admin");
        sb.append ("\n");
        sb.append ("收据单号: 1234567890");
        sb.append ("\n");
        sb.append ("编号  数量  单价  折扣  小计");

        sb.append ("\n");
			/*
			sb.append("下划线——————————————————\n");

			sb.append("横线--------------------\n");
			sb.append("横线---------下划线————————\n");
			sb.append("------------------------\n");
			sb.append("——————————————————————————\n");
			sb.append("=== === === = = =   =======\n");
			sb.append("+++++++----————............\n");
			sb.append(".........................\n");
			sb.append(".|||||//////||||||\n");
            */
        sb.append ("-----------------------------");
        sb.append ("\n");
        sb.append ("AM126   1  1200  0   1200");
        sb.append ("\n");
        sb.append ("AM127   1  1300  0   1300");
        sb.append ("\n");
        sb.append ("AM128   1  1400  0   1400");
        sb.append ("\n");

        sb.append ("-----------------------------");
        sb.append ("\n");
        sb.append ("共销售数量: 3 ");
        sb.append ("\n");
        sb.append ("售价合计(RMB): 3900");
        sb.append ("\n");
        sb.append ("实收金额(RMB): 3900");
        sb.append ("\n");
        sb.append ("找零金额(RMB): 0");
        sb.append ("\n");

        sb.append ("-----------------------------");
        sb.append ("\n");
        sb.append ("支付方式: 微信支付 ");
        sb.append ("\n");
        sb.append ("\n");
        sb.append ("\n");
        return sb.toString ();
    }
}
