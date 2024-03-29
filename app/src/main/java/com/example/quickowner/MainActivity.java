package com.example.quickowner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final int EDIT_PLACE = 0;
    private static final int LOGIN = 1;
    int currentTab;

    private FirebaseAuth firebaseAuth;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    {
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {


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
                        if (currentTab == 1)
                            return false;
                        currentTab = 1;
                        fragmentClass = DashboardFragment.class;
                        break;
                    case R.id.navigation_notifications:
                        if (currentTab == 2)
                            return false;
                        currentTab = 2;
                        fragmentClass = MessageFragment.class;
                        break;
                }
                if (fragmentClass != null) {
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException ee) {
                        ee.printStackTrace();
                    }
                    final Fragment finalFragment = fragment;
                    Thread changeFragment = new Thread() {
                        @Override
                        public void run() {
                            FragmentManager fragmentManager;
                            fragmentManager = getSupportFragmentManager();
                            assert finalFragment != null;
                            fragmentManager.beginTransaction().replace(R.id.mainFragment, finalFragment).commit();
                        }
                    };
                    changeFragment.run();
                    return true;
                }
                return false;
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Fragment fragment = DashboardFragment.class.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.mainFragment, fragment).commit();
            currentTab = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED && requestCode == LOGIN) {
            finish();
        } else {
            onStart();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logOutMenu) {
            firebaseAuth.signOut();
            finish();
            startActivity(getIntent());
            return true;
        }
        if (id == R.id.editPlace) {
            Intent intent = new Intent(this, EditPlaceActivity.class);
            startActivityForResult(intent, EDIT_PLACE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
