package com.wezen.madisonpartner.request;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
        HomeServiceRequest item = list.get(position);
        holder.name.setText(item.getName());
        holder.date.setText(item.getDate());
        holder.description.setText(item.getDescription());
        holder.review.setMax(item.getReview());
        Picasso.with(context).load(item.getImage()).into(holder.image);
        if(item.getStatus()!=null){
            holder.status.setText(item.getStatus().toString());
        }

        holder.rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  ((RequestHolder) context).showBottomSheet(position);
            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RequestHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView date;
        public TextView description;
        public RatingBar review;
        public ImageView image;
        public Button rating;
        public TextView status;

        public RequestHolder(View itemView) {
            super(itemView);

            image = (ImageView)itemView.findViewById(R.id.request_image);
            name = (TextView)itemView.findViewById(R.id.request_name);
            description = (TextView)itemView.findViewById(R.id.request_item_description);
            status = (TextView)itemView.findViewById(R.id.request_item_status);
            date = (TextView)itemView.findViewById(R.id.request_item_date);
            review = (RatingBar)itemView.findViewById(R.id.requestItemRating);
            rating = (Button)itemView.findViewById(R.id.buttonRequestItem);
        }
    }


}
