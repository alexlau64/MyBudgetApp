package com.example.mybudgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private ImageView img;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;
    User user = User.getUser_instance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        String user_id = user.getUser_id();
        String username = user.getUsername();
        String fullname = user.getFull_name();
        String dob = user.getDob();

        EditText edtusername = findViewById(R.id.edtusername);
        EditText edtfullname = findViewById(R.id.edtfullname);
        TextView edtdob = findViewById(R.id.edtdob);

        // Set an OnClickListener on the EditText to show the date picker dialog
        edtdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Update the EditText with the selected date
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                edtdob.setText(selectedDate);
                            }
                        }, year, month, day);

                // Show the date picker dialog
                datePickerDialog.show();
            }
        });

        img = findViewById(R.id.imageView);
        DocumentReference userRef = db.collection("users").document(user.getUser_id());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String imageUrl = documentSnapshot.getString("image_url");
                Glide.with(EditProfileActivity.this).load(imageUrl).into(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                // Handle any errors
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
            }
        });


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
                    if (filePath != null) {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference ref = storageRef.child("images/" + user.getUser_id().toString());
                        ref.putFile(filePath)
                                .addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // Get the download URL of the uploaded image
                                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUrl = uri.toString();
                                                        // Update the user profile with the image URL
                                                        db.collection("users")
                                                                .document(user_id)
                                                                .update(
                                                                        "fullname", txtfullname,
                                                                        "dob", txtdob,
                                                                        "username", txtusername,
                                                                        "image_url", imageUrl
                                                                );
                                                        // Update the current user session data
                                                        User user = User.getUser_instance();
                                                        user.setUsername(txtusername);
                                                        user.setDob(txtdob);
                                                        user.setFull_name(txtfullname);
                                                        user.setImage_url(imageUrl);

                                                        Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });
                                                Toast.makeText(EditProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Update the user profile without an image
                        db.collection("users")
                                .document(user_id)
                                .update(
                                        "fullname", txtfullname,
                                        "dob", txtdob,
                                        "username", txtusername
                                );
                        // Update the current user session data
                        User user = User.getUser_instance();
                        user.setUsername(txtusername);
                        user.setDob(txtdob);
                        user.setFull_name(txtfullname);

                        Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}