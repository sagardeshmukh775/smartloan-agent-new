package com.smartloan.smtrick.smart_loan.view.activites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.smartloan.smtrick.smart_loan.R;
import com.smartloan.smtrick.smart_loan.exception.ExceptionUtil;
import com.smartloan.smtrick.smart_loan.interfaces.OnFragmentInteractionListener;
import com.smartloan.smtrick.smart_loan.preferences.AppSharedPreference;
import com.smartloan.smtrick.smart_loan.utilities.Utility;
import com.smartloan.smtrick.smart_loan.view.fragements.Fragment_Calculator;
import com.smartloan.smtrick.smart_loan.view.fragements.Fragment_GenerateLeads;
import com.smartloan.smtrick.smart_loan.view.fragements.Fragment_LeadsActivity;
import com.smartloan.smtrick.smart_loan.view.fragements.Fragment_Reports;
import com.smartloan.smtrick.smart_loan.view.fragements.InvoicesTabFragment;
import com.squareup.picasso.Picasso;

import static com.smartloan.smtrick.smart_loan.constants.Constant.REQUEST_CODE;
import static com.smartloan.smtrick.smart_loan.constants.Constant.RESULT_CODE;

public class MainActivity extends AppCompatActivity
        implements OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    private AppSharedPreference appSharedPreference;
    private NavigationView navigationView;
    private Fragment selectedFragement;
    ImageUploadReceiver imageUploadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appSharedPreference = new AppSharedPreference(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // NOTE : Just remove the fab button
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //NOTE:  Checks first item in the navigation drawer initially
        navigationView.setCheckedItem(R.id.generateleads);
        updateNavigationHeader();
        //NOTE:  Open fragment1 initially.
        selectedFragement = new Fragment_GenerateLeads();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, selectedFragement);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        //NOTE: creating fragment object
        Fragment fragment = null;
        if (id == R.id.generateleads) {
            fragment = new Fragment_GenerateLeads();
        } else if (id == R.id.Leads) {
            fragment = new Fragment_LeadsActivity();
        } else if (id == R.id.Invices) {
            fragment = new InvoicesTabFragment();
        } else if (id == R.id.Reports) {
            fragment = new Fragment_Reports();
        } else if (id == R.id.Calulator) {
            fragment = new Fragment_Calculator();
        } else if (id == R.id.item_logout) {
            clearDataWithSignOut();
        }
        //NOTE: Fragment changing code
        selectedFragement = fragment;
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }
        //NOTE:  Closing the drawer after selecting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //Ya you can also globalize this variable :P
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String title) {
        // NOTE:  Code to replace the toolbar title based current visible fragment
        getSupportActionBar().setTitle(title);
    }

    private void clearDataWithSignOut() {
        FirebaseAuth.getInstance().signOut();
        appSharedPreference.clear();
        logOut();
    }

    private void logOut() {
        Intent intent = new Intent(this, LoginScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void updateNavigationHeader() {
        try {
            View header = navigationView.getHeaderView(0);
            TextView textViewAgentId = (TextView) header.findViewById(R.id.textView_agent_id);
            TextView textViewUserName = (TextView) header.findViewById(R.id.textView_user_name);
            TextView textViewEmailId = (TextView) header.findViewById(R.id.text_view_email);
            TextView textViewMobileNumber = (TextView) header.findViewById(R.id.textView_contact);
            ImageView imageViewProfile = (ImageView) header.findViewById(R.id.image_view_profile);
            textViewUserName.setText(appSharedPreference.getUserName());
            textViewEmailId.setText(appSharedPreference.getEmaiId());
            textViewAgentId.setText(appSharedPreference.getAgeniId());
            textViewMobileNumber.setText(appSharedPreference.getMobileNo());
            if (!Utility.isEmptyOrNull(appSharedPreference.getProfileLargeImage())) {
                Picasso.with(this).load(appSharedPreference.getProfileLargeImage()).resize(200, 200).centerCrop().placeholder(R.drawable.imagelogo).into(imageViewProfile);
            } else
                imageViewProfile.setImageResource(R.drawable.imagelogo);

            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, UpdateProfileActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        } catch (Exception ex) {
            ExceptionUtil.logException(ex);
        }
    }

    @Override
    public void changeFragement(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
            updateNavigationHeader();
        } else if (selectedFragement != null)
            selectedFragement.onActivityResult(requestCode, resultCode, data);
    }

    private void setReceiver() {
        try {
            IntentFilter filter = new IntentFilter(ImageUploadReceiver.PROCESS_RESPONSE);
            imageUploadReceiver = new ImageUploadReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(imageUploadReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        setReceiver();
        super.onStart();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(imageUploadReceiver);
        super.onStop();
    }

    public class ImageUploadReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "com.smartloan.smtrick.smart_loan.intent.action.UPDATE_USER_DATA";

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNavigationHeader();
        }
    }
}
