package com.example.quickowner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.Dash;

public class MainActivity extends AppCompatActivity {

    int currentTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Fragment fragment = (Fragment) DashboardFragment.class.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.mainFragment, fragment).commit();
            currentTab=1;
        }catch(Exception e){
            e.printStackTrace();
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {



        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            Class fragmentClass = null;
            switch (item.getItemId()) {
                case R.id.navigation_trends:
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


                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainFragment, fragment).commit();
                return true;
            }
            return false;
        }
    };


}
