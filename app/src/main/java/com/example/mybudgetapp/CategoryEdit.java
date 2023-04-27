package com.example.mybudgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CategoryEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        User user = User.getUser_instance();
        String user_id = user.getUser_id();
        String username = user.getUsername();
        String fullname = user.getFull_name();
        String dob = user.getDob();

        String categoryId = getIntent().getStringExtra("category_id");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText edtname = findViewById(R.id.edt_name);
        EditText edtdescription = findViewById(R.id.edt_description);

        db.collection("category")
                .whereEqualTo("category_id", categoryId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Get the category object from the document
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Category category = queryDocumentSnapshots.getDocuments().get(0).toObject(Category.class);
                            // Set the category name and description in the UI
                            edtname.setText(category.getCategory_name());
                            edtdescription.setText(category.getDescription());
                        } else {
                            // Handle the case where no documents are returned
                        }

                    }
                });

        Button btncomplete = findViewById(R.id.btncomplete);
        btncomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_name = edtname.getText().toString();
                String new_description = edtdescription.getText().toString();

                db.collection("category")
                        .whereEqualTo("category_id", categoryId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    // Update the document data
                                    documentSnapshot.getReference().update(
                                            "category_name", new_name,
                                            "description", new_description
                                    );
                                }
                                Toast.makeText(getApplicationContext(), "Category update successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error updating category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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
}