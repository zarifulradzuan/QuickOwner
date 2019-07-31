package com.example.quickowner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.example.quickowner.controller.PlaceController;
import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class TrendFragment extends Fragment {
    private BarChart trendChart;
    private ProgressBar progressBar;
    private PlaceController placeController;
    private Switch modeSwitch;
    private TrendFragment self;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        self = this;
        //TODO
        //Get place id from shared preferences

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String placeId = firebaseAuth.getUid();
        View rootView = inflater.inflate(R.layout.fragment_trend, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        trendChart = rootView.findViewById(R.id.trendChart);
        modeSwitch = rootView.findViewById(R.id.modeSwitch);

        placeController = new PlaceController();

        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                progressBar.setVisibility(View.VISIBLE);
                if (isChecked) {
                    displayChart(placeId, PlaceController.MODE_WEEKLY);
                    modeSwitch.setText(getString(R.string.mode_weekly));
                } else {
                    displayChart(placeId, PlaceController.MODE_DAILY);
                    modeSwitch.setText(Objects.requireNonNull(getContext()).getString(R.string.mode_daily));
                }
            }
        });


        displayChart(placeId, PlaceController.MODE_DAILY);

        return rootView;
    }

    private void displayChart(final String placeId, final int mode) {
        progressBar.setVisibility(View.INVISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                placeController.getTrendData(placeId, mode, trendChart, self);
            }
        };
        thread.run();
    }
}
