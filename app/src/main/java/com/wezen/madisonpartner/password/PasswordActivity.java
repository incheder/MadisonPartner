package com.wezen.madisonpartner.password;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.jakewharton.rxbinding.view.RxView;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wezen.madisonpartner.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class PasswordActivity extends AppCompatActivity {

    @Bind(R.id.oldPassword)
    EditText editTextOldPassword;
    @Bind(R.id.newPassword)
    EditText editTextNewPassword;
    @Bind(R.id.repeatPassword)
    EditText editTextRepeatPassword;
    @Bind(R.id.fabPassword)
    FloatingActionButton fab;
    @Bind(R.id.changePasswordToolbar)
    Toolbar toolbar;
    @BindString(R.string.error_field_required)
    String errorFieldRequired;
    @BindString(R.string.passwords_must_been_the_same)
    String errorPasswordsAreDifferent;
    @BindString(R.string.you_can_not_use_the_same_password)
    String errorDontUseOldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        RxView.clicks(fab).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (PasswordActivity.this.validateLocalFields()) {
                    PasswordActivity.this.validateInBackground();
                }
            }
        });
    }


    private boolean validateLocalFields(){
        if(TextUtils.isEmpty(editTextOldPassword.getText().toString())){
            editTextOldPassword.requestFocus();
            editTextOldPassword.setError(errorFieldRequired);
            return false;
        }
        if(TextUtils.isEmpty(editTextNewPassword.getText().toString())){
            editTextNewPassword.requestFocus();
            editTextNewPassword.setError(errorFieldRequired);
            return false;
        }
        if(TextUtils.isEmpty(editTextRepeatPassword.getText().toString())){
            editTextRepeatPassword.requestFocus();
            editTextRepeatPassword.setError(errorFieldRequired);
            return false;
        }
        if(!editTextNewPassword.getText().toString().equals(editTextRepeatPassword.getText().toString())){
            editTextRepeatPassword.requestFocus();
            editTextRepeatPassword.setError(errorPasswordsAreDifferent);
            return false;
        }
        if(editTextNewPassword.getText().toString().equals(editTextOldPassword.getText().toString())){
            editTextNewPassword.requestFocus();
            editTextNewPassword.setError(errorDontUseOldPassword);
        }



        return true;
    }


    private void validateInBackground(){
        ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), editTextOldPassword.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    //old password is correct
                    parseUser.setPassword(editTextNewPassword.getText().toString());
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e1) {
                            if (e1 == null) {
                                ParseUser.getCurrentUser().fetchInBackground();
                            } else {//ups

                            }

                        }
                    });


                } else {//ups

                }
            }
        });
    }

}
