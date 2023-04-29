package com.example.mybudgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BudgetDetail extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_detail);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        Button btnedit = findViewById(R.id.btnedit);
        Button btndelete = findViewById(R.id.btndelete);

        String budgetId = getIntent().getStringExtra("budget_id");

        db.collection("budget")
                .whereEqualTo("budget_id", budgetId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Budget budget = queryDocumentSnapshots.getDocuments().get(0).toObject(Budget.class);

                        TextView amountTextView = findViewById(R.id.txtamount);
                        String amount = budget.getAmount();
                        String formattedAmount = "RM " + amount;
                        amountTextView.setText(formattedAmount);


                        TextView nameTextView = findViewById(R.id.txtname);
                        nameTextView.setText(budget.getBudget_name());

                        TextView categoryextView = findViewById(R.id.txtcategory);
                        categoryextView.setText(budget.getCategory());

                        TextView monthTextView = findViewById(R.id.txtmonth);
                        monthTextView.setText(budget.getMonth());

                        TextView descriptionTextView = findViewById(R.id.txtdescription);
                        descriptionTextView.setText(budget.getDescription());
                    }
                });

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetDetail.this, BudgetEdit.class);
                intent.putExtra("budget_id", budgetId);
                startActivity(intent);
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("budget")
                        .whereEqualTo("budget_id", budgetId)
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
                                                    Toast.makeText(BudgetDetail.this, "Delete successful", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(BudgetDetail.this, "Error delete budget", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(BudgetDetail.this, "Error delete budget", Toast.LENGTH_SHORT).show();
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