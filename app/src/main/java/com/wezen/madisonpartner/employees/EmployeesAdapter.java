package com.wezen.madisonpartner.employees;

import android.content.Context;
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
import com.wezen.madisonpartner.request.HomeServiceRequest;
import com.wezen.madisonpartner.request.HomeServiceRequestStatus;

import java.util.List;

/**
 * Created by eder on 28/10/15.
 */
public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.RequestHolder> {

    private List<Employee> list;
    private Context context;

    public EmployeesAdapter(List<Employee> list, Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public RequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RequestHolder vh;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_employee,parent,false);
        vh = new RequestHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(RequestHolder holder, final int position) {
        final Employee item = list.get(position);
        holder.name.setText(item.getName());
        Picasso.with(context).load(item.getAvatar()).into(holder.userAvatar);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EmployeeListActivity)context).sendData(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RequestHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView userAvatar;
        private LinearLayout container;

        public RequestHolder(View itemView) {
            super(itemView);

            userAvatar = (ImageView)itemView.findViewById(R.id.employeeAvatar);
            name = (TextView)itemView.findViewById(R.id.employeeName);
            container = (LinearLayout)itemView.findViewById(R.id.employeeContainer);

        }
    }


}
