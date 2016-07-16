package com.wezen.madisonpartner.request;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.employees.Employee;
import com.wezen.madisonpartner.employees.EmployeeListActivity;
import com.wezen.madisonpartner.notification.SendingNotificationActivity;
import com.wezen.madisonpartner.request.dialogs.ConfirmDialogFragment;
import com.wezen.madisonpartner.request.dialogs.DateDialogFragment;
import com.wezen.madisonpartner.request.dialogs.IncomingRequestDialogFragment;
import com.wezen.madisonpartner.request.dialogs.TimeDialogFragment;
import com.wezen.madisonpartner.utils.Utils;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class IncomingRequestActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, IncomingRequestDialogFragment.OnClickIncomingRequestDialog,
        DialogInterface.OnClickListener,ConfirmDialogFragment.OnClickConfirmDialog{

    public static final String REQUEST_ID = "request_id";
    public static final int REQUEST_CODE = 1;
    private MapView map;
    private GoogleMap gMap;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd 'de' MMM 'del 'yyyy 'a las' hh:mm a", Locale.getDefault());
    private Calendar calendarDate;
    private Button accept;
    private Button decline;
    private String id;
    private HomeServiceRequest incomingRequest;
    private DateDialogFragment dialogDate;
    private TimeDialogFragment dialogTime;
    private IncomingRequestDialogFragment dialogRequest;
    private boolean isCancelled = false;
    private ProgressBar progressBar;
    private String imageUrl;
    private String problemDesc;
    private ParseObject poToUpdate; //in case we reject the request
    private boolean hasEmployee = false;
    private String employeeName;
    private String employeeAvatarUrl;
    private String employeeId;
    private LinearLayout layoutStatus;
    private TextView statusLabel;
    private LinearLayout terminateServiceLayout;
    private Button terminate;
    private Button cancel;
    private String clientId;
    private LinearLayout layoutContent;
    private CardView cardRequestDate;
    private TextView textViewRequestDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_request);
        Toolbar toolbar = (Toolbar)findViewById(R.id.incoming_request_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        accept = (Button)findViewById(R.id.incoming_request_accept);
        decline = (Button)findViewById(R.id.incoming_request_decline);
        terminate = (Button)findViewById(R.id.terminate_service_button);
        cancel = (Button)findViewById(R.id.cancel_service_button);

        if(getIntent().getExtras() != null){
            id = getIntent().getStringExtra(REQUEST_ID);

        }

        layoutStatus = (LinearLayout)findViewById(R.id.request_layout_status);
        progressBar = (ProgressBar)findViewById(R.id.progressBarIncomingRequest);
        statusLabel = (TextView)findViewById(R.id.request_status_label);
        terminateServiceLayout = (LinearLayout)findViewById(R.id.terminate_service_layout);
        layoutContent = (LinearLayout)findViewById(R.id.incoming_request_content);
        cardRequestDate = (CardView)findViewById(R.id.card_request_date);
        textViewRequestDate = (TextView)findViewById(R.id.request_date);

        map = (MapView)findViewById(R.id.incomming_request_map);
        map.setClickable(false);
        map.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                gMap.getUiSettings().setAllGesturesEnabled(false);
                getRequest(id);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }


    private void getRequest(String id){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HomeServiceRequest");
        query.include("user");
        query.include("homeService.employees");
        query.include("review");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject po, ParseException e) {
                layoutContent.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (e == null) {
                    map.setVisibility(View.VISIBLE);
                    poToUpdate = po;
                    incomingRequest = new HomeServiceRequest();
                    incomingRequest.setLocation(new LatLng(po.getParseGeoPoint("userLocation").getLatitude(), po.getParseGeoPoint("userLocation").getLongitude()));
                    incomingRequest.setName(po.getParseObject("user").getString("username"));
                    problemDesc = po.getString("problemDescription");
                    incomingRequest.setDescription(problemDesc);
                    int status = po.getInt("status");
                    incomingRequest.setStatus(HomeServiceRequestStatus.valueOf(status));
                    incomingRequest.setHomeServiceID((po.getParseObject("homeService").getObjectId()));
                    incomingRequest.setDate(po.getCreatedAt().toString());
                    if (po.getParseObject("user").getParseFile("userImage") != null) {
                        incomingRequest.setUserAvatar(po.getParseObject("user").getParseFile("userImage").getUrl());

                    }
                    incomingRequest.setAddress(po.getString("address"));
                    incomingRequest.setUserID(po.getParseObject("user").getObjectId());
                    incomingRequest.setProviderName(po.getParseObject("homeService").getString("name"));
                    incomingRequest.setPhone(po.getString("phone"));
                    incomingRequest.setDateForService(po.getDate("dateForService"));

                    if(po.getParseObject("review") != null ){
                        incomingRequest.setReview(po.getParseObject("review").getInt("numStars"));
                        incomingRequest.setReviewText(po.getParseObject("review").getString("comment"));
                    }
                    //incomingRequest.setReview(po.getInt("rating"));

                    setData(incomingRequest);

                    ParseFile image = po.getParseObject("homeService").getParseFile("image");
                    imageUrl = null;
                    if (image != null) {
                        imageUrl = image.getUrl();
                    }


                } else {
                    map.setVisibility(View.INVISIBLE);
                    //ups!
                }
            }
        });

    }

    private void setData(HomeServiceRequest request){
        CircleImageView userAvatar = (CircleImageView)findViewById(R.id.incoming_request_user_avatar);
        TextView userName = (TextView)findViewById(R.id.incoming_request_name);
        TextView userAddress = (TextView)findViewById(R.id.incoming_request_address);
        TextView description = (TextView)findViewById(R.id.incoming_request_item_description);
        TextView phone = (TextView)findViewById(R.id.incoming_request_phone);
        CardView layoutRating = (CardView)findViewById(R.id.incoming_request_layout_rating);
        RatingBar ratingBar = (RatingBar)findViewById(R.id.incoming_request_ratingBar);
        TextView textViewReview = (TextView)findViewById(R.id.incoming_request_review_textView);


        //accept.setVisibility(View.VISIBLE);
        //decline.setVisibility(View.VISIBLE);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(request.getLocation(), 14));
        gMap.addMarker(new MarkerOptions().position(request.getLocation()));
        Picasso.with(this).load(request.getUserAvatar()).into(userAvatar);
        userName.setText(request.getName());
        userAddress.setText(request.getAddress());
        description.setText(request.getDescription());
        phone.setText(request.getPhone());

        if(ParseUser.getCurrentUser().getInt("userType") == 3
                || request.getStatus() == HomeServiceRequestStatus.ASIGNADO
                || request.getStatus() == HomeServiceRequestStatus.COMPLETO
                || request.getStatus() == HomeServiceRequestStatus.CANCELADO){
            accept.setVisibility(View.GONE);
            decline.setVisibility(View.GONE);
            cardRequestDate.setVisibility(View.VISIBLE);
            if(incomingRequest.getDateForService() != null){
                textViewRequestDate.setText(Utils.setDateFormat(incomingRequest.getDateForService()));
            } else {
                cardRequestDate.setVisibility(View.GONE);
            }

            if(request.getStatus() == HomeServiceRequestStatus.ASIGNADO && checkAfterDateForService(incomingRequest.getDateForService()) ){
                terminate.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                terminate.setTextColor(Utils.getColorByStatus(this,HomeServiceRequestStatus.valueOf(request.getStatus().getValue())));
            } else if(request.getStatus() == HomeServiceRequestStatus.ASIGNADO){
                terminate.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                cancel.setTextColor(Utils.getColorByStatus(this,HomeServiceRequestStatus.valueOf(request.getStatus().getValue())));
            }

        }else{
            accept.setVisibility(View.VISIBLE);
            decline.setVisibility(View.VISIBLE);
            cardRequestDate.setVisibility(View.GONE);
        }



        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ParseUser> list = poToUpdate.getParseObject("homeService").getList("employees");
                if (list != null && list.size() > 0) {
                    ArrayList<Parcelable> employeesList = new ArrayList<>();
                    for (ParseUser user : list) {
                        Employee employee = new Employee();
                        employee.setId(user.getObjectId());
                        employee.setName(user.getUsername());
                        if (user.getParseFile("userImage") != null) {
                            employee.setAvatarUrl(user.getParseFile("userImage").getUrl());
                        }
                        employeesList.add(Parcels.wrap(employee));
                    }
                    Intent intent = new Intent(IncomingRequestActivity.this, EmployeeListActivity.class);
                    intent.putParcelableArrayListExtra("employeesList", employeesList);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    accept.setEnabled(false);
                    decline.setEnabled(false);
                    dialogDate = DateDialogFragment.newInstance("", "");
                    dialogDate.setCancelable(false);
                    dialogDate.show(getSupportFragmentManager(), "dialogDate");
                }

            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IncomingRequestActivity.this, R.string.maybe_the_next_time, Toast.LENGTH_SHORT).show();
                updateHomeServiceRequestStatus(HomeServiceRequestStatus.RECHAZADO);
            }
        });
        terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(false);
                confirmDialogFragment.setCancelable(false);
                confirmDialogFragment.show(getSupportFragmentManager(), null);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(true);
                confirmDialogFragment.setCancelable(false);
                confirmDialogFragment.show(getSupportFragmentManager(), null);
            }
        });

        layoutStatus.setBackgroundColor(Utils.getColorByStatus(this,request.getStatus()));
        statusLabel.setText(request.getStatus().getValue() == -1 ? "" :request.getStatus().toString());
        layoutStatus.setVisibility(View.VISIBLE);
        if(request.getStatus() == HomeServiceRequestStatus.COMPLETO && request.getReview() > 0){
            layoutRating.setVisibility(View.VISIBLE);
            ratingBar.setRating(request.getReview());
            if(request.getReviewText() != null && !request.getReviewText().equals("")){
                textViewReview.setText(request.getReviewText());
                textViewReview.setVisibility(View.VISIBLE);
            } else {
                textViewReview.setVisibility(View.GONE);
            }

        } else {
            layoutRating.setVisibility(View.GONE);
        }

    }

    private boolean checkAfterDateForService(Date dateForService) {
        if(new Date().after(dateForService)){
            return true;
        }
        return false;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //if(!isCancelled || !Utils.isAffectedVersion()){
            if(dialogDate != null){
                dialogDate.dismiss();
            }
            isCancelled = false;
            calendarDate = Calendar.getInstance();
            calendarDate.set(year, monthOfYear, dayOfMonth);

            dialogTime = TimeDialogFragment.newInstance("","");
            dialogTime.setCancelable(false);
            dialogTime.show(getSupportFragmentManager(), "dialogTime");
       /* } else {
            isCancelled = false;
            enableButtons();
        }*/

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(!isCancelled || !Utils.isAffectedVersion()){

            if(dialogTime!=null){
                dialogTime.dismiss();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            calendar.set(Calendar.MINUTE,minute);
            calendar.set(Calendar.YEAR, calendarDate.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, calendarDate.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH));

            Date timeToAttend = calendar.getTime();
            String message = getResources().getString(R.string.invalid_date);
            String title = getResources().getString(R.string.choose_another_date);
            boolean showCancel = false;

            if(!timeToAttend.before(new Date())){
                message = dateFormat.format(timeToAttend);
                title = getResources().getString(R.string.incoming_request_dialog_title);
                showCancel = true;
            }

            dialogRequest = IncomingRequestDialogFragment.newInstance(message,title,showCancel,hasEmployee,employeeName,employeeAvatarUrl);
            dialogRequest.setCancelable(false);
            dialogRequest.show(getSupportFragmentManager(), "dialogRequest");

        }else {
            isCancelled = false;
            enableButtons();
        }

    }

    @Override
    public void onPositiveButtonClicked(String date) {
        if(dialogRequest!= null){
            dialogRequest.dismiss();
        }
        enableButtons();
        //TODO crear cloud function para mandar el push al usuario que solicito el servicio, si un asistente realizará el servicio enviarle un push tambien
        if(date != null){
            Intent intent = new Intent(this, SendingNotificationActivity.class);
            intent.putExtra(SendingNotificationActivity.DATE,date);
            intent.putExtra(SendingNotificationActivity.USER_ID,incomingRequest.getUserID());
            intent.putExtra(SendingNotificationActivity.HOME_SERVICE_REQUEST_ID, id);
            intent.putExtra(SendingNotificationActivity.HAS_EMPLOYEE,hasEmployee);
            if(hasEmployee){
                intent.putExtra(SendingNotificationActivity.EMPLOYEE_NAME,employeeName);
                intent.putExtra(SendingNotificationActivity.EMPLOYEE_ID,employeeId);
            }
            intent.putExtra(SendingNotificationActivity.HOME_SERVICE_NAME,incomingRequest.getProviderName());
            intent.putExtra(SendingNotificationActivity.IMAGE_URL,imageUrl);
            intent.putExtra(SendingNotificationActivity.PROBLEM_DESCRIPTION,problemDesc);
            startActivity(intent);

        }
    }

    @Override
    public void onNegativeButtonClicked() {
        enableButtons();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        enableButtons();
        isCancelled = which == DialogInterface.BUTTON_NEGATIVE;
    }

    private void enableButtons(){
        accept.setEnabled(true);
        decline.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            hasEmployee = true;
            employeeId = data.getStringExtra(EmployeeListActivity.EMPLOYEE_ID);
            employeeName = data.getStringExtra(EmployeeListActivity.EMPLOYEE_NAME);
            employeeAvatarUrl = data.getStringExtra(EmployeeListActivity.EMPLOYEE_AVATAR_URL);

            DateDialogFragment dialog = DateDialogFragment.newInstance("", "");
            dialog.setCancelable(false);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(dialog, null);
            ft.commitAllowingStateLoss();

        }
    }

    private void updateHomeServiceRequestStatus(final HomeServiceRequestStatus status){
        poToUpdate.put("status",status.getValue());
        poToUpdate.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    if(status == HomeServiceRequestStatus.RECHAZADO)
                        pushCancelService(true);
                        //finish();
                } else {
                    //ups
                    Toast.makeText(IncomingRequestActivity.this, R.string.we_could_not_comunicate_with_our_mothership, Toast.LENGTH_SHORT).show();
                    Log.e("ERROR","Home service request not updated: " + e.getMessage());
                }
            }
        });
    }

    private void pushServiceComplete(){
        Map<String,Object> params = new HashMap<>();
        params.put("userId",poToUpdate.getParseUser("user").getObjectId());
        params.put("requestId",poToUpdate.getObjectId());
        params.put("homeServiceName", ParseUser.getCurrentUser().getUsername());
        ParseFile file = ParseUser.getCurrentUser().getParseFile("userImage");
        if(file != null){
            params.put("avatarUrl", file.getUrl());

        }
        ParseCloud.callFunctionInBackground("sendServiceCompletedPush",params,new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if(e == null){

                }else {

                }

            }
        });
    }

    private void pushCancelService(final boolean isRefuse){
        Map<String,Object> params = new HashMap<>();
        params.put("userId",poToUpdate.getParseUser("user").getObjectId());
        params.put("requestId",poToUpdate.getObjectId());
        params.put("homeServiceName", ParseUser.getCurrentUser().getUsername());
        params.put("isRefuse",isRefuse );

        ParseFile file = ParseUser.getCurrentUser().getParseFile("userImage");
        if(file != null){
            params.put("avatarUrl", file.getUrl());

        }

        ParseCloud.callFunctionInBackground("sendCancelServicePushToUser",params,new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if(e == null){
                    if(isRefuse){
                        finish();
                    }

                }else {

                }

            }
        });
    }


    @Override
    public void onConfirmPositiveButtonClicked(final boolean isCancel) {
        if(isCancel){
            showCancelLayout();
        } else {
            showCompleteLayout();
        }

        poToUpdate.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null){
                    if(isCancel && parseObject.getInt("status") != HomeServiceRequestStatus.COMPLETO.getValue()){
                            poToUpdate.put("status", HomeServiceRequestStatus.CANCELADO.getValue());
                            showCancelLayout();
                            poToUpdate.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        pushCancelService(false);
                                    } else{
                                        //TODO add error message
                                    }
                                }
                            });

                    }
                    else if(parseObject.getInt("status") != HomeServiceRequestStatus.CANCELADO.getValue()){
                        poToUpdate.put("status", HomeServiceRequestStatus.COMPLETO.getValue());
                        showCompleteLayout();
                        poToUpdate.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    pushServiceComplete();
                                } else{
                                    //TODO add error message
                                }
                            }
                        });
                    } else{
                        String errorMessage = getResources().getString(R.string.can_not_terminate_service);
                        if(isCancel){
                            errorMessage = getResources().getString(R.string.can_not_cancel_service);
                        }
                        if(parseObject.getInt("status") == HomeServiceRequestStatus.CANCELADO.getValue() ){
                            showCancelLayout();
                        } else if(parseObject.getInt("status") == HomeServiceRequestStatus.COMPLETO.getValue()){
                            showCompleteLayout();
                        }
                        Toast.makeText(IncomingRequestActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }

                } else {
                    //TODO add message error
                }
            }
        });

    }

    private void showCancelLayout(){
        layoutStatus.setBackgroundColor(Utils.getColorByStatus(IncomingRequestActivity.this,HomeServiceRequestStatus.CANCELADO));
        statusLabel.setText(HomeServiceRequestStatus.CANCELADO.toString());
        cancel.setVisibility(View.GONE);
        terminate.setVisibility(View.GONE);
    }

    private void showCompleteLayout(){
        layoutStatus.setBackgroundColor(Utils.getColorByStatus(IncomingRequestActivity.this,HomeServiceRequestStatus.COMPLETO));
        statusLabel.setText(HomeServiceRequestStatus.COMPLETO.toString());
        terminate.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
    }
}
