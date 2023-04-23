package com.example.mybudgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

public class LoginActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        User user = User.getUser_instance();
        if(user.getIs_login()){
            Intent intent = new Intent(LoginActivty.this, MainActivity.class);
            startActivity(intent);
        }

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        EditText passwordEditText = findViewById(R.id.edt_password);
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden, 0);
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

        EditText txtusername = (EditText) findViewById(R.id.edt_username);
        EditText txtpassword = (EditText) findViewById(R.id.edt_password);
        Button btnSignIn = (Button) findViewById(R.id.btnsign_in);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtusername.getText().toString();
                String password = txtpassword.getText().toString();
                if (username.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your username", Toast.LENGTH_SHORT).show();
                } else if (password.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.collection("users")
                            .whereEqualTo("username", username).whereEqualTo("password", password)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().isEmpty()){
                                            Toast.makeText(getApplicationContext(), "Incorrect credential! Please try again", Toast.LENGTH_LONG).show();
                                        }
                                        else{

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                user.setUser_id(document.getId());
                                                user.setUsername(document.getString("username"));
                                                user.setFull_name(document.getString("fullname"));
                                                user.setIs_login(true);

                                                Intent intent = new Intent(LoginActivty.this, Home.class);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Incorrect credential! Please try again", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}