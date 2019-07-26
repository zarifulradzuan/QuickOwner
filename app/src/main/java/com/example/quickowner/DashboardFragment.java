package com.example.quickowner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.quickowner.controller.PlaceController;
import com.example.quickowner.model.Place;

public class DashboardFragment extends Fragment {
    private TextView dshPlaceName, dshOpenStatus, dshLastUpdated, dshPlaceCurrMax, dshPercentage, dshOverrideStatus;
    private RadioButton dshOpen, dshClose, dshClear;
    private RadioGroup dshOverrideGroup;
    private ProgressBar placeFullness;
    private Place place;
    private View rootView;
    private boolean updating;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        updating = false;
        dshLastUpdated = rootView.findViewById(R.id.dshLastUpdated);
        dshOpenStatus = rootView.findViewById(R.id.dshOpenStatus);
        dshPlaceCurrMax = rootView.findViewById(R.id.dshCurrMax);
        dshPlaceName = rootView.findViewById(R.id.dshPlaceName);
        dshPercentage = rootView.findViewById(R.id.dshPercentage);
        placeFullness = rootView.findViewById(R.id.progressBar);
        dshOverrideStatus = rootView.findViewById(R.id.dshOverrideStatus);

        dshOpen = rootView.findViewById(R.id.dshOverrideOpen);
        dshClear = rootView.findViewById(R.id.dshOverrideClear);
        dshClose = rootView.findViewById(R.id.dshOverrideClose);

        dshOverrideGroup = rootView.findViewById(R.id.dshOverrideGroup);
        dshOverrideGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(!updating){
                    if(checkedId == R.id.dshOverrideClear){
                        place.setOverrideStatus(-1);
                        PlaceController.insertPlace(place);
                    }
                    else if(checkedId == R.id.dshOverrideOpen){
                        place.setOverrideStatus(1);
                        PlaceController.insertPlace(place);
                    }
                    else if(checkedId == R.id.dshOverrideClose){
                        place.setOverrideStatus(0);
                        PlaceController.insertPlace(place);
                    }
                }
            }
        });
        PlaceController.getPlace("mcdonald-mitc", this);
        SharedPreferences.Editor sharedPreferences = getActivity().getSharedPreferences("quickowner",Context.MODE_PRIVATE).edit();
        sharedPreferences.putString("placeid","mcdonald-mitc");
        sharedPreferences.commit();
        rootView.setVisibility(View.INVISIBLE);
        return rootView;
    }

    public void updatePlace(Place place){
        try {
            this.place = place;
            updating = true;
            dshPlaceCurrMax.setText("Capacity currently at: " + place.getCurrentOccupancy() + "/" + place.getMaxOccupancy());
            dshPlaceName.setText(place.getPlaceName());
            dshLastUpdated.setText("Last updated on: " + place.getLastUpdated());

            if (place.getOverrideStatus() == -1) {
                dshOverrideStatus.setText("Override is not applied");
                dshClear.setChecked(true);
            } else {
                dshOverrideStatus.setText("Override is applied");
                if (place.getOverrideStatus() == 0)
                    dshClose.setChecked(true);
                else
                    dshOpen.setChecked(true);
            }


            PlaceController placeController = new PlaceController(place);
            if (placeController.isOpen()) {
                dshOpenStatus.setText("OPEN");
                dshOpenStatus.setTextColor(getResources().getColor(R.color.occupancyGreen));
            } else {
                dshOpenStatus.setText("CLOSED");
                dshOpenStatus.setTextColor(getResources().getColor(R.color.occupancyRed));
            }
            dshPercentage.setText(placeController.getFullness() + "%");
            placeFullness.setProgress(placeController.getFullness());
            if (placeController.getFullness() < 50)
                placeFullness.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.occupancyGreen)));
            else if (placeController.getFullness() < 85)
                placeFullness.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.occupancyYellow)));
            else
                placeFullness.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.occupancyRed)));
        }catch (Exception e){
            e.printStackTrace();
            onDestroy();
        }
        rootView.setVisibility(View.VISIBLE);
        updating=false;
    }
}
