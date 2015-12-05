package com.wezen.madisonpartner.information;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.wezen.madisonpartner.R;

/**
 * Created by eder on 5/9/15.
 */
public class SelectImageDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";

    private OnClickSelectImageDialogListener mListener;
    private String mParamTitle;

    public interface OnClickSelectImageDialogListener {
        void onCameraClicked();
        void onGalleryClicked();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }

    public static SelectImageDialogFragment newInstance(String title) {
        SelectImageDialogFragment fragment = new SelectImageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamTitle = getArguments().getString(ARG_TITLE);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity){
            AppCompatActivity activity = (AppCompatActivity)context;
            try {
                mListener = (OnClickSelectImageDialogListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnClickSelectImageDialogListener");
            }

        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mParamTitle)
                .setItems(R.array.select_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        mListener.onCameraClicked();
                        break;
                    case 1:
                        mListener.onGalleryClicked();
                        break;
                }

            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
