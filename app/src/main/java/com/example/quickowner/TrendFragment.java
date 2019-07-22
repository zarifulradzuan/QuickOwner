package com.example.quickowner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;


public class TrendFragment extends Fragment {
    private BarChart trendChart;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trend, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        trendChart = rootView.findViewById(R.id.trendChart);

        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 7; i++)
            entries.add(new BarEntry(i, i * 10));

        BarDataSet barDataSet = new BarDataSet(entries, "Trend over the past week");
        BarData barData = new BarData(barDataSet);
        trendChart.setData(barData);
        trendChart.invalidate();
        return rootView;
    }
}
