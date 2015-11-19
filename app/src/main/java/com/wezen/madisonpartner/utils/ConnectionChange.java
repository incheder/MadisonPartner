package com.wezen.madisonpartner.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by eder on 22/10/2015.
 */
public class ConnectionChange extends BroadcastReceiver {

    public static String SHOW_DIALOG = "action_show_dialog";
    private static  boolean isConnected;


    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //if(isConnected){
            isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            if(!isConnected){
                // Toast.makeText(context, "Connected " + type, Toast.LENGTH_SHORT).show();
                Intent addressResultIntent = new Intent(SHOW_DIALOG);
                //addressResultIntent.putExtra(DATA_ADDRESS,address);
                LocalBroadcastManager.getInstance(context).sendBroadcast(addressResultIntent);
            }
        //}
       // int type = activeNetworkInfo != null ? activeNetworkInfo.getType() : -1;



    }
}
