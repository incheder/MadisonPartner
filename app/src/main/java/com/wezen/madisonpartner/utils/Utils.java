package com.wezen.madisonpartner.utils;

import android.os.Build;

/**
 * Created by eder on 17/11/2015.
 */
public class Utils {
    private Utils(){}

    public static boolean isAffectedVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
