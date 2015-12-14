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

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wezen.madisonpartner.MainActivity;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.request.HomeServiceRequestStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SendingNotificationActivity extends AppCompatActivity {
    public  static final String USER_ID = "userId";
    public  static final String DATE = "date";
    public  static final String HOME_SERVICE_REQUEST_ID = "home_service_request_id";
    public  static final String HOME_SERVICE_NAME = "home_service_name";
    public  static final String IMAGE_URL = "image_url";
    public static final String PROBLEM_DESCRIPTION = "problem_description";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd 'de' MMM 'del 'yyyy 'a las' hh:mm a", Locale.getDefault());

    private ProgressBar progressBar;
    private LinearLayout orderSent;
    private String userId;
    private String date;
    private String requestId;
    private String name;
    private String imageUrl;
    private String problemDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_notification);
        progressBar = (ProgressBar)findViewById(R.id.progressBarNotification);
        orderSent = (LinearLayout)findViewById(R.id.orderSentLayout);
        Button btnBack = (Button)findViewById(R.id.notificationGoBack);
        btnBack.setOnClickListener(goBackClickListener);
        if(getIntent().getExtras()!= null){
            userId =  getIntent().getStringExtra(USER_ID);
            date =  getIntent().getStringExtra(DATE);
            requestId = getIntent().getStringExtra(HOME_SERVICE_REQUEST_ID);
            name = getIntent().getStringExtra(HOME_SERVICE_NAME);
            requestId = getIntent().getStringExtra(HOME_SERVICE_REQUEST_ID);
            problemDesc = getIntent().getStringExtra(PROBLEM_DESCRIPTION);
        }
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

    private void sendPushToClient(String date, String userID,String name,String requestId,String imageUrl,String problemDesc){
        Map<String,Object> params = new HashMap<>();
        params.put("client",userID);
        params.put("date",date);
        params.put("homeServiceName",name);
        //params.put("requestId", requestId);
        params.put("imageUrl",imageUrl);
        params.put("problemDescription",problemDesc);
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
                    TextView textViewOrderSent = (TextView) orderSent.findViewById(R.id.textview_notification);
                    if (textViewOrderSent != null) {
                        textViewOrderSent.setText(getResources().getString(R.string.order_not_sent));
                    }
                }
            }
        });
    }

    private void updateHomeServiceRequestStatus(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HomeServiceRequest");
        query.getInBackground(requestId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                progressBar.setVisibility(View.GONE);
                orderSent.setVisibility(View.VISIBLE);
                if (e == null && parseObject != null) {//TODO get the data of umage url from parseObject
                    parseObject.put("status", HomeServiceRequestStatus.ASIGNADO.getValue());
                    parseObject.put("attendedBy", ParseUser.getCurrentUser());//TODO if the current user is admin, he can choose an employee to attend the request
                    Date dateForService = stringToDate(date);
                    if(dateForService != null){
                        parseObject.put("dateForService",dateForService);
                    }
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            //el status se actulizó mandamos el push al cliente
                            if (e == null) {
                                sendPushToClient(date, userId, name,requestId,imageUrl,problemDesc);
                            } else {
                                //no se actualizó
                                Log.e("ERROR: ", e.getMessage());
                                TextView textViewOrderSent = (TextView) orderSent.findViewById(R.id.textview_notification);
                                if (textViewOrderSent != null) {
                                    textViewOrderSent.setText(getResources().getString(R.string.order_not_sent));
                                }
                            }
                        }
                    });
                } else {
                    //TODO mostrar mensaje de error en el textview
                    Log.e("ERROR: ", e != null ? e.getMessage() : null);
                    TextView textViewOrderSent = (TextView) orderSent.findViewById(R.id.textview_notification);
                    if (textViewOrderSent != null) {
                        textViewOrderSent.setText(getResources().getString(R.string.request_not_updated));
                    }
                }


            }
        });
    }

    private Date stringToDate(String dateString){

        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         return convertedDate;
    }
}
