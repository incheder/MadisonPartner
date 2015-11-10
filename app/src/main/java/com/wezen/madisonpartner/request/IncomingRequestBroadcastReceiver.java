package com.wezen.madisonpartner.request;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eder on 11/7/15.
 */
public class IncomingRequestBroadcastReceiver extends com.parse.ParsePushBroadcastReceiver {

    private static String PARSE_DATA_KEY = "com.parse.Data";

    @Override
    protected void onPushReceive(Context context, Intent intent) {
       // super.onPushReceive(context, intent);
        getDataFromIntent(intent);
    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
            Log.d("TAG","data: " + data);
        } catch (JSONException e) {
            // Json was not readable...
            Log.e("ERROR",e.getMessage());
        }
        return data;
    }
}
