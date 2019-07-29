package com.example.quickowner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    String username;
    private ProgressBar progressBar;
    int currentTab;
    String placeId;
    SharedPreferences sharedPreferences;
    private int LOGIN = 1;
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {



        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;
            Class fragmentClass = null;
            switch (item.getItemId()) {
                case R.id.navigation_trends:
                    if (currentTab == 0)
                        return false;
                    currentTab = 0;
                    fragmentClass = TrendFragment.class;
                    break;
                case R.id.navigation_dashboard:
                    if (currentTab==1)
                        return false;
                    currentTab=1;
                    fragmentClass = DashboardFragment.class;
                    break;
                case R.id.navigation_notifications:
                    if (currentTab==2)
                        return false;
                    currentTab=2;
                    fragmentClass = MessageFragment.class;
                    break;
            }
            if(fragmentClass!=null){
                try{
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException e){
                    e.printStackTrace();
                } catch (IllegalAccessException ee){
                    ee.printStackTrace();
                }
                final Fragment finalFragment = fragment;
                Thread changeFragment = new Thread() {
                    @Override
                    public void run() {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.mainFragment, finalFragment).commit();
                    }
                };
                changeFragment.run();
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("QuickOwner", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();


        super.onCreate(savedInstanceState);
        progressBar = findViewById(R.id.progressBar);
        setContentView(R.layout.activity_main);
        try {
            Fragment fragment = (Fragment) DashboardFragment.class.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.mainFragment, fragment).commit();
            currentTab = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == LOGIN) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (firebaseAuth.getCurrentUser() != null) {
                    editor.putString("email", firebaseAuth.getCurrentUser().getEmail());

                }
            }
        }
    }


}
