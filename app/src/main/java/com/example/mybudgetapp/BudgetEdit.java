package com.example.mybudgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BudgetEdit extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user = User.getUser_instance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_edit);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        String budgetId = getIntent().getStringExtra("budget_id");
        EditText edtname = findViewById(R.id.edt_name);
        EditText edtdescription = findViewById(R.id.edt_description);
        EditText edtamount = findViewById(R.id.edt_amount);
        Spinner spinnermonth = findViewById(R.id.spinner_month);
        Spinner spinnercategory = findViewById(R.id.spinner_category);

        //Spinner spinner = findViewById(R.id.spinner_month);
        String[] month = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_month2, month);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermonth.setAdapter(adapter);
        spinnermonth.setSelection(0);
        spinnermonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        db.collection("category")
                .whereEqualTo("user_id", user.getUser_id())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<String> spinnerItems = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String spinnerItem = document.getString("category_name");
                            spinnerItems.add(spinnerItem);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(BudgetEdit.this, R.layout.spinner_item_month2, spinnerItems);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Spinner spinner2 = findViewById(R.id.spinner_category);
                        spinnercategory.setAdapter(adapter);
                        spinnercategory.setSelection(0);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                    }
                });
        spinnercategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        db.collection("budget")
                .whereEqualTo("budget_id", budgetId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Get the category object from the document
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Budget budget = queryDocumentSnapshots.getDocuments().get(0).toObject(Budget.class);
                            // Set the category name and description in the UI
                            edtname.setText(budget.getBudget_name());
                            edtdescription.setText(budget.getDescription());
                            edtamount.setText(budget.getAmount());

                            // Set the selected item in the spinner for the month
                            ArrayAdapter<String> monthAdapter = (ArrayAdapter<String>) spinnermonth.getAdapter();
                            int monthIndex = monthAdapter.getPosition(budget.getMonth());
                            spinnermonth.setSelection(monthIndex);

                            // Set the selected item in the spinner for the category
                            ArrayAdapter<String> categoryAdapter = (ArrayAdapter<String>) spinnercategory.getAdapter();
                            int categoryIndex = categoryAdapter.getPosition(budget.getCategory());
                            spinnercategory.setSelection(categoryIndex);

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
                String new_amount = edtamount.getText().toString();
                String selectedMonth = spinnermonth.getSelectedItem().toString();
                String selectedCategory = spinnercategory.getSelectedItem().toString();

                if (new_name.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter budget name", Toast.LENGTH_SHORT).show();
                } else if (new_description.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your description", Toast.LENGTH_SHORT).show();
                } else if (new_amount.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                } else if (selectedMonth.equals("Month")) {
                    Toast.makeText(getApplicationContext(), "Please select a month", Toast.LENGTH_SHORT).show();
                } else if (selectedCategory.equals("Category")) {
                    Toast.makeText(getApplicationContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("budget")
                            .whereEqualTo("budget_id", budgetId)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        // Update the document data
                                        documentSnapshot.getReference().update(
                                                "budget_name", new_name,
                                                "description", new_description,
                                                "amount", new_amount,
                                                "month", selectedMonth,
                                                "category", selectedCategory
                                        );
                                    }
                                    Toast.makeText(getApplicationContext(), "Budget update successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.putExtra("data_updated", true);
                                    intent.putExtra("budget_id", budgetId);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error updating budget: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}