package com.example.mybudgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BudgetAdd extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user = User.getUser_instance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_add);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}


        Spinner spinner = findViewById(R.id.spinner_month);
        String[] month = new String[]{"Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_month2, month);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Spinner spinner2 = findViewById(R.id.spinner_category);
        db.collection("category")
                .whereEqualTo("user_id", user.getUser_id())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<String> spinnerItems = new ArrayList<>();
                        spinnerItems.add("Category");
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String spinnerItem = document.getString("category_name");
                            spinnerItems.add(spinnerItem);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(BudgetAdd.this, R.layout.spinner_item_month2, spinnerItems);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spinner2 = findViewById(R.id.spinner_category);
                        spinner2.setAdapter(adapter);
                        spinner2.setSelection(0);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                    }
                });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        EditText txtname = findViewById(R.id.edt_name);
        EditText txtdescription = findViewById(R.id.edt_description);
        EditText txtamount = findViewById(R.id.edt_amount);
        Button btnsave = findViewById(R.id.btncomplete);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txtname.getText().toString();
                String description = txtdescription.getText().toString();
                String amount = txtamount.getText().toString();
                final String selectedMonth = spinner.getSelectedItem().toString();
                final String selectedCategory = spinner2.getSelectedItem().toString();

                if (name.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter category name", Toast.LENGTH_SHORT).show();
                } else if (description.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your description", Toast.LENGTH_SHORT).show();
                } else if (amount.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                } else if (selectedMonth.equals("Month")) {
                    Toast.makeText(getApplicationContext(), "Please select a month", Toast.LENGTH_SHORT).show();
                } else if (selectedCategory.equals("Category")) {
                    Toast.makeText(getApplicationContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                } else {
                    User user = User.getUser_instance();
                    String id = UUID.randomUUID().toString();
                    Map<String, Object> budget = new HashMap<>();
                    budget.put("budget_id", id);
                    budget.put("budget_name", name);
                    budget.put("description", description);
                    budget.put("amount", amount);
                    budget.put("month", selectedMonth);
                    budget.put("category", selectedCategory);
                    budget.put("user_id", user.getUser_id());

                    db.collection("budget")
                            .add(budget)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(), "Add Budget Successfull", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@androidx.annotation.NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Add Budget Fail", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}