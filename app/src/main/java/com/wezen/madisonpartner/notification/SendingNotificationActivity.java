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
    public static final String HAS_EMPLOYEE = "hasEmployee";
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String EMPLOYEE_NAME = "employee_name";


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd 'de' MMM 'del 'yyyy 'a las' hh:mm a", Locale.getDefault());

    private ProgressBar progressBar;
    private LinearLayout orderSent;
    private String userId;
    private String date;
    private String requestId;
    private String name;
    private String imageUrl;
    private String problemDesc;
    private boolean hasEmployee;
    private String employeeId;
    private boolean notificationSent = false;
    private String attendedByAvatar;
    //private String employeeName;


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
            problemDesc = getIntent().getStringExtra(PROBLEM_DESCRIPTION);
            hasEmployee = getIntent().getBooleanExtra(HAS_EMPLOYEE, false);
            employeeId = getIntent().getStringExtra(EMPLOYEE_ID);
            imageUrl = getIntent().getStringExtra(IMAGE_URL);
            //employeeName = getIntent().getStringExtra(EMPLOYEE_NAME);

        }

        if(savedInstanceState == null){
            getAttendedBy();
        } else {
            goHome();
        }

    }


    @Override
    public void onBackPressed() {
        goHome();
    }

    private void goHome(){
        Intent home = new Intent(this, MainActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
        finish();
    }

    private View.OnClickListener goBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goHome();
        }
    };

    private void sendPushToClient(String date, String userID,String serviceName,String imageUrl,String problemDesc,String attendedBy){
        Map<String,Object> params = new HashMap<>();
        params.put("client",userID);
        params.put("date",date);
        params.put("homeServiceName",serviceName);
        params.put("imageUrl",imageUrl);
        params.put("problemDescription",problemDesc);
        params.put("attendedBy",attendedBy);
        params.put("attendedByAvatar",attendedByAvatar);
        ParseCloud.callFunctionInBackground("sendPushToClient", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
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

    private void updateHomeServiceRequestStatus(final ParseUser attendedBy){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HomeServiceRequest");
        query.getInBackground(requestId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                progressBar.setVisibility(View.GONE);
                orderSent.setVisibility(View.VISIBLE);
                if (e == null && parseObject != null) {
                    parseObject.put("status", HomeServiceRequestStatus.ASIGNADO.getValue());
                    parseObject.put("attendedBy", attendedBy);//TODO if the current user is admin, he can choose an employee to attend the request
                    Date dateForService = stringToDate(date);
                    if (dateForService != null) {
                        parseObject.put("dateForService", dateForService);
                    }
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) { //el status se actulizó, mandamos el push al cliente
                                sendPushToClient(date, userId, name, imageUrl, problemDesc, attendedBy.getUsername());
                                if (hasEmployee) {//y al empleado
                                    sendPushToEmployee(requestId, employeeId);
                                }
                                notificationSent = true;
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

    private void sendPushToEmployee(String requestId, String employeeId){
        Map<String,Object> params = new HashMap<>();
        params.put("requestId", requestId);
        params.put("employeeId", employeeId);
        ParseCloud.callFunctionInBackground("sendPushToEmployee", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if (e == null) {
                    Log.d("SUCCESS: ", o.toString());
                    //finish();
                } else {
                    Log.e("ERROR: ", e.getMessage());

                }
            }
        });
    }

    private void getAttendedBy(){
        if(hasEmployee && employeeId != null){
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId",employeeId);
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if(e== null){
                        if(parseUser.getParseFile("userImage")!= null){
                            attendedByAvatar = parseUser.getParseFile("userImage").getUrl();
                        }
                        updateHomeServiceRequestStatus(parseUser);
                    } else {
                        Log.e("ERROR","empleado no encontrado: " + e.getMessage());
                    }
                }
            });
        } else {
            if(ParseUser.getCurrentUser().getParseFile("userImage")!= null){
                attendedByAvatar = ParseUser.getCurrentUser().getParseFile("userImage").getUrl();
            }
            updateHomeServiceRequestStatus(ParseUser.getCurrentUser());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(notificationSent){
            goHome();
        }
    }
}
