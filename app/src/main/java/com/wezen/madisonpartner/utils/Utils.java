package com.wezen.madisonpartner.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.request.HomeServiceRequestStatus;

/**
 * Created by eder on 17/11/2015.
 */
public class Utils {
    private Utils(){}

    public static boolean isAffectedVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isNetworkEnable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static int getColorByStatus(Context context, HomeServiceRequestStatus status){
        int color = ContextCompat.getColor(context, R.color.transparent);
        switch (status) {
            case ENVIADO:
                color = ContextCompat.getColor(context, R.color.palette_green);
                break;
            case ASIGNADO:
                color = ContextCompat.getColor(context, R.color.palette_yellow_dark);
                break;
            case CANCELADO:
                color = ContextCompat.getColor(context, R.color.palette_red);
                break;
            case COMPLETO:
                color = ContextCompat.getColor(context, R.color.palette_blue);
                break;
            case RECHAZADO:
                color = ContextCompat.getColor(context, R.color.palette_red);
                break;
        }
        return color;

    }
}
