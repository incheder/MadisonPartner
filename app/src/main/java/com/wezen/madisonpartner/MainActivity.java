package com.wezen.madisonpartner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.wezen.madisonpartner.account.AccountActivity;
import com.wezen.madisonpartner.home.ViewPagerAdapter;
import com.wezen.madisonpartner.information.InformationFragment;
import com.wezen.madisonpartner.information.SelectImageDialogFragment;
import com.wezen.madisonpartner.login.LoginActivity;
import com.wezen.madisonpartner.password.PasswordActivity;
import com.wezen.madisonpartner.request.RequestListFragment;
import com.wezen.madisonpartner.utils.DialogActivity;

import java.text.DecimalFormat;

public class MainActivity extends DialogActivity implements SelectImageDialogFragment.OnClickSelectImageDialogListener, InformationFragment.OnBusinessInformationReadyListener {

    private NavigationView navigationView;
    private String userName;
    private String userEmail;
    private String imageUrl;
    private DrawerLayout drawerLayout;
    private InformationFragment infoFragment;
    private SharedPreferences sharedPref;
    private TextView ratingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener( navigationItemSelectedListener );

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        fillNavigationViewHeader();
        saveInstallationData();

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(RequestListFragment.newInstance("",""), getResources().getString(R.string.home_orders));
        if(ParseUser.getCurrentUser().getInt("userType") == 2){
            infoFragment = InformationFragment.newInstance("", "");
            adapter.addFrag(infoFragment, getResources().getString(R.string.home_info));
        }
        viewPager.setAdapter(adapter);
    }

    private void fillNavigationViewHeader(){
        ImageView imageAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        TextView textViewUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        ratingTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navigationViewRating);
        ParseUser user = ParseUser.getCurrentUser();
        userName = user.getUsername();
        userEmail = user.getEmail();
        textViewUsername.setText(userName);
        //ratingTextView.setText(userEmail);
        if(user.getParseFile("userImage")!= null){
            imageUrl = user.getParseFile("userImage").getUrl();
            Picasso.with(this).load(imageUrl).into(imageAvatar);
        }

    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            Intent toLaunch = null;
            int id = menuItem.getItemId();

            if(id == R.id.menu_account){

                toLaunch = new Intent(MainActivity.this, AccountActivity.class);
                toLaunch.putExtra(AccountActivity.USERNAME,userName);
                toLaunch.putExtra(AccountActivity.EMAIL,userEmail);
                toLaunch.putExtra(AccountActivity.IMAGE_URL,imageUrl);
                //toLaunch.putExtra(AccountActivity.LASTNAME,userLastName);
                //toLaunch.putExtra(AccountActivity.PHONE,phone);
                startActivity(toLaunch);

            } else if (id == R.id.menu_password){
                toLaunch = new Intent(MainActivity.this, PasswordActivity.class);

            } /*else if (id == R.id.menu_help){

            }*/ else if (id == R.id.menu_logout){
                ParseUser.logOut();
                updateSharedPref(R.string.installation_already_saved, 0);
                ParseInstallation.getCurrentInstallation().remove("channels");
                ParseInstallation.getCurrentInstallation().saveInBackground();
                goToLogin();
            }

            menuItem.setChecked(false);
            drawerLayout.closeDrawers();
            if(toLaunch != null){
                //menuItem.setChecked(true);
                startActivity(toLaunch);
            }


            return true;
        }
    };

    private void goToLogin(){
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }


    @Override
    public void onCameraClicked() {
        Log.d("CLICK", "CLICK");
    }

    @Override
    public void onGalleryClicked() {
        Log.d("CLICK","CLICK");
        infoFragment.launchGallery();

    }

    private void saveInstallationData(){
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        int isSaved = sharedPref.getInt(getString(R.string.installation_already_saved), 0);
        if(isSaved == 0){
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();

            //ParseObject hs = ParseObject.createWithoutData("HomeServices",homeService.getObjectId());
            installation.put("user", ParseUser.getCurrentUser());
            installation.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Log.d("SUCCESS", "installation saved");
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.installation_already_saved), 1);
                        editor.apply();
                    } else {
                        Log.e("ERROR", "installation not saved: "+ e.getMessage());
                    }
                }
            });

        }

    }

    private void updateSharedPref(int key, int value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(key), value);
        editor.apply();
    }

    @Override
    public void seRating(Double rating) {
        ratingTextView.setText(String.valueOf(  new DecimalFormat("#.#").format(rating) ));
    }
}
