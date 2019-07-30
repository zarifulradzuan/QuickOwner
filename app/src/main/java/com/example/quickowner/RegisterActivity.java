package com.example.quickowner;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements MapDialog.PremiseLocationInterface, ReverseGeocoder.ReverseGeocodeListener {
    Button registerButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    //Owner info
    EditText email;
    EditText password;
    EditText name;

    //Premise info
    EditText premiseName;
    EditText address;
    Button openMap;
    LatLng premiseLocation;
    EditText maxOccupancy;
    Place placeToCreate;

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
        setContentView(R.layout.activity_register);
        progressBar = findViewById(R.id.loadingRegister);

        //Owner
        email = findViewById(R.id.emailRegister);
        password = findViewById(R.id.passwordRegister);
        name = findViewById(R.id.nameRegister);

        //Premise
        address = findViewById(R.id.addressRegister);
        maxOccupancy = findViewById(R.id.maxOccupancyRegister);
        openMap = findViewById(R.id.openMap);
        premiseName = findViewById(R.id.premiseNameRegister);

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
        openingMonday = findViewById(R.id.openMondayRegister);
        closingMonday = findViewById(R.id.closeMondayRegister);

        openingTuesday = findViewById(R.id.openTuesdayRegister);
        closingTuesday = findViewById(R.id.closeTuesdayRegister);

        openingWednesday = findViewById(R.id.openWednesdayRegister);
        closingWednesday = findViewById(R.id.closeWednesdayRegister);

        openingThursday = findViewById(R.id.openThursdayRegister);
        closingThursday = findViewById(R.id.closeThursdayRegister);

        openingFriday = findViewById(R.id.openFridayRegister);
        closingFriday = findViewById(R.id.closeFridayRegister);

        openingSaturday = findViewById(R.id.openSaturdayRegister);
        closingSaturday = findViewById(R.id.closeSaturdayRegister);

        openingSunday = findViewById(R.id.openSundayRegister);
        closingSunday = findViewById(R.id.closeSundayRegister);

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


        for (final TextView a : openingHoursTextView) {
            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(RegisterActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    a.setText(selectedHour + ":" + selectedMinute);
                                }
                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Select Time");
                            mTimePicker.show();
                        }
                    };
                    thread.run();
                }
            });
        }

        //Switch
        openingHoursSwitch = new ArrayList<>();
        mondaySwitch = findViewById(R.id.switchMondayRegister);
        tuesdaySwitch = findViewById(R.id.switchTuesdayRegister);
        wednesdaySwitch = findViewById(R.id.switchWednesdayRegister);
        thursdaySwitch = findViewById(R.id.switchThursdayRegister);
        fridaySwitch = findViewById(R.id.switchFridayRegister);
        saturdaySwitch = findViewById(R.id.switchSaturdayRegister);
        sundaySwitch = findViewById(R.id.switchSundayRegister);

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
        registerButton = findViewById(R.id.saveRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name.getText().toString())
                                            .build();


                                    String uid = user.getUid();
                                    //TODO
                                    //Implement firebase user+premise
                                    //Get opening hours from textviews
                                    ArrayList<OpeningHours> openingHours = new ArrayList<>();
                                    for (int i = 0; i < (openingHoursTextView.size() / 2) - 1; i += 2) {
                                        System.out.println(i + " " + (openingHoursTextView.size() / 2));
                                        OpeningHours openingHour = new OpeningHours();
                                        if (!openingHoursSwitch.get(i).isChecked()) {
                                            if (openingHoursTextView.get(i).getText().toString().equals(getString(R.string.opening_time)))
                                                openingHour.setOpening("");
                                            else
                                                openingHour.setOpening(openingHoursTextView.get(i).getText().toString());
                                            if (openingHoursTextView.get(i + 1).getText().toString().equals(getString(R.string.closing_time)))
                                                openingHour.setClosing("");
                                            else
                                                openingHour.setClosing(openingHoursTextView.get(i + 1).getText().toString());
                                        } else {
                                            openingHour.setOpening("00:00");
                                            openingHour.setClosing("00:00");
                                        }
                                        openingHours.add(openingHour);
                                    }

                                    placeToCreate = new Place();
                                    placeToCreate.setAddress(address.getText().toString());
                                    placeToCreate.setPlaceLatitude(premiseLocation.latitude);
                                    placeToCreate.setPlaceLongitude(premiseLocation.longitude);
                                    placeToCreate.setIdOwner(user.getUid());
                                    placeToCreate.setCurrentOccupancy(0);
                                    placeToCreate.setMaxOccupancy(Integer.parseInt(maxOccupancy.getText().toString()));
                                    placeToCreate.setLastUpdated("00:00");
                                    placeToCreate.setOpeningHours(openingHours);
                                    placeToCreate.setPlaceName(premiseName.getText().toString());
                                    placeToCreate.setOverrideStatus(-1);
                                    placeToCreate.setPlaceId(uid);

                                    PlaceController.insertPlace(placeToCreate);



                                    setResult(RESULT_OK);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    setResult(RESULT_CANCELED);
                                    Toast.makeText(getApplicationContext(), "Register application failed. Please try again, or contact us", Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });
    }

    private void setSwitchListener() {
        mondaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openingHoursTextView.get(0).setEnabled(!isChecked);
                openingHoursTextView.get(0).setText("");
                openingHoursTextView.get(1).setEnabled(!isChecked);
                openingHoursTextView.get(1).setText("");
            }
        });

        tuesdaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openingHoursTextView.get(2).setEnabled(!isChecked);
                openingHoursTextView.get(2).setText("");
                openingHoursTextView.get(3).setEnabled(!isChecked);
                openingHoursTextView.get(3).setText("");
            }
        });

        wednesdaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openingHoursTextView.get(4).setEnabled(!isChecked);
                openingHoursTextView.get(4).setText("");
                openingHoursTextView.get(5).setEnabled(!isChecked);
                openingHoursTextView.get(5).setText("");
            }
        });

        thursdaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openingHoursTextView.get(6).setEnabled(!isChecked);
                openingHoursTextView.get(6).setText("");
                openingHoursTextView.get(7).setEnabled(!isChecked);
                openingHoursTextView.get(7).setText("");
            }
        });

        fridaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openingHoursTextView.get(8).setEnabled(!isChecked);
                openingHoursTextView.get(8).setText("");
                openingHoursTextView.get(9).setEnabled(!isChecked);
                openingHoursTextView.get(9).setText("");
            }
        });

        saturdaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openingHoursTextView.get(10).setEnabled(!isChecked);
                openingHoursTextView.get(10).setText("");
                openingHoursTextView.get(11).setEnabled(!isChecked);
                openingHoursTextView.get(11).setText("");
            }
        });

        sundaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openingHoursTextView.get(12).setEnabled(!isChecked);
                openingHoursTextView.get(12).setText("");
                openingHoursTextView.get(13).setEnabled(!isChecked);
                openingHoursTextView.get(13).setText("");
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
            this.address.setText("Not found");
        }
    }

}
