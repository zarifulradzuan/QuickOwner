package com.example.quickowner;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends Activity implements MapDialog.PremiseLocationInterface, ReverseGeocoder.ReverseGeocodeListener {
    Button registerButton;
    FirebaseAuth mAuth;
    EditText email;
    EditText password;
    EditText name;
    EditText address;
    Button openMap;
    Location premiseLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.emailRegister);
        password = findViewById(R.id.password);
        name = findViewById(R.id.nameRegister);
        address = findViewById(R.id.addressRegister);
        openMap = findViewById(R.id.openMap);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName("USERNAME")
                                            .build();
                                    assert user != null;
                                    String uid = user.getUid();

                                    Intent intent = new Intent();
                                    intent.putExtra("user", user);
                                    setResult(RESULT_OK);

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


    @Override
    public void setLocationOfPremise(Location locationOfPremise) {
        premiseLocation = locationOfPremise;
        ReverseGeocoder.reverseGeoCode(this, locationOfPremise);
    }

    @Override
    public void returnAddress(String address) {
        if (!address.equals("")) {
            this.address.setText(address);
            this.address.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        }
    }
}
