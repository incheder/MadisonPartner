package com.wezen.madisonpartner.request;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.utils.AutofitRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<HomeServiceRequest> requestList;
    private RequestAdapter adapter;
    private ProgressBar progressBar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestListFragment newInstance(String param1, String param2) {
        RequestListFragment fragment = new RequestListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RequestListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_list, container, false);
        AutofitRecyclerView recyclerViewRequestList = (AutofitRecyclerView)view.findViewById(R.id.recyclerViewRequestList);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBarRequestList);
        recyclerViewRequestList.setHasFixedSize(true);
        requestList = new ArrayList<>();
        adapter = new RequestAdapter(requestList,getActivity());
        recyclerViewRequestList.setAdapter(adapter);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getList(null);
    }

    private void getList(HomeServiceRequestStatus status) {
        ParseQuery<ParseObject> queryRequest = ParseQuery.getQuery("HomeServiceRequest");
        if(ParseUser.getCurrentUser().getInt("userType") == 2){
            ParseQuery<ParseObject> queryServices = ParseQuery.getQuery("HomeServices");
            queryServices.whereEqualTo("serviceProvider", ParseUser.getCurrentUser());
            queryRequest.whereNotEqualTo("status", HomeServiceRequestStatus.RECHAZADO.getValue());
            if(status != null){
                queryRequest.whereEqualTo("status", status.getValue());
            }
            queryRequest.whereMatchesQuery("homeService",queryServices);

        } else {
            queryRequest.whereEqualTo("attendedBy", ParseUser.getCurrentUser());
        }
        //TODO if userType 3 query for attendedBy
        // queryRequest.whereNotEqualTo("status", HomeServiceRequestStatus.RECIBIDO.getValue());
        queryRequest.include("homeService");
        queryRequest.include("user");
        queryRequest.orderByDescending("createdAt");
        queryRequest.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (e == null) {
                    requestList.clear();
                    for (ParseObject po : list) {
                        HomeServiceRequest request = new HomeServiceRequest();
                        request.setId(po.getObjectId());
                        request.setLocation(new LatLng(po.getParseGeoPoint("userLocation").getLatitude(), po.getParseGeoPoint("userLocation").getLongitude()));
                        request.setName(po.getParseObject("user").getString("username"));
                        request.setDescription(po.getString("problemDescription"));
                        int status = po.getInt("status");
                        request.setStatus(HomeServiceRequestStatus.valueOf(status));
                        request.setHomeServiceID((po.getParseObject("homeService").getObjectId()));
                        request.setDate(po.getCreatedAt().toString());
                        request.setUserAvatar(po.getParseObject("user").getParseFile("userImage").getUrl());
                        request.setAddress(po.getString("address"));
                        request.setPhone(po.getString("phone"));
                        requestList.add(request);
                    }

                    adapter.notifyDataSetChanged();
                } else { // ups
                    //TODO mostrar algun mensaje de error con una imagen coqueta

                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_request_filter, menu);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_request_filter, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.all:
                getList(null);
                break;
            case R.id.send:
                getList(HomeServiceRequestStatus.RECIBIDO);
                break;
            case R.id.asigned:
                getList(HomeServiceRequestStatus.ASIGNADO);
                break;
            case R.id.done:
                getList(HomeServiceRequestStatus.COMPLETO);
                break;
            case R.id.canceled:
                getList(HomeServiceRequestStatus.CANCELADO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
