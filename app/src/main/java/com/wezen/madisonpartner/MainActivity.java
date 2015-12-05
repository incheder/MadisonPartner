package com.wezen.madisonpartner;

import android.content.Intent;
import android.media.Image;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.wezen.madisonpartner.home.DummyFragment;
import com.wezen.madisonpartner.home.ViewPagerAdapter;
import com.wezen.madisonpartner.information.InformationFragment;
import com.wezen.madisonpartner.information.SelectImageDialogFragment;
import com.wezen.madisonpartner.login.LoginActivity;
import com.wezen.madisonpartner.request.RequestListFragment;
import com.wezen.madisonpartner.utils.DialogActivity;

public class MainActivity extends DialogActivity implements SelectImageDialogFragment.OnClickSelectImageDialogListener {

    private NavigationView navigationView;
    private String userName;
    private String userEmail;
    private String imageUrl;
    private DrawerLayout drawerLayout;
    private InformationFragment infoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //navigationView.setNavigationItemSelectedListener( navigationItemSelectedListener );

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

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(RequestListFragment.newInstance("",""), getResources().getString(R.string.home_orders));
        infoFragment = InformationFragment.newInstance("", "");
        adapter.addFrag(infoFragment, getResources().getString(R.string.home_info));

        viewPager.setAdapter(adapter);
    }

    private void fillNavigationViewHeader(){
        ImageView imageAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        TextView textViewUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        TextView textViewEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        ParseUser user = ParseUser.getCurrentUser();
        userName = user.getUsername();
        userEmail = user.getEmail();
        textViewUsername.setText(userName);
        textViewEmail.setText(userEmail);
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

            } else if (id == R.id.menu_history){

            } else if (id == R.id.menu_settings){

            } else if (id == R.id.menu_help){

            } else if (id == R.id.menu_logout){
                ParseUser.logOut();
                goToLogin();
            }
            if(toLaunch != null){
                menuItem.setChecked(true);
                startActivity(toLaunch);
                drawerLayout.closeDrawers();
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
        Log.d("CLICK","CLICK");
    }

    @Override
    public void onGalleryClicked() {
        Log.d("CLICK","CLICK");
        infoFragment.launchGallery();

    }
}
