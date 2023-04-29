package com.example.mybudgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class CategoryDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        Button btnedit = findViewById(R.id.btnedit);
        Button btndelete = findViewById(R.id.btndelete);

        String categoryId = getIntent().getStringExtra("category_id");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("category")
                .whereEqualTo("category_id", categoryId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle the error
                            return;
                        }

                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Get the category object from the document
                            Category category = queryDocumentSnapshots.getDocuments().get(0).toObject(Category.class);

                            // Set the category name and description in the UI
                            TextView nameTextView = findViewById(R.id.txtname);
                            nameTextView.setText(category.getCategory_name());

                            TextView descriptionTextView = findViewById(R.id.txtdescription);
                            descriptionTextView.setText(category.getDescription());
                        } else {
                            // Handle the case where no documents are returned
                        }
                    }
                });


        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryDetail.this, CategoryEdit.class);
                intent.putExtra("category_id", categoryId);
                startActivityForResult(intent, 1);
                startActivity(intent);
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("category")
                        .whereEqualTo("category_id", categoryId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    DocumentReference docRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                                    docRef.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(CategoryDetail.this, "Delete successful", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CategoryDetail.this, "Error delete category", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CategoryDetail.this, "Error delete category", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean dataUpdated = data.getBooleanExtra("data_updated", false);

            if (dataUpdated) {
                String categoryId = data.getStringExtra("category_id");

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("category")
                        .whereEqualTo("category_id", categoryId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // Get the category object from the document
                                    Category category = queryDocumentSnapshots.getDocuments().get(0).toObject(Category.class);

                                    // Set the category name and description in the UI
                                    TextView nameTextView = findViewById(R.id.txtname);
                                    nameTextView.setText(category.getCategory_name());

                                    TextView descriptionTextView = findViewById(R.id.txtdescription);
                                    descriptionTextView.setText(category.getDescription());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Error getting category: " + e);
                            }
                        });
            }
        }
    }

}