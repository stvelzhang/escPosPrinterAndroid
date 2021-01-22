package com.example.esc_printdemo.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.example.esc_printdemo.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Rair on 2017/4/27.
 * Email:rairmmd@gmail.com
 * Author:Rair
 */

public class PrintStyleSettingFragment extends PreferenceFragment {

    private EditTextPreference pre_editheight;
    private SharedPreferences.Editor mEditor;
    private SwitchPreference pre_lineheight;
    private SharedPreferences getPrintStyle;
    private boolean mLineHeight = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);

        mEditor = getActivity().getSharedPreferences("PrintStyleSet", MODE_PRIVATE).edit();
        getPrintStyle = getActivity().getSharedPreferences("PrintStyleSet", MODE_PRIVATE);
        mLineHeight = getPrintStyle.getString("lineHeight", "false").equals("true") ? true: false;
        addPreferencesFromResource(R.xml.printer_style_setting_pref);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferenceCategory hwDouble = (PreferenceCategory) findPreference("hw_double");
        SwitchPreference hdouble = (SwitchPreference) hwDouble.findPreference("pre_hdouble");
        SwitchPreference wdouble = (SwitchPreference) hwDouble.findPreference("pre_wdouble");

        PreferenceCategory preSecond = (PreferenceCategory) findPreference("pre_sec");
        SwitchPreference pre_textbold = (SwitchPreference) preSecond.findPreference("pre_textbold");
        SwitchPreference pre_reverse = (SwitchPreference) preSecond.findPreference("pre_reverse");
        SwitchPreference pre_underline = (SwitchPreference) preSecond.findPreference("pre_underline");
        pre_lineheight = (SwitchPreference) preSecond.findPreference("pre_lineheight");
        pre_editheight = (EditTextPreference) preSecond.findPreference("pre_editheight");

        if (!mLineHeight) {
            preSecond.removePreference(pre_editheight);
        }





        hdouble.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                mEditor.putString("hdouble", newValue.toString());
                mEditor.apply();
                return true;
            }
        });

        wdouble.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                mEditor.putString("wdouble", newValue.toString());
                mEditor.apply();
                return true;
            }
        });

        pre_textbold.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                mEditor.putString("textbold", newValue.toString());
                mEditor.apply();
                return true;
            }
        });

        pre_reverse.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                mEditor.putString("reverseBW", newValue.toString());
                mEditor.apply();
                return true;
            }
        });

        pre_lineheight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                mEditor.putString("lineHeight", newValue.toString());
                mEditor.apply();
                if(newValue.toString().equals("true")){
                    getPreferenceScreen().addPreference(pre_editheight);
                }else {
                    getPreferenceScreen().removePreference(pre_editheight);
                }
                return true;
            }
        });
        pre_editheight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String  height_value = (String) newValue.toString();
                pre_editheight.setText(height_value);
                pre_editheight.setSummary(height_value);
                mEditor.putString("lineHeightValue", newValue.toString());
                mEditor.apply();
                return true;
            }
        });

        pre_underline.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String  value = (String) newValue.toString();

                mEditor.putString("underline", newValue.toString());
                mEditor.apply();
                return true;
            }
        });


    }
}
