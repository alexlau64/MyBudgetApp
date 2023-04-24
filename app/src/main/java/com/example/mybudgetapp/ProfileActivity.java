package com.example.mybudgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView edtusername = findViewById(R.id.username);
        TextView edtfullname = findViewById(R.id.edtfullname);
        TextView edtdob = findViewById(R.id.edtdob);

        User user = User.getUser_instance();
        String fullname = user.getFull_name();
        String username = user.getUsername();
        String dob = user.getDob();
        edtusername.setText(username);
        edtfullname.setText(fullname);
        edtdob.setText(dob);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_analytics:
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_profile:
                        return true;
                }
                return false;
            }
        });

        Button btnsignout = findViewById(R.id.btnsign_out);
        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.unset_user_session();
                Intent intent = new Intent(ProfileActivity.this, LoginActivty.class);
                startActivity(intent);
            }
        });
    }
}