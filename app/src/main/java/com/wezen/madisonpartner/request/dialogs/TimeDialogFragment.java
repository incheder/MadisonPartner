package com.wezen.madisonpartner.request.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.utils.Utils;

import java.util.Calendar;

/**
 * Created by eder on 11/9/15.
 */
public class TimeDialogFragment extends DialogFragment{

    private static final String ARG_HOME_SERVICE_NAME = "home_service_name";
    private static final String ARG_USER_ADDRESS = "user_address";
    private TimePickerDialog.OnTimeSetListener mListener;
    private DialogInterface.OnClickListener cancelListener;


    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }

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
                cancelListener = (DialogInterface.OnClickListener)activity;
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
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);

        final TimePickerDialog picker =  new TimePickerDialog(getActivity(),getConstructorListener(),hour,minute,false);

        picker.setTitle(R.string.choose_a_time);

        /*timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), cancelListener);
        timePickerDialog.setCancelable(false);*/
        if (Utils.isAffectedVersion()) {
            picker.setButton(DialogInterface.BUTTON_POSITIVE,
                    getActivity().getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TimePicker dp = picker.findViewById(android.internal.R);
                            //mListener.onTimeSet(null, hour,minute);

                        }
                    });
        }
            picker.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getActivity().getString(android.R.string.cancel),
                    cancelListener);
       // }
        return picker;
    }


    private  TimePickerDialog.OnTimeSetListener getConstructorListener() {
        return Utils.isAffectedVersion() ? mTimeSetListener : mListener;
    }

    //Added to handle parent listener
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
           // if (!isCancelled)
            {
                mListener.onTimeSet(view,hourOfDay,minute);
            }
        }
    };


}
