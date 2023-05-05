package com.example.mybudgetapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BudgetActivity extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<Budget> dataList;
    private BudgetGridViewAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String selectedMonth;
    private double totalBudget = 0.00;
    private TextView totalAmountTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        totalAmountTextView = findViewById(R.id.totalbudget);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        gridView = findViewById(R.id.grid_layout);
        dataList = new ArrayList<>();
        adapter = new BudgetGridViewAdapter(this, dataList);
        
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingactionbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BudgetActivity.this, BudgetAdd.class);
                startActivity(intent);
            }
        });

        ImageView img = findViewById(R.id.back);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Spinner spinner = findViewById(R.id.spinner);
        String[] month = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_month1, month);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = parent.getItemAtPosition(position).toString();
                totalBudget = 0.00;
                db.collection("budget")
                        .whereEqualTo("user_id", User.getUser_id())
                        .whereEqualTo("month", selectedMonth)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    dataList.clear();
                                    for (DocumentSnapshot d : list) {
                                        Budget dataClass = d.toObject(Budget.class);
                                        dataList.add(dataClass);
                                        totalBudget += dataClass.getAmount();
                                    }
                                    BudgetGridViewAdapter adapter = new BudgetGridViewAdapter(BudgetActivity.this, dataList);
                                    gridView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                    totalAmountTextView.setText(String.format("RM %.2f", totalBudget));
                                } else {
                                    dataList.clear();
                                    BudgetGridViewAdapter adapter = new BudgetGridViewAdapter(BudgetActivity.this, dataList);
                                    gridView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(BudgetActivity.this, "No budget created for " + selectedMonth, Toast.LENGTH_SHORT).show();

                                    totalAmountTextView.setText(String.format("RM %.2f", totalBudget));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(BudgetActivity.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
}