package com.wezen.madisonpartner.request;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.wezen.madisonpartner.R;

import java.util.List;

/**
 * Created by eder on 28/10/15.
 */
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

    private List<HomeServiceRequest> list;
    private Context context;

    public RequestAdapter(List<HomeServiceRequest> list, Context context){
        this.list = list;
        this.context = context;
    }




    @Override
    public RequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RequestHolder vh;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_request,parent,false);
        vh = new RequestHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(RequestHolder holder, final int position) {
        final HomeServiceRequest item = list.get(position);
        holder.name.setText(item.getName());
        holder.date.setText(item.getDate());
        holder.description.setText(item.getDescription());
        holder.review.setMax(item.getReview());
        Picasso.with(context).load(item.getUserAvatar()).into(holder.userAvatar);
        holder.address.setText(item.getAddress());
        holder.phone.setText(item.getPhone());
        if(item.getStatus()!=null){
            holder.status.setText(item.getStatus().toString());
            setColorByStatus(holder.status,item.getStatus());
        }

        if(item.getStatus() != HomeServiceRequestStatus.RECIBIDO){
            holder.accept.setVisibility(View.GONE);
        } else {
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent incomingRequest = new Intent(context,IncomingRequestActivity.class);
                    incomingRequest.putExtra(IncomingRequestActivity.REQUEST_ID,item.getId());
                   // incomingRequest.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(incomingRequest);
                }
            });

        }


        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent incomingRequest = new Intent(context,IncomingRequestActivity.class);
                incomingRequest.putExtra(IncomingRequestActivity.REQUEST_ID,item.getId());
                // incomingRequest.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(incomingRequest);
            }
        });

        holder.map.onCreate(null);
        holder.map.onResume();
        holder.map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(context);
                GoogleMap gMap = googleMap;

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(item.getLocation(), 16));
                gMap.addMarker(new MarkerOptions().position(item.getLocation()));
                gMap.getUiSettings().setAllGesturesEnabled(false);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RequestHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView address;
        private TextView date;
        private TextView description;
        private RatingBar review;
        private ImageView userAvatar;
        private Button accept;
        private TextView status;
        private MapView map;
        private LinearLayout container;
        private TextView phone;


        public RequestHolder(View itemView) {
            super(itemView);

            userAvatar = (ImageView)itemView.findViewById(R.id.requestUserAvatar);
            name = (TextView)itemView.findViewById(R.id.request_name);
            address = (TextView)itemView.findViewById(R.id.request_address);
            description = (TextView)itemView.findViewById(R.id.request_item_description);
            status = (TextView)itemView.findViewById(R.id.request_item_status);
            date = (TextView)itemView.findViewById(R.id.request_item_date);
            review = (RatingBar)itemView.findViewById(R.id.requestItemRating);
            accept = (Button)itemView.findViewById(R.id.buttonRequestItem);
            map = (MapView)itemView.findViewById(R.id.request_map);
            container = (LinearLayout)itemView.findViewById(R.id.request_item_container);
            phone = (TextView)itemView.findViewById(R.id.request_phone);

            map.setClickable(false);

        }
    }

    private void setColorByStatus(TextView textView, HomeServiceRequestStatus status){
        int color = ContextCompat.getColor(context, R.color.transparent);
        switch (status) {
            case RECIBIDO:
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
        textView.setBackgroundColor(color);

    }


}
