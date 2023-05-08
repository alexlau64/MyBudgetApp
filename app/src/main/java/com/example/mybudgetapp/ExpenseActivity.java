package com.example.mybudgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference expensesRef = db.collection("expense");
    User user = User.getUser_instance();
    private double totalExpense = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        expensesRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ExpenseActivity.this));
                        List<DocumentSnapshot> expenseList = queryDocumentSnapshots.getDocuments();
                        MyAdapter adapter = new MyAdapter(expenseList, ExpenseActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting expenses", e);
                    }
                });

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingactionbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpenseActivity.this, ExpenseAdd.class);
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

                db.collection("expense")
                        .whereEqualTo("user_id", User.getUser_id())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> expenseList = new ArrayList<>();
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
                                    TextView totalAmountTextView = findViewById(R.id.totalexpense);
                                    totalAmountTextView.setText(String.format("RM %.2f", totalExpense));
                                }
                                MyAdapter adapter = new MyAdapter(expenseList, ExpenseActivity.this);
                                recyclerView = findViewById(R.id.recyclerView);
                                recyclerView.setAdapter(adapter);
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