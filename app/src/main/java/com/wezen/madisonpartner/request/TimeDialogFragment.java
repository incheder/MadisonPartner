package com.wezen.madisonpartner.request;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.wezen.madisonpartner.R;

import java.util.Calendar;

/**
 * Created by eder on 11/9/15.
 */
public class TimeDialogFragment extends DialogFragment{

    private static final String ARG_HOME_SERVICE_NAME = "home_service_name";
    private static final String ARG_USER_ADDRESS = "user_address";
    private TimePickerDialog.OnTimeSetListener mListener;
    private DialogInterface.OnDismissListener dismissListener;



    public static TimeDialogFragment newInstance(String name, String address) {
        TimeDialogFragment fragment = new TimeDialogFragment();
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

        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity){
            AppCompatActivity activity = (AppCompatActivity)context;
            try {
                mListener = (TimePickerDialog.OnTimeSetListener) activity;
                dismissListener = (DialogInterface.OnDismissListener)activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement DatePickerDialog.OnDateSetListener");
            }

        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog =  new TimePickerDialog(getActivity(),mListener,hour,minute,false);
        timePickerDialog.setOnDismissListener(dismissListener);
        timePickerDialog.setTitle(R.string.choose_a_time);
        return timePickerDialog;
    }

}
