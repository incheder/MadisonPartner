package com.wezen.madisonpartner.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.wezen.madisonpartner.R;

/**
 * Created by eder on 5/9/15.
 */
public class NetworkDialogFragment extends DialogFragment {

    private static final String ARG_HOME_SERVICE_NAME = "home_service_name";
    private static final String ARG_USER_ADDRESS = "user_address";
    private OnClickOrderDialog mListener;
    private String mParamHomeServiceName;
    private String mParamUserAddress;

    public interface OnClickOrderDialog {
        void onButtonClicked();
    }

    public static NetworkDialogFragment newInstance(String name, String address) {
        NetworkDialogFragment fragment = new NetworkDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HOME_SERVICE_NAME, name);
        args.putString(ARG_USER_ADDRESS, address);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamHomeServiceName = getArguments().getString(ARG_HOME_SERVICE_NAME);
            mParamUserAddress = getArguments().getString(ARG_USER_ADDRESS);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity){
            AppCompatActivity activity = (AppCompatActivity)context;
            try {
                mListener = (OnClickOrderDialog) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }

        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //String message = String.format(getResources().getString(R.string.order_dialog), mParamHomeServiceName, mParamUserAddress);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.network_dialog_title)
                .setMessage(R.string.network_not_available)
                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        mListener.onButtonClicked();
                    }
                })
                /*.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })*/;
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
