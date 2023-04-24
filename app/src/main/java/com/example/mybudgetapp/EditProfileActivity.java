package com.example.mybudgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        User user = User.getUser_instance();
        String user_id = user.getUser_id();
        String username = user.getUsername();
        String fullname = user.getFull_name();
        String dob = user.getDob();

        EditText edtusername = findViewById(R.id.edtusername);
        EditText edtfullname = findViewById(R.id.edtfullname);
        EditText edtdob = findViewById(R.id.edtdob);

        edtusername.setText(username);
        edtfullname.setText(fullname);
        edtdob.setText(dob);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        Button btncomplete = findViewById(R.id.btncomplete);
        btncomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtusername = edtusername.getText().toString();
                String txtfullname = edtfullname.getText().toString();
                String txtdob = edtdob.getText().toString();

                if (txtusername.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your username", Toast.LENGTH_SHORT).show();
                } else if (txtfullname.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your full name", Toast.LENGTH_SHORT).show();
                } else if (txtdob.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("users")
                            .document(user_id)
                            .update(
                                    "fullname", txtfullname,
                                    "dob", txtdob,
                                    "username", txtusername
                            );

                    //edit current user session data
                    User user = User.getUser_instance();
                    user.setUsername(txtusername);
                    user.setDob(txtdob);
                    user.setFull_name(txtfullname);

                    Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}