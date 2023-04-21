package com.example.mybudgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeBack extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_back);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        Button signInButton = findViewById(R.id.sign_in);
        Button signUpButton = findViewById(R.id.sign_up);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeBack.this, LoginActivty.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeBack.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}