package com.example.mybudgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PredictionActivity extends AppCompatActivity {
    private BudgetPredictor budgetPredictor;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        try {
            // Initialize the BudgetPredictor with the TensorFlow Lite model
            budgetPredictor = new BudgetPredictor(getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }

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

                db.collection("expense")
                        .whereEqualTo("user_id", User.getUser_id())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> expenseList = new ArrayList<>();
                                double totalExpense = 0.0;
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Expense expense = documentSnapshot.toObject(Expense.class);
                                    String dateString = expense.getDate();
                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                    Date date = null;
                                    try {
                                        date = format.parse(dateString);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    int month = calendar.get(Calendar.MONTH);
                                    if (selectedMonth.equals(getMonthName(month))) {
                                        DocumentSnapshot expenseDocument = documentSnapshot;
                                        expenseList.add(expenseDocument);
                                        totalExpense += expense.getAmount();
                                    }
                                }
                                TextView tv_totalbudget = findViewById(R.id.totalexpense);
                                tv_totalbudget.setText(String.format("RM %.2f", totalExpense));
                                // Use the totalExpense value in your prediction logic
                                float predictedBudget = budgetPredictor.predictBudget((float) totalExpense);
                                String formattedBudget;
                                if (totalExpense == 0.0) {
                                    formattedBudget = "RM 0";
                                } else {
                                    formattedBudget = String.format("RM %.0f", predictedBudget);
                                }
                                TextView tv_predict = findViewById(R.id.tv_predict);
                                tv_predict.setText(formattedBudget);
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    private String getMonthName(int month) {
        String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
}