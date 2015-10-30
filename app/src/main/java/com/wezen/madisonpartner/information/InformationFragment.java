package com.wezen.madisonpartner.information;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.information.bottomsheet.BottomSheetCategoriesAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText editTextName;
    private EditText editTextDescription;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InformationFragment newInstance(String param1, String param2) {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        final BottomSheetLayout bottomSheet = (BottomSheetLayout)view.findViewById(R.id.bottomsheet);

        Button buttonCategories = (Button)view.findViewById(R.id.buttonCategories);
        buttonCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View bottomSheetLayout = LayoutInflater.from(getActivity()).inflate(R.layout.bottomsheet_categories, bottomSheet, false);
                bottomSheet.showWithSheetView(bottomSheetLayout);
                RecyclerView recyclerView = (RecyclerView)bottomSheetLayout.findViewById(R.id.recyclerViewCategories);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                String[] list = {"plomeria","cerrajeria","jardineria","limpieza"};
                BottomSheetCategoriesAdapter adapter = new BottomSheetCategoriesAdapter(Arrays.asList(list),getActivity());
                recyclerView.setAdapter(adapter);
            }
        });
        editTextName = (EditText)view.findViewById(R.id.editTextBusinessName);
        editTextDescription = (EditText)view.findViewById(R.id.editTextBusinessDescription);
        getBusinessInformation();
        return view;
    }

    private void getBusinessInformation() {
        ParseQuery<ParseObject> queryServices = ParseQuery.getQuery("HomeServices");
        queryServices.whereEqualTo("serviceProvider", ParseUser.getCurrentUser());
        queryServices.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    editTextName.setText(parseObject.getString("name"));
                    editTextDescription.setText(parseObject.getString("description"));
                } else {//ups
                  //TODO handle the error
                }
            }
        });

    }


}
