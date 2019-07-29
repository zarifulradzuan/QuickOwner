package com.example.quickowner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quickowner.controller.UserController;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity implements UserController.LoginResponseListener {
    private int REGISTER = 2;
    private EditText username;
    private EditText password;
    private Button loginButton;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REGISTER);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Register application successful. Please wait for confirmation email.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void loginResponse(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            Intent intent = new Intent();
            intent.putExtra(getApplicationContext().getString(R.string.userIntentName), firebaseUser);
            setResult(RESULT_OK);
            finish();
        } else
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.loginFail), Toast.LENGTH_SHORT).show();
    }
}
