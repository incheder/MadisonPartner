package com.wezen.madisonpartner.information;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.information.bottomsheet.CategoriesAdapter;
import com.wezen.madisonpartner.request.dialogs.IncomingRequestDialogFragment;
import com.wezen.madisonpartner.utils.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private Button buttonSaveImage;
    private BottomSheetLayout bottomSheet;
    private List<Category> availableCategories;
    private List<Category> currentCategories;
    private RecyclerView rvCategories;
    private CategoriesAdapter currentAdapter;
    private CategoriesAdapter availableAdapter;
    private RecyclerView recyclerViewAvailables;
    private View bottomSheetLayout;
    private ParseObject business;
    private ProgressBar progressBar;
    private ImageView businessImage;
    private ImageView adminCategories;
    private final static int PICK_IMAGE_REQUEST = 1;
    private Bitmap businessBitmap;
    private RelativeLayout layoutContent;
    private FloatingActionButton fabSave;


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
        progressBar = (ProgressBar)view.findViewById(R.id.progressBarInformation);
        buttonSaveImage = (Button)view.findViewById(R.id.buttonSaveBusinessImage);
        rvCategories = (RecyclerView)view.findViewById(R.id.recyclerViewBusinessCategories);
        layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvCategories.setLayoutManager(layoutManager);
        layoutContent = (RelativeLayout)view.findViewById(R.id.informationFragmentContent);


        editTextName = (EditText)view.findViewById(R.id.editTextBusinessName);
        editTextDescription = (EditText)view.findViewById(R.id.editTextBusinessDescription);
        fabSave = (FloatingActionButton)view.findViewById(R.id.fabSaveBusinessInfo);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBusinessInformation();
            }
        });

        businessImage = (ImageView)view.findViewById(R.id.imageViewBusiness);
        adminCategories = (ImageView)view.findViewById(R.id.imageViewAdminCategories);

        buttonSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SelectImageDialogFragment dialogRequest = SelectImageDialogFragment.newInstance(getActivity().getResources().getString(R.string.select_image_title));
                dialogRequest.setCancelable(true);
                dialogRequest.show(getActivity().getSupportFragmentManager(), "dialogSelectImage");*/
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_REQUEST);

            }
        });

       // if(ParseUser.getCurrentUser().getInt("userType") == 2){
            getBusinessInformation();
        //}

        return view;
    }

    private void getBusinessInformation() {
        ParseQuery<ParseObject> queryServices = ParseQuery.getQuery("HomeServices");
        //if(ParseUser.getCurrentUser().getInt("userType") == 2){
            queryServices.whereEqualTo("serviceProvider", ParseUser.getCurrentUser());
       // } else {
         //   queryServices.whereEqualTo("employees", ParseUser.getCurrentUser());
        //}
        queryServices.include("Category");
        queryServices.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                fabSave.setVisibility(View.VISIBLE);
                layoutContent.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (e == null) {
                    business = parseObject;

                    editTextName.setText(parseObject.getString("name"));
                    editTextDescription.setText(parseObject.getString("description"));
                    if (parseObject.getParseFile("image") != null) {
                        Picasso.with(getActivity()).load((parseObject.getParseFile("image").getUrl())).into(businessImage);
                    }

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
                    currentAdapter = new CategoriesAdapter(currentCategories, getActivity(), InformationFragment.this, false, false);
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
                    adminCategories.setOnClickListener(new MyOnClickListener());
                } else {
                    Log.e("ERROR", e.getMessage());
                }
            }
        });


    }


    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(getActivity(),adminCategories);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_categories,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.popup_menu_add_categeories:
                            showBottomSheet(availableCategories,true);
                            break;
                        case R.id.popup_menu_delete_categories:
                            showBottomSheet(currentCategories,false);
                            break;
                    }

                    return true;
                }
            });
            popupMenu.show();
        }
    }

    public void addCategory(Category category,int position){
        bottomSheet.dismissSheet();
        currentCategories.add(0, category);
        //currentAdapter.notifyDataSetChanged();

        currentAdapter.notifyItemInserted(position);
        currentAdapter.notifyItemRangeChanged(position, currentCategories.size());
        availableCategories.remove(category);
        availableAdapter.notifyDataSetChanged();

    }

    public void removeCategory(Category category,int position){
        bottomSheet.dismissSheet();
        currentCategories.remove(category);
        //currentAdapter.notifyDataSetChanged();
        currentAdapter.notifyItemRemoved(position);
        currentAdapter.notifyItemRangeChanged(position, currentCategories.size());
        availableCategories.add(category);
        availableAdapter.notifyDataSetChanged();



    }



    private void saveBusinessInformation(){
        if(business!= null && validateInput()){
            business.put("name",editTextName.getText().toString());
            business.put("description", editTextDescription.getText().toString());
            List<ParseObject> categoriesList = new ArrayList<>();
            for (Category category : currentCategories) {
                ParseObject po = ParseObject.createWithoutData("Categories",category.getId());
                categoriesList.add(po);
            }
            business.put("Category", categoriesList);
            if(businessBitmap != null){
                final ParseFile pf = new ParseFile("business_image.jpeg",bitmapToByteArray(businessBitmap));
                pf.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            completeBusinessInfoSavingProcess(pf);
                        } else {
                            Log.e("ERROR",e.getMessage());
                            Toast.makeText(getActivity(), R.string.image_not_saved, Toast.LENGTH_SHORT).show();
                            //we try to save the rest of the data
                            completeBusinessInfoSavingProcess(null);
                        }
                    }
                });
            }else {
                completeBusinessInfoSavingProcess(null);
            }

        }

    }

    private boolean validateInput(){
        boolean isValid = true;
        if(TextUtils.isEmpty(editTextName.getText().toString())){
            isValid = false;
            editTextName.setError(getResources().getString(R.string.name_required));
        }
        if(TextUtils.isEmpty(editTextDescription.getText().toString())){
            isValid = false;
            editTextDescription.setError(getResources().getString(R.string.description_required));
        }

        return isValid;
    }

    private void showBottomSheet(List<Category> listCat, boolean isAdding){
        if(listCat.size() >  0){
            if(bottomSheetLayout == null){
                bottomSheetLayout = LayoutInflater.from(getActivity()).inflate(R.layout.bottomsheet_categories, bottomSheet, false);
            }
            bottomSheet.showWithSheetView(bottomSheetLayout);
            TextView title = (TextView)bottomSheet.findViewById(R.id.textViewBottomSheetTitle);
            if(isAdding){
                title.setText(R.string.choose_your_category);
            } else {
                title.setText(R.string.delete_your_category);

            }
            if(recyclerViewAvailables == null){
                recyclerViewAvailables = (RecyclerView)bottomSheetLayout.findViewById(R.id.recyclerViewCategories);
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerViewAvailables.setLayoutManager(layoutManager);
            }
            //TODO usar notifydatasetchanged en vez de crear nuevos adapter , reusar layoutmanager
            //if(availableAdapter == null){
                availableAdapter = new CategoriesAdapter(listCat,getActivity(),InformationFragment.this,isAdding,true);
                recyclerViewAvailables.setAdapter(availableAdapter);
           // } else{
             //   availableAdapter.notifyDataSetChanged();
            //}

        }
        else {
            String message = isAdding ? getResources().getString(R.string.no_more_categories_to_add) : getResources().getString(R.string.no_more_categories_to_delete);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public void launchGallery(){

        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
        startActivityForResult(chooseImageIntent, PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != 0){
            switch(requestCode) {
                case PICK_IMAGE_REQUEST:
                    businessBitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                    // TODO use bitmap
                    businessImage.setImageBitmap(businessBitmap);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void completeBusinessInfoSavingProcess(ParseFile pf){
        if(pf!= null){
            business.put("image",pf);
        }
        business.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), R.string.data_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.data_not_saved, Toast.LENGTH_SHORT).show();
                    Log.e("ERROR", e.getMessage());
                }
            }
        });
    }

}
