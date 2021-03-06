package com.wezen.madisonpartner.request.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.DatePicker;

import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.utils.Utils;

import java.util.Calendar;

/**
 * Created by eder on 11/9/15.
 */
public class DateDialogFragment extends DialogFragment{

    private static final String ARG_HOME_SERVICE_NAME = "home_service_name";
    private static final String ARG_USER_ADDRESS = "user_address";

    private DatePickerDialog.OnDateSetListener mListener;
    private DialogInterface.OnClickListener cancelListener;

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }

    public static DateDialogFragment newInstance(String name, String address) {
        DateDialogFragment fragment = new DateDialogFragment();
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
                mListener = (DatePickerDialog.OnDateSetListener) activity;
                cancelListener = ( DialogInterface.OnClickListener) activity;
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
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        /*DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(),null,year,month,day);
        datePickerDialog.setTitle(R.string.choose_a_date);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), cancelListener);*/

        final DatePickerDialog picker = new DatePickerDialog(getActivity(),
                getConstructorListener(), year, month, day);
        picker.setTitle(R.string.choose_a_date);
        if (Utils.isAffectedVersion()) {
            picker.setButton(DialogInterface.BUTTON_POSITIVE,
                    getActivity().getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatePicker dp = picker.getDatePicker();
                            mListener.onDateSet(dp,
                                    dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                        }
                    });
        }

            picker.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getActivity().getString(android.R.string.cancel),
                    cancelListener);
      //  }

        return picker;
    }


    private DatePickerDialog.OnDateSetListener getConstructorListener() {
        return Utils.isAffectedVersion() ? null : mListener;
    }

}
