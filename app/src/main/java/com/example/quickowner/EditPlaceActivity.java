package com.example.quickowner;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.quickowner.controller.PlaceController;
import com.example.quickowner.model.OpeningHours;
import com.example.quickowner.model.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class EditPlaceActivity extends AppCompatActivity implements PlaceController.PlaceListener, MapDialog.PremiseLocationInterface, ReverseGeocoder.ReverseGeocodeListener {
    Button saveButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    PlaceController placeController;
    Place place;
    //Owner info
    EditText email;
    EditText name;

    //Premise info
    EditText premiseName;
    EditText address;
    Button openMap;
    LatLng premiseLocation;
    EditText maxOccupancy;
    Place placeEdited;

    //Opening hours
    //Switch
    Switch mondaySwitch;
    Switch tuesdaySwitch;
    Switch wednesdaySwitch;
    Switch thursdaySwitch;
    Switch fridaySwitch;
    Switch saturdaySwitch;
    Switch sundaySwitch;
    //Opening closing
    TextView openingMonday;
    TextView closingMonday;

    TextView openingTuesday;
    TextView closingTuesday;

    TextView openingWednesday;
    TextView closingWednesday;

    TextView openingThursday;
    TextView closingThursday;

    TextView openingFriday;
    TextView closingFriday;

    TextView openingSaturday;
    TextView closingSaturday;

    TextView openingSunday;
    TextView closingSunday;

    ArrayList<TextView> openingHoursTextView;
    ArrayList<Switch> openingHoursSwitch;
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);
        progressBar = findViewById(R.id.loadingEdit);
        placeController = new PlaceController();
        placeController.setPlaceListener(this);
        //Owner
        email = findViewById(R.id.emailEdit);
        name = findViewById(R.id.nameEdit);

        //Premise
        address = findViewById(R.id.addressEdit);
        maxOccupancy = findViewById(R.id.maxOccupancyEdit);
        openMap = findViewById(R.id.openMap);
        premiseName = findViewById(R.id.premiseNameEdit);

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread openMapDialogThread = new Thread() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        MapDialog mapDialog = new MapDialog();
                        mapDialog.show(getSupportFragmentManager(), "Map Dialog");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                };
                openMapDialogThread.run();

            }
        });

        //Initialise opening times variables
        //Opening closing

        openingHoursTextView = new ArrayList<>();
        openingMonday = findViewById(R.id.openMondayEdit);
        closingMonday = findViewById(R.id.closeMondayEdit);

        openingTuesday = findViewById(R.id.openTuesdayEdit);
        closingTuesday = findViewById(R.id.closeTuesdayEdit);

        openingWednesday = findViewById(R.id.openWednesdayEdit);
        closingWednesday = findViewById(R.id.closeWednesdayEdit);

        openingThursday = findViewById(R.id.openThursdayEdit);
        closingThursday = findViewById(R.id.closeThursdayEdit);

        openingFriday = findViewById(R.id.openFridayEdit);
        closingFriday = findViewById(R.id.closeFridayEdit);

        openingSaturday = findViewById(R.id.openSaturdayEdit);
        closingSaturday = findViewById(R.id.closeSaturdayEdit);

        openingSunday = findViewById(R.id.openSundayEdit);
        closingSunday = findViewById(R.id.closeSundayEdit);

        openingHoursTextView.add(openingMonday);
        openingHoursTextView.add(closingMonday);
        openingHoursTextView.add(openingTuesday);
        openingHoursTextView.add(closingTuesday);
        openingHoursTextView.add(openingWednesday);
        openingHoursTextView.add(closingWednesday);
        openingHoursTextView.add(openingThursday);
        openingHoursTextView.add(closingThursday);
        openingHoursTextView.add(openingFriday);
        openingHoursTextView.add(closingFriday);
        openingHoursTextView.add(openingSaturday);
        openingHoursTextView.add(closingSaturday);
        openingHoursTextView.add(openingSunday);
        openingHoursTextView.add(closingSunday);


        for (final TextView a : openingHoursTextView)
            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            Calendar currentTime = Calendar.getInstance();
                            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = currentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(EditPlaceActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    a.setText(String.format(getString(R.string.timeFormat), selectedHour, selectedMinute));
                                }
                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Select Time");
                            mTimePicker.show();
                        }
                    };
                    thread.run();
                }
            });

        //Switch
        openingHoursSwitch = new ArrayList<>();
        mondaySwitch = findViewById(R.id.switchMondayEdit);
        tuesdaySwitch = findViewById(R.id.switchTuesdayEdit);
        wednesdaySwitch = findViewById(R.id.switchWednesdayEdit);
        thursdaySwitch = findViewById(R.id.switchThursdayEdit);
        fridaySwitch = findViewById(R.id.switchFridayEdit);
        saturdaySwitch = findViewById(R.id.switchSaturdayEdit);
        sundaySwitch = findViewById(R.id.switchSundayEdit);

        openingHoursSwitch.add(mondaySwitch);
        openingHoursSwitch.add(thursdaySwitch);
        openingHoursSwitch.add(tuesdaySwitch);
        openingHoursSwitch.add(wednesdaySwitch);
        openingHoursSwitch.add(fridaySwitch);
        openingHoursSwitch.add(saturdaySwitch);
        openingHoursSwitch.add(sundaySwitch);

        setSwitchListener();

        mAuth = FirebaseAuth.getInstance();

        //Buttons
        saveButton = findViewById(R.id.saveEdit);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    save();
                } else {
                    // If edit fails, display a message to the user.
                    Toast.makeText(getApplicationContext(), "Edited place information successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            placeController.getPlace(mAuth.getUid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save() {
        progressBar.setVisibility(View.VISIBLE);
        String uid;
        FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).getUid();
        uid = user.getUid();
        ArrayList<OpeningHours> openingHours = new ArrayList<>();
        for (int i = 0; i < (openingHoursTextView.size()); i += 2) {
            System.out.println(i + " " + (openingHoursTextView.size() / 2));
            OpeningHours openingHour = new OpeningHours();
            if (!openingHoursSwitch.get(i / 2).isChecked()) {
                if (openingHoursTextView.get(i).getText().toString().equals(getString(R.string.opening_time)))
                    openingHour.setOpening(null);
                else
                    openingHour.setOpening(openingHoursTextView.get(i).getText().toString());
                if (openingHoursTextView.get(i + 1).getText().toString().equals(getString(R.string.closing_time)))
                    openingHour.setClosing(null);
                else
                    openingHour.setClosing(openingHoursTextView.get(i + 1).getText().toString());
            } else {
                openingHour.setOpening("00:00");
                openingHour.setClosing("00:00");
            }
            System.out.println(openingHour);
            openingHours.add(openingHour);
        }

        placeEdited = place;
        placeEdited.setAddress(address.getText().toString());
        placeEdited.setPlaceLatitude(premiseLocation.latitude);
        placeEdited.setPlaceLongitude(premiseLocation.longitude);
        placeEdited.setMaxOccupancy(Integer.parseInt(maxOccupancy.getText().toString()));
        placeEdited.setOpeningHours(openingHours);
        placeEdited.setPlaceName(premiseName.getText().toString());

        PlaceController.insertPlace(placeEdited);


        setResult(RESULT_OK);
        finish();
    }

    private void setSwitchListener() {
        mondaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openingHoursTextView.get(0).setEnabled(false);
                    openingHoursTextView.get(0).setText("");
                    openingHoursTextView.get(1).setEnabled(false);
                    openingHoursTextView.get(1).setText("");
                } else {
                    openingHoursTextView.get(0).setEnabled(true);
                    openingHoursTextView.get(0).setTextColor(Color.BLACK);
                    openingHoursTextView.get(1).setEnabled(true);
                    openingHoursTextView.get(1).setTextColor(Color.BLACK);
                }
            }
        });

        tuesdaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openingHoursTextView.get(2).setEnabled(false);
                    openingHoursTextView.get(2).setText("");
                    openingHoursTextView.get(3).setEnabled(false);
                    openingHoursTextView.get(3).setText("");
                } else {
                    openingHoursTextView.get(2).setEnabled(true);
                    openingHoursTextView.get(2).setTextColor(Color.BLACK);
                    openingHoursTextView.get(3).setEnabled(true);
                    openingHoursTextView.get(3).setTextColor(Color.BLACK);
                }
            }
        });

        wednesdaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openingHoursTextView.get(4).setEnabled(false);
                    openingHoursTextView.get(4).setText("");
                    openingHoursTextView.get(5).setEnabled(false);
                    openingHoursTextView.get(5).setText("");
                } else {
                    openingHoursTextView.get(4).setEnabled(true);
                    openingHoursTextView.get(4).setTextColor(Color.BLACK);
                    openingHoursTextView.get(5).setEnabled(true);
                    openingHoursTextView.get(5).setTextColor(Color.BLACK);
                }
            }
        });

        thursdaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openingHoursTextView.get(6).setEnabled(false);
                    openingHoursTextView.get(6).setText("");
                    openingHoursTextView.get(7).setEnabled(false);
                    openingHoursTextView.get(7).setText("");
                } else {
                    openingHoursTextView.get(6).setEnabled(true);
                    openingHoursTextView.get(6).setTextColor(Color.BLACK);
                    openingHoursTextView.get(7).setEnabled(true);
                    openingHoursTextView.get(7).setTextColor(Color.BLACK);
                }
            }
        });

        fridaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openingHoursTextView.get(8).setEnabled(false);
                    openingHoursTextView.get(8).setText("");
                    openingHoursTextView.get(9).setEnabled(false);
                    openingHoursTextView.get(9).setText("");
                } else {
                    openingHoursTextView.get(8).setEnabled(true);
                    openingHoursTextView.get(8).setTextColor(Color.BLACK);
                    openingHoursTextView.get(9).setEnabled(true);
                    openingHoursTextView.get(9).setTextColor(Color.BLACK);
                }
            }
        });

        saturdaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openingHoursTextView.get(10).setEnabled(false);
                    openingHoursTextView.get(10).setText("");
                    openingHoursTextView.get(11).setEnabled(false);
                    openingHoursTextView.get(11).setText("");
                } else {
                    openingHoursTextView.get(10).setEnabled(true);
                    openingHoursTextView.get(10).setTextColor(Color.BLACK);
                    openingHoursTextView.get(11).setEnabled(true);
                    openingHoursTextView.get(11).setTextColor(Color.BLACK);
                }
            }
        });

        sundaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openingHoursTextView.get(12).setEnabled(false);
                    openingHoursTextView.get(12).setText("");
                    openingHoursTextView.get(13).setEnabled(false);
                    openingHoursTextView.get(13).setText("");
                } else {
                    openingHoursTextView.get(12).setEnabled(true);
                    openingHoursTextView.get(12).setTextColor(Color.BLACK);
                    openingHoursTextView.get(13).setEnabled(true);
                    openingHoursTextView.get(13).setTextColor(Color.BLACK);
                }
            }
        });
    }


    @Override
    public void setLocationOfPremise(LatLng locationOfPremise) {
        premiseLocation = locationOfPremise;
        ReverseGeocoder.reverseGeoCode(this, locationOfPremise);
    }

    @Override
    public void returnAddress(String address) {
        if (!address.equals("")) {
            this.address.setText(address);
        } else {
            this.address.setText("");
        }
    }

    private boolean validate() {
        for (int i = 0; i < (openingHoursTextView.size()); i += 2) {
            //Check if opening equals to default value, but closing is not equal to default value
            if (openingHoursTextView.get(i).getText().toString().equals(getString(R.string.opening_time)) &&
                    !openingHoursTextView.get(i + 1).getText().toString().equals(getString(R.string.closing_time))) {
                Toast.makeText(getApplicationContext(), "Invalid opening and closing time, please check.", Toast.LENGTH_LONG).show();
                return false;
            }
            //Check if closing equals to default value, but opening is not equal to default value
            else if (!openingHoursTextView.get(i).getText().toString().equals(getString(R.string.opening_time)) &&
                    openingHoursTextView.get(i + 1).getText().toString().equals(getString(R.string.closing_time))) {
                Toast.makeText(getApplicationContext(), "Invalid opening and closing time, please check.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (premiseLocation == null) {
            Toast.makeText(getApplicationContext(), "Location not chosen", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }


    @Override
    public void returnPlace(Place place) {
        this.place = place;
        List<OpeningHours> openingHoursList = place.getOpeningHours();
        for (int i = 0; i < openingHoursList.size(); i++) {
            //Check condition closed, therefore default value
            if (openingHoursList.get(i).getOpening().equals(""))
                continue;
            //Check condition 24 hours
            if (openingHoursList.get(i).getOpening().equals(openingHoursList.get(i).getClosing())) {
                System.out.println(i + " " + i / 2 + " " + openingHoursList.size());
                openingHoursSwitch.get(i).setChecked(true);
                continue;
            }
            openingHoursTextView.get(i * 2).setText(openingHoursList.get(i).getOpening());
            openingHoursTextView.get(i * 2 + 1).setText(openingHoursList.get(i).getClosing());
        }

        address.setText(place.getAddress());
        premiseLocation = new LatLng(place.getPlaceLatitude(), place.getPlaceLongitude());
        name.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
        email.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        premiseName.setText(place.getPlaceName());
        maxOccupancy.setText(String.valueOf(place.getMaxOccupancy()));
    }
}
