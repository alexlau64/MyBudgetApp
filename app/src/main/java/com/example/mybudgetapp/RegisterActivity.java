package com.example.mybudgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        EditText passwordEditText = findViewById(R.id.edt_password);
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden, 0);
        EditText cpasswordEditText = findViewById(R.id.edt_cpassword);
        cpasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        cpasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden, 0);
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
                            // Show the password
                            passwordEditText.setTransformationMethod(null);
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden, 0);
                        } else {
                            // Hide the password
                            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden, 0);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        cpasswordEditText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (cpasswordEditText.getRight() - cpasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (cpasswordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
                            // Show the password
                            cpasswordEditText.setTransformationMethod(null);
                            cpasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden, 0);
                        } else {
                            // Hide the password
                            cpasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            cpasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden, 0);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        EditText txtusername = findViewById(R.id.edt_username);
        EditText txtpassword = findViewById(R.id.edt_password);
        EditText txtcpasswordtxt = findViewById(R.id.edt_cpassword);
        Button btnRegister = findViewById(R.id.btnsign_up);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtusername.getText().toString();
                String password = txtpassword.getText().toString();
                String confirm_password = txtcpasswordtxt.getText().toString();

                if (username.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your username", Toast.LENGTH_SHORT).show();
                } else if (password.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (!confirm_password.matches(password)) {
                    Toast.makeText(getApplicationContext(), "Confirm password does not match password", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("users")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().isEmpty()){
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("username", username);
                                            user.put("password", password);
                                            user.put("fullname", "");
                                            user.put("dob", "");

                                            db.collection("users")
                                                    .add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getApplicationContext(), "Registration Successfull", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "Registration Fail", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Username already exist! Please try a new one", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}