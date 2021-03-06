package com.wezen.madisonpartner.request.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezen.madisonpartner.R;

/**
 * Created by eder on 5/9/15.
 */
public class IncomingRequestDialogFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_TITLE = "title";
    private static final String ARG_SHOW_CANCEL_BUTTON = "cancel_button";
    private static final String ARG_HAS_EMPLOYEE = "hasEmployee";
    private static final String ARG_EMPLOYEE_NAME = "employeeName";
    private static final String ARG_EMPLOYEE_AVATAR = "employeeAvatar";

    private OnClickIncomingRequestDialog mListener;
    private String mParamMessage;
    private String mParamTitle;
    private boolean mParamCancelButton;
    private boolean mParamHasEmployee;
    private String mParamEmployeeName;
    private String mParamEmployeeAvatar;

    public interface OnClickIncomingRequestDialog {
        void onPositiveButtonClicked(String date);
        void onNegativeButtonClicked();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }

    public static IncomingRequestDialogFragment newInstance(String message,
                                                            String title,
                                                            boolean cancelButton,
                                                            boolean hasEmployee,
                                                            String employeeName,
                                                            String employeeAvatar) {
        IncomingRequestDialogFragment fragment = new IncomingRequestDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putBoolean(ARG_SHOW_CANCEL_BUTTON, cancelButton);
        args.putBoolean(ARG_HAS_EMPLOYEE, hasEmployee);
        args.putString(ARG_EMPLOYEE_NAME, employeeName);
        args.putString(ARG_EMPLOYEE_AVATAR, employeeAvatar);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamMessage = getArguments().getString(ARG_MESSAGE);
            mParamTitle = getArguments().getString(ARG_TITLE);
            mParamCancelButton = getArguments().getBoolean(ARG_SHOW_CANCEL_BUTTON);
            mParamHasEmployee = getArguments().getBoolean(ARG_HAS_EMPLOYEE);
            mParamEmployeeName = getArguments().getString(ARG_EMPLOYEE_NAME);
            mParamEmployeeAvatar = getArguments().getString(ARG_EMPLOYEE_AVATAR);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity){
            AppCompatActivity activity = (AppCompatActivity)context;
            try {
                mListener = (OnClickIncomingRequestDialog) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }

        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = mParamMessage;
        if(mParamCancelButton){
            message = String.format(getResources().getString(R.string.incoming_request_dialog), mParamMessage);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mParamTitle)
                .setMessage(message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onPositiveButtonClicked(mParamCancelButton ? mParamMessage : null);
                    }
                });
                if(mParamCancelButton){
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            mListener.onNegativeButtonClicked();
                        }
                    });
                }
        if(mParamHasEmployee){
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.employee_info_in_dialog, null);
            TextView employeeName = (TextView)view.findViewById(R.id.employee_info_name);
            ImageView employeeAvatar = (ImageView)view.findViewById(R.id.employee_info_avatar);
            if(mParamEmployeeAvatar != null && mParamEmployeeName != null){
                employeeName.setText(mParamEmployeeName);
                Picasso.with(getActivity()).load(mParamEmployeeAvatar).into(employeeAvatar);
            } else{
                employeeName.setVisibility(View.GONE);
                employeeAvatar.setVisibility(View.GONE);
            }

            builder.setView(view);
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
