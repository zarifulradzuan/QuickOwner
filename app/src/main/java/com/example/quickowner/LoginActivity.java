package com.example.quickowner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quickowner.controller.UserController;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity implements UserController.LoginResponseListener {
    private int REGISTER = 2;
    private UserController userController;
    private EditText email;
    private EditText password;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.loading);

        userController = new UserController(this);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                userController.signIn(email.getText().toString(), password.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REGISTER);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.INVISIBLE);
        if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Register application successful. Please wait for confirmation email.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void loginResponse(FirebaseUser firebaseUser) {
        progressBar.setVisibility(View.INVISIBLE);
        if (firebaseUser != null) {
            setResult(RESULT_OK);
            finish();
        } else
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.loginFail), Toast.LENGTH_SHORT).show();
    }
}
