package com.wezen.madisonpartner.request.dialogs;

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
public class ConfirmDialogFragment extends DialogFragment {

    private static final String TAG = ConfirmDialogFragment.class.getSimpleName();
    private static final String ARG_IS_CANCEL = TAG+"_isCancel";
    private OnClickConfirmDialog mListener;
    private boolean mParamIsCancel;

    public interface OnClickConfirmDialog {
        void onConfirmPositiveButtonClicked(boolean isCancel);
    }

    public static ConfirmDialogFragment newInstance(boolean isCancel) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_CANCEL, isCancel);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamIsCancel = getArguments().getBoolean(ARG_IS_CANCEL);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity){
            AppCompatActivity activity = (AppCompatActivity)context;
            try {
                mListener = (OnClickConfirmDialog) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnClickConfirmDialog");
            }

        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getResources().getString(R.string.confirm_end_service_message);
        if(mParamIsCancel){
            message = getResources().getString(R.string.confirm_cancel_service_message);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_title)
                .setMessage(message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onConfirmPositiveButtonClicked(mParamIsCancel);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
