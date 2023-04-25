package com.example.mybudgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CategoryAdd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final EditText txtname = (EditText) findViewById(R.id.edt_name);
        final EditText txtdescription = (EditText) findViewById(R.id.edt_description);
        final Button btnsave = (Button) findViewById(R.id.btncomplete);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txtname.getText().toString();
                String description = txtdescription.getText().toString();

                if (name.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter category name", Toast.LENGTH_SHORT).show();
                } else if (description.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    User user = User.getUser_instance();
                    String id = UUID.randomUUID().toString();// generate new ID for the document
                    Map<String, Object> category = new HashMap<>();
                    category.put("category_id", id);
                    category.put("category_name", name);
                    category.put("description", description);
                    category.put("user_id", user.getUser_id());

                    db.collection("category")
                            .add(category)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(), "Add Category Successfull", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@androidx.annotation.NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Add Category Fail", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        ImageView img = findViewById(R.id.back);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.back_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_back:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}