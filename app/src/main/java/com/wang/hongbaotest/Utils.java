package com.wang.hongbaotest;

import android.widget.Toast;

public class Utils {

    public static void toast(CharSequence cs) {
        Toast.makeText(MyApp.mApp, cs, Toast.LENGTH_SHORT).show();
    }
}
