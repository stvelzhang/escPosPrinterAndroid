package com.example.esc_printdemo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.example.esc_printdemo.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Rair on 2017/4/27.
 * Email:rairmmd@gmail.com
 * Author:Rair
 */

public class PrintStyleSettingMainFragment extends PreferenceFragment {


    private SharedPreferences.Editor mEditor;
    private ListPreference printer_conn;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditor = getActivity().getSharedPreferences("PrintStyleSet", MODE_PRIVATE).edit();

        addPreferencesFromResource(R.xml.printer_style_mainsetting_pref);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        printer_conn = (ListPreference) findPreference("printer_conn");
        Preference printsetstyle = (Preference) findPreference("printsetstyle");
        Preference print_demo = (Preference) findPreference("print_demo");

        printer_conn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("stvel", "onPreferenceChange: " + newValue.toString());

                mEditor.putString("connLevel", newValue.toString());
                mEditor.apply();
                return true;
            }
        });
        printsetstyle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent=new Intent (getActivity(), EscPrintSettingActivity.class);
                startActivity (intent);
                return true;
            }
        });

        print_demo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent=new Intent (getActivity(), ESCActivity.class);
                startActivity (intent);
                return true;
            }
        });


    }
}
