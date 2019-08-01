package com.example.quickowner;

import android.app.ProgressDialog;
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
    private RadioGroup dshOverrideGroup;
    private View rootView;
    private ProgressDialog progressDialog;
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


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait while data is loaded..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        PlaceController placeController = new PlaceController();
        placeController.setPlaceListener(this);
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            placeController.getPlace(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public void returnPlace(final Place place) {

        Thread placeReturnThread = new Thread() {
            @Override
            public void run() {
                try {
                    updating = true;
                    dshPlaceCurrMax.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dshPlaceCurrMax.setText(String.format(getString(R.string.capacity_format), place.getCurrentOccupancy(), place.getMaxOccupancy()));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dshPlaceName.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dshPlaceName.setText(place.getPlaceName());
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dshLastUpdated.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dshLastUpdated.setText(String.format("%s%s", getString(R.string.last_updated_on), place.getLastUpdated()));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dshOverrideGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (!updating) {
                                if (checkedId == R.id.dshOverrideClear) {
                                    place.setOverrideStatus(-1);
                                    PlaceController.insertPlace(place);
                                } else if (checkedId == R.id.dshOverrideOpen) {
                                    place.setOverrideStatus(1);
                                    PlaceController.insertPlace(place);
                                } else if (checkedId == R.id.dshOverrideClose) {
                                    place.setOverrideStatus(0);
                                    PlaceController.insertPlace(place);
                                }
                            }
                        }
                    });

                    if (place.getOverrideStatus() == -1) {
                        dshOverrideStatus.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dshOverrideStatus.setText(getString(R.string.override_not_applied));
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        dshClear.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dshClear.setChecked(true);
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        dshOverrideStatus.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dshOverrideStatus.setText(getString(R.string.override_is_applied));
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        try {
                            if (place.getOverrideStatus() == 0)
                                dshClose.setChecked(true);
                            else
                                dshOpen.setChecked(true);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }


                    final PlaceController placeController = new PlaceController(place);
                    if (placeController.isOpen()) {
                        dshOpenStatus.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dshOpenStatus.setText(getString(R.string.open_cap));
                                    dshOpenStatus.setTextColor(getResources().getColor(R.color.occupancyGreen));
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        dshOpenStatus.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dshOpenStatus.setText(getString(R.string.closed_cap));
                                    dshOpenStatus.setTextColor(getResources().getColor(R.color.occupancyRed));
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    dshPercentage.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dshPercentage.setText(String.format(getString(R.string.percentage_format), placeController.getFullness()));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    placeFullness.setProgress(placeController.getFullness());
                    if (placeController.getFullness() < 50)
                        placeFullness.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.occupancyGreen)));
                    else if (placeController.getFullness() < 85)
                        placeFullness.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.occupancyYellow)));
                    else
                        placeFullness.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.occupancyRed)));

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    onDestroy();
                }
                progressDialog.dismiss();
                updating = false;
            }
        };
        placeReturnThread.start();

    }
}
