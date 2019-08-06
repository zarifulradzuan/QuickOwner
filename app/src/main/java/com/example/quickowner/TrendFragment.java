package com.example.quickowner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.quickowner.controller.PlaceController;
import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.auth.FirebaseAuth;


public class TrendFragment extends Fragment {
    private BarChart trendChart;
    private ProgressBar progressBar;
    private PlaceController placeController;
    private Spinner modeSpinnger;
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
        progressBar = rootView.findViewById(R.id.trendProgress);
        progressBar.setVisibility(View.VISIBLE);
        trendChart = rootView.findViewById(R.id.trendChart);
        modeSpinnger = rootView.findViewById(R.id.modeSpinner);
        modeSpinnger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                if (position == 0) {
                    displayChart(placeId, PlaceController.MODE_DAILY);
                } else {
                    displayChart(placeId, PlaceController.MODE_WEEKLY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                modeSpinnger.setSelection(0);
            }
        });
        placeController = new PlaceController();


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
