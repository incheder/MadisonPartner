package com.wezen.madisonpartner.request;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class IncomingRequestActivity extends AppCompatActivity {
    private MapView map;
    private GoogleMap gMap;

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
                if(e == null){
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
        Button accept = (Button)findViewById(R.id.incoming_request_accept);
        Button decline = (Button)findViewById(R.id.incoming_request_decline);
        accept.setVisibility(View.VISIBLE);
        decline.setVisibility(View.VISIBLE);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(request.getLocation(), 14));
        gMap.addMarker(new MarkerOptions().position(request.getLocation()));
        Picasso.with(this).load(request.getUserAvatar()).into(userAvatar);
        userName.setText(request.getName());
        userAddress.setText(request.getAddress());
        description.setText(request.getDescription());

    }
}
