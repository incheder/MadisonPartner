package com.wezen.madisonpartner.request;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eder on 11/7/15.
 */
public class IncomingRequestBroadcastReceiver extends com.parse.ParsePushBroadcastReceiver {

    private static final String PARSE_INCOMING_REQUEST = "homeServiceRequest";

    /*@Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        //return super.getActivity(context, intent);
        return IncomingRequestActivity.class;
    }*/

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        //super.onPushOpen(context, intent);
        JSONObject jObject= getDataFromIntent(intent);
        Intent incomingRequest = new Intent(context,IncomingRequestActivity.class);
        try {
            incomingRequest.putExtra(IncomingRequestActivity.REQUEST_ID,jObject.getString(PARSE_INCOMING_REQUEST));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        incomingRequest.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(incomingRequest);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
       // getDataFromIntent(intent);
    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            String PARSE_DATA_KEY = "com.parse.Data";
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
            Log.d("TAG","data: " + data);
        } catch (JSONException e) {
            // Json was not readable...
            Log.e("ERROR",e.getMessage());
        }
        return data;
    }
}
