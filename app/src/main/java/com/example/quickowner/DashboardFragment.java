package com.example.quickowner;

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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class DashboardFragment extends Fragment implements PlaceController.PlaceListener {
    private TextView dshPlaceName, dshOpenStatus, dshLastUpdated, dshPlaceCurrMax, dshPercentage, dshOverrideStatus;
    private RadioButton dshOpen, dshClose, dshClear;
    private ProgressBar placeFullness;
    private Place place;
    private View rootView;
    private boolean updating;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        RadioGroup dshOverrideGroup;
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
        PlaceController placeController = new PlaceController();
        placeController.setPlaceListener(this);
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            placeController.getPlace(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        } catch (Exception e) {
            e.printStackTrace();
        }
        rootView.setVisibility(View.INVISIBLE);
        return rootView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void returnPlace(Place place) {
        try {
            this.place = place;
            updating = true;
            dshPlaceCurrMax.setText(String.format(Objects.requireNonNull(getContext()).getString(R.string.capacity_format), place.getCurrentOccupancy(), place.getMaxOccupancy()));
            dshPlaceName.setText(place.getPlaceName());
            dshLastUpdated.setText(String.format("%s%s", getContext().getString(R.string.last_updated_on), place.getLastUpdated()));

            if (place.getOverrideStatus() == -1) {
                dshOverrideStatus.setText(getContext().getString(R.string.override_not_applied));
                dshClear.setChecked(true);
            } else {
                dshOverrideStatus.setText(getContext().getString(R.string.override_is_applied));
                if (place.getOverrideStatus() == 0)
                    dshClose.setChecked(true);
                else
                    dshOpen.setChecked(true);
            }


            PlaceController placeController = new PlaceController(place);
            if (placeController.isOpen()) {
                dshOpenStatus.setText(getString(R.string.open_cap));
                dshOpenStatus.setTextColor(getResources().getColor(R.color.occupancyGreen));
            } else {
                dshOpenStatus.setText(getString(R.string.closed_cap));
                dshOpenStatus.setTextColor(getResources().getColor(R.color.occupancyRed));
            }
            dshPercentage.setText(String.format(getString(R.string.percentage_format), placeController.getFullness()));
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
