package com.wezen.madisonpartner.information;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.information.bottomsheet.CategoriesAdapter;

import java.util.ArrayList;
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
    private LinearLayoutManager layoutManager;
    private Button buttonCategories;
    private BottomSheetLayout bottomSheet;
    private List<Category> availableCategories;
    private List<Category> currentCategories;
    private RecyclerView rvCategories;
    private CategoriesAdapter currentAdapter;
    private CategoriesAdapter availableAdapter;
    private RecyclerView recyclerViewAvailables;
    private View bottomSheetLayout;

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
        bottomSheet = (BottomSheetLayout)view.findViewById(R.id.bottomsheet);

        buttonCategories = (Button)view.findViewById(R.id.buttonCategories);
        rvCategories = (RecyclerView)view.findViewById(R.id.recyclerViewBusinessCategories);
        layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvCategories.setLayoutManager(layoutManager);


        editTextName = (EditText)view.findViewById(R.id.editTextBusinessName);
        editTextDescription = (EditText)view.findViewById(R.id.editTextBusinessDescription);
        getBusinessInformation();

        return view;
    }

    private void getBusinessInformation() {
        ParseQuery<ParseObject> queryServices = ParseQuery.getQuery("HomeServices");
        queryServices.whereEqualTo("serviceProvider", ParseUser.getCurrentUser());
        queryServices.include("Category");
        queryServices.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    editTextName.setText(parseObject.getString("name"));
                    editTextDescription.setText(parseObject.getString("description"));
                    List<ParseObject> list = parseObject.getList("Category");
                    currentCategories = new ArrayList<>();
                    List<String> names = new ArrayList<>();
                    for (ParseObject po : list) {
                        names.add(po.getString("name"));
                        Category category = new Category();
                        category.setId(po.getObjectId());
                        category.setName(po.getString("name"));
                        category.setImage(po.getParseFile("image").getUrl());
                        category.setMainColor(po.getString("mainColor"));
                        category.setSecondaryColor("secondaryColor");
                        currentCategories.add(category);
                    }
                    currentAdapter = new CategoriesAdapter(currentCategories, getActivity(), InformationFragment.this, false);
                    rvCategories.setAdapter(currentAdapter);

                    getAvailableCategories(names);
                } else {//ups
                    //TODO handle the error
                }
            }
        });

    }

    private void  getAvailableCategories(List<String> names){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Categories");
       // query.whereNot("objectId",category);
        query.whereNotContainedIn("name", names);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    availableCategories = new ArrayList<>();
                    for (ParseObject po : list) {
                        Category category = new Category();
                        category.setId(po.getObjectId());
                        category.setName(po.getString("name"));
                        category.setImage(po.getParseFile("image").getUrl());
                        category.setMainColor(po.getString("mainColor"));
                        category.setSecondaryColor("secondaryColor");
                        availableCategories.add(category);
                    }

                    buttonCategories.setOnClickListener(new MyOnClickListener(bottomSheet));
                } else {
                    Log.e("ERROR", e.getMessage());
                }
            }
        });


    }


    private class MyOnClickListener implements View.OnClickListener {
        private final BottomSheetLayout bottomSheet;


        public MyOnClickListener(BottomSheetLayout bottomSheet) {
            this.bottomSheet = bottomSheet;

        }

        @Override
        public void onClick(View v) {
            if(availableCategories.size() >  0){
                if(bottomSheetLayout == null){
                    bottomSheetLayout = LayoutInflater.from(getActivity()).inflate(R.layout.bottomsheet_categories, bottomSheet, false);
                }
                bottomSheet.showWithSheetView(bottomSheetLayout);
                if(recyclerViewAvailables == null){
                    recyclerViewAvailables = (RecyclerView)bottomSheetLayout.findViewById(R.id.recyclerViewCategories);
                    layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerViewAvailables.setLayoutManager(layoutManager);
                }
                //TODO usar notifydatasetchanged en vez de crear nuevos adapter , reusar layoutmanager
                if(availableAdapter == null){
                    availableAdapter = new CategoriesAdapter(availableCategories,getActivity(),InformationFragment.this,true);
                    recyclerViewAvailables.setAdapter(availableAdapter);
                } else{
                    availableAdapter.notifyDataSetChanged();
                }

            }
        }
    }

    public void addCategory(Category category){
        bottomSheet.dismissSheet();
        currentCategories.add(category);
        currentAdapter.notifyDataSetChanged();
        availableCategories.remove(category);
        availableAdapter.notifyDataSetChanged();

    }

    public void removeCategory(Category category){
        availableCategories.add(category);


    }

}
