package com.wezen.madisonpartner.request;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.employees.EmployeeListActivity;
import com.wezen.madisonpartner.request.dialogs.DateDialogFragment;
import com.wezen.madisonpartner.request.dialogs.IncomingRequestDialogFragment;
import com.wezen.madisonpartner.request.dialogs.TimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class IncomingRequestActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, IncomingRequestDialogFragment.OnClickIncomingRequestDialog,DialogInterface.OnClickListener{
    public static final int REQUEST_CODE = 1;
    private MapView map;
    private GoogleMap gMap;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd 'de' MMM 'del 'yyyy 'a las' hh:mm a", Locale.getDefault());
    private Calendar calendarDate;
    private Button accept;
    private Button decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_request);
        Toolbar toolbar = (Toolbar)findViewById(R.id.incoming_request_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        map = (MapView)findViewById(R.id.incomming_request_map);
        map.setClickable(false);
        map.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                gMap.getUiSettings().setAllGesturesEnabled(false);
                getRequest("hpENjphqcW");
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
        query.include("homeService");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject po, ParseException e) {
                if (e == null) {
                    HomeServiceRequest incomingRequest = new HomeServiceRequest();
                    incomingRequest.setLocation(new LatLng(po.getParseGeoPoint("userLocation").getLatitude(), po.getParseGeoPoint("userLocation").getLongitude()));
                    incomingRequest.setName(po.getParseObject("user").getString("username"));
                    incomingRequest.setDescription(po.getString("problemDescription"));
                    int status = po.getInt("status");
                    incomingRequest.setStatus(HomeServiceRequestStatus.valueOf(status));
                    incomingRequest.setHomeServiceID((po.getParseObject("homeService").getObjectId()));
                    incomingRequest.setDate(po.getCreatedAt().toString());
                    incomingRequest.setUserAvatar(po.getParseObject("user").getParseFile("userImage").getUrl());
                    incomingRequest.setAddress(po.getString("address"));
                    setData(incomingRequest);


                } else {
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
        accept = (Button)findViewById(R.id.incoming_request_accept);
        decline = (Button)findViewById(R.id.incoming_request_decline);
        accept.setVisibility(View.VISIBLE);
        decline.setVisibility(View.VISIBLE);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(request.getLocation(), 14));
        gMap.addMarker(new MarkerOptions().position(request.getLocation()));
        Picasso.with(this).load(request.getUserAvatar()).into(userAvatar);
        userName.setText(request.getName());
        userAddress.setText(request.getAddress());
        description.setText(request.getDescription());
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(IncomingRequestActivity.this,EmployeeListActivity.class);
                //startActivityForResult(intent, REQUEST_CODE);
                accept.setEnabled(false);
                decline.setEnabled(false);
                DateDialogFragment dialog = DateDialogFragment.newInstance("", "");
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IncomingRequestActivity.this, "decline", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendarDate = Calendar.getInstance();
        calendarDate.set(year,monthOfYear,dayOfMonth);

        TimeDialogFragment dialog = TimeDialogFragment.newInstance("","");
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(),null);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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

        IncomingRequestDialogFragment dialog = IncomingRequestDialogFragment.newInstance(message,title,showCancel);
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(),null);

    }

    @Override
    public void onPositiveButtonClicked() {
        enableButtons();
        //TODO crear cloud function para mandar el push al usuario que solicito el servicio, si un asistente realizará el servicio enviarle un push tambien
    }

    @Override
    public void onNegativeButtonClicked() {
        enableButtons();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        enableButtons();

    }

    private void enableButtons(){
        accept.setEnabled(true);
        decline.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            String result = data.getStringExtra(EmployeeListActivity.EMPLOYEE_ID);
            DateDialogFragment dialog = DateDialogFragment.newInstance("", "");
            dialog.setCancelable(false);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(dialog, null);
            ft.commitAllowingStateLoss();

        }
    }
}