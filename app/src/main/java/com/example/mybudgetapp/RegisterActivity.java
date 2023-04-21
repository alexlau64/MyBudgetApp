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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

        /*FirebaseFirestore db = FirebaseFirestore.getInstance();


        final EditText fullnametxt = (EditText) findViewById(R.id.fullname_editText);
        final EditText usernametxt = (EditText) findViewById(R.id.username_editText);
        final EditText emailtxt = (EditText) findViewById(R.id.email_editText);
        final EditText passwordtxt = (EditText) findViewById(R.id.password_editText);
        final EditText confirmpasswordtxt = (EditText) findViewById(R.id.confirm_password_editText);
        final TextView backtologin = (TextView) findViewById(R.id.back_to_login);
        Button btnRegister = (Button) findViewById(R.id.registerButton);
        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivty.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = fullnametxt.getText().toString();
                String email = emailtxt.getText().toString();
                String username = usernametxt.getText().toString();
                String password = passwordtxt.getText().toString();
                String confirm_password = confirmpasswordtxt.getText().toString();

                //check if EditText fields are empty
                if (username.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your username", Toast.LENGTH_SHORT).show();
                } else if (email.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                } else if (password.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (fullname.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your full name", Toast.LENGTH_SHORT).show();
                } else if (!confirm_password.matches(password)) {
                    Toast.makeText(getApplicationContext(), "Confirm password does not match password", Toast.LENGTH_SHORT).show();
                } else {
                    //add user
                    Map<String, Object> user = new HashMap<>();
                    user.put("fullname", fullname);
                    user.put("username", username);
                    user.put("email", email);
                    user.put("password", password);

                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_LONG).show();
                                    //add empty symptom table
                                    Map<String, Object> budget = new HashMap<>();
                                    budget.put("user_id", documentReference.getId());
                                    budget.put("budget_id", "");
                                    budget.put("budget_name", "");
                                    budget.put("amount", "");
                                    budget.put("start_date", "");
                                    budget.put("end_date", "");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Registration Fail", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });*/
    }
}