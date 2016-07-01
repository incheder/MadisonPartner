package com.wezen.madisonpartner.password;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.utils.DialogActivity;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class PasswordActivity extends DialogActivity {

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
                    changePassword(parseUser);
                    //old password is correct
                    /*parseUser.setPassword(editTextNewPassword.getText().toString());
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e1) {
                            if (e1 == null) {
                                ParseUser.getCurrentUser().fetchInBackground();
                                Toast.makeText(PasswordActivity.this,R.string.password_updated,Toast.LENGTH_SHORT).show();
                                finish();
                            } else {//ups
                                Toast.makeText(PasswordActivity.this, R.string.error_while_updating_password, Toast.LENGTH_LONG).show();
                            }

                        }
                    });*/
                } else {//ups
                    if(e.getCode() == 101){
                        Toast.makeText(PasswordActivity.this,R.string.invalid_current_password,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void changePassword(ParseUser parseUser){
        parseUser.setPassword(editTextNewPassword.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e1) {
                if (e1 == null) {
                    //ParseUser.getCurrentUser().fetchInBackground();
                    ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), editTextNewPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if(e==null){
                                Toast.makeText(PasswordActivity.this,R.string.password_updated,Toast.LENGTH_SHORT).show();
                                ParseUser.getCurrentUser().fetchInBackground();
                                //finish();
                            } else {
                                Toast.makeText(PasswordActivity.this,R.string.error_while_login,Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {//ups
                    Toast.makeText(PasswordActivity.this, R.string.error_while_updating_password, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
