package com.wezen.madisonpartner.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.wezen.madisonpartner.MainActivity;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.request.HomeServiceRequestStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class SendingNotificationActivity extends AppCompatActivity {
    public  static final String ID = "id";
    public  static final String DATE = "date";
    public  static final String HOME_SERVICE_REQUEST_ID = "home_service_request_id";
    public  static final String HOME_SERVICE_NAME = "home_service_name";

    private ProgressBar progressBar;
    private LinearLayout orderSent;
    private String id;
    private String date;
    private String requestId;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_notification);
        progressBar = (ProgressBar)findViewById(R.id.progressBarNotification);
        orderSent = (LinearLayout)findViewById(R.id.orderSentLayout);
        Button btnBack = (Button)findViewById(R.id.notificationGoBack);
        btnBack.setOnClickListener(goBackClickListener);
        if(getIntent().getExtras()!= null){
            id =  getIntent().getStringExtra(ID);
            date =  getIntent().getStringExtra(DATE);
            requestId = getIntent().getStringExtra(HOME_SERVICE_REQUEST_ID);
            name = getIntent().getStringExtra(HOME_SERVICE_NAME);
        }


        //sendPushToClient(date,id,name);
        updateHomeServiceRequestStatus();

    }


    @Override
    public void onBackPressed() {
        goHome();
    }

    private void goHome(){
        Intent home = new Intent(this, MainActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
    }

    private View.OnClickListener goBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goHome();
        }
    };

    private void sendPushToClient(String date, String userID,String name){
        Map<String,Object> params = new HashMap<>();
        params.put("client",userID);
        params.put("date",date);
        params.put("homeServiceName",name);
        ParseCloud.callFunctionInBackground("sendPushToClient", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                progressBar.setVisibility(View.GONE);
                orderSent.setVisibility(View.VISIBLE);
                if (e == null) {
                    Log.d("SUCCESS: ", o.toString());
                    //finish();

                } else {
                    //TODO mostrar opcion de reenviar la notificacion y no hacerlo volver al menu principal para comenzar el procedimiento desde cero
                    Log.e("ERROR: ", e.getMessage());
                    TextView textViewOrderSent = (TextView)orderSent.findViewById(R.id.textview_notification);
                    if(textViewOrderSent!= null){
                        textViewOrderSent.setText(getResources().getString(R.string.order_not_sent));
                    }
                }
            }
        });
    }

    private void updateHomeServiceRequestStatus(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HomeServiceRequest");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null && parseObject != null){
                    parseObject.put("status", HomeServiceRequestStatus.ASIGNADO);
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            //el status se actulizó mandamos el push al cliente
                            sendPushToClient(date,id,name);
                        }
                    });
                } else {
                    //TODO mostrar mensaje de error en el textview
                }


            }
        });
    }
}
