package com.wezen.madisonpartner.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.utils.DialogActivity;
import com.wezen.madisonpartner.utils.ImagePicker;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends DialogActivity {
    public static final String USERNAME = "username";
    public static final String IMAGE_URL = "url";
    public static final String EMAIL = "email";
    public static final String LASTNAME = "lastName";
    public static final String PHONE = "phone";

    private String userName;
    private String imageUrl;
    private String userEmail;
    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView avatar;
    private Bitmap avatarBitmap;
    private EditText lastNameEditText;
    private EditText phoneEditText;
    private EditText accountName;
    private EditText accountEmail;
    private String lastName;
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar)findViewById(R.id.accountToolbar);
        setSupportActionBar(toolbar);
        avatar = (CircleImageView)findViewById(R.id.account_image);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(AccountActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_REQUEST);
            }
        });
        lastNameEditText = (EditText)findViewById(R.id.accountLastNameEditText);
        phoneEditText = (EditText)findViewById(R.id.accountPhoneEditText);
        accountName = (EditText)findViewById(R.id.accountNameEditText);
        accountEmail = (EditText)findViewById(R.id.accountEmailEditText);


        Button save = (Button)findViewById(R.id.accountSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseUser user = ParseUser.getCurrentUser();
                if(!TextUtils.isEmpty(lastNameEditText.getText())){
                    user.put("lastName",lastNameEditText.getText().toString());
                }
                if(!TextUtils.isEmpty(phoneEditText.getText())){
                    user.put("phone",phoneEditText.getText().toString());
                }
                if(!TextUtils.isEmpty(accountName.getText())){
                    user.put("userName",accountName.getText().toString());
                }
                if(!TextUtils.isEmpty(accountEmail.getText())){
                    user.put("email", accountEmail.getText().toString());
                }
                if(avatarBitmap!= null){
                    final ParseFile pf = new ParseFile(user.getObjectId()+ "_avatar_image.jpeg",bitmapToByteArray(avatarBitmap));
                    pf.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                user.put("userImage",pf);
                            }
                            saveParseUSer(user);
                        }
                    });
                } else {
                    saveParseUSer(user);
                }

            }
        });

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(getIntent().getExtras()!= null){
            userName = getIntent().getStringExtra(USERNAME);
            userEmail = getIntent().getStringExtra(EMAIL);
            imageUrl = getIntent().getStringExtra(IMAGE_URL);
            lastName = getIntent().getStringExtra(LASTNAME);
            phone = getIntent().getStringExtra(PHONE);
        }

        getUserInfo();
    }

    private void getUserInfo() {


        accountName.setText(userName);
        accountEmail.setText(userEmail);
        lastNameEditText.setText(lastName);
        phoneEditText.setText(phone);
        if(imageUrl!= null){
            Picasso.with(this).load(imageUrl).into(avatar);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PICK_IMAGE_REQUEST:
                avatarBitmap = ImagePicker.getImageFromResult(AccountActivity.this, resultCode, data);
                // TODO use bitmap
                avatar.setImageBitmap(avatarBitmap);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private byte[] bitmapToByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void saveParseUSer(ParseUser user){
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                        ParseUser.getCurrentUser().fetchInBackground();
                } else { //ups

                }
            }
        });
    }

}
