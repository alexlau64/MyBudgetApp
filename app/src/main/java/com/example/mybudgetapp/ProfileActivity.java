package com.example.mybudgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView edtusername = findViewById(R.id.edtusername);
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

        ImageView imageView = findViewById(R.id.imageView);
        DocumentReference userRef = db.collection("users").document(user.getUser_id());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String imageUrl = documentSnapshot.getString("image_url");
                Glide.with(ProfileActivity.this).load(imageUrl).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
            }
        });
    }
}