package com.example.mybudgetapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Report extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ImageView hamburgerMenu;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user = User.getUser_instance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        try
        {
            this.getSupportActionBar().hide();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch (NullPointerException e){}

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_analytics:
                        startActivity(new Intent(getApplicationContext(),Report.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
/*
        // Create entries for the first line
        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0f, 5f));
        entries1.add(new Entry(1f, 7f));
        entries1.add(new Entry(2f, 3f));
// Add more entries for the first line as needed

// Create entries for the second line
        List<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(0f, 3f));
        entries2.add(new Entry(1f, 4f));
        entries2.add(new Entry(2f, 6f));
// Add more entries for the second line as needed

        LineDataSet lineDataSet1 = new LineDataSet(entries1, "Data Set 1");
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setCircleColor(Color.RED);
        lineDataSet1.setLineWidth(2f);
        lineDataSet1.setCircleRadius(4f);
        lineDataSet1.setDrawValues(true);

        LineDataSet lineDataSet2 = new LineDataSet(entries2, "Data Set 2");
        lineDataSet2.setColor(Color.GREEN);
        lineDataSet2.setCircleColor(Color.YELLOW);
        lineDataSet2.setLineWidth(2f);
        lineDataSet2.setCircleRadius(4f);
        lineDataSet2.setDrawValues(true);

        LineData lineData = new LineData(lineDataSet1, lineDataSet2);

        LineChart lineChart = findViewById(R.id.chart);
        lineChart.setData(lineData);
        lineChart.invalidate();*/



        Spinner spinner = findViewById(R.id.spinner);
        String[] options = new String[]{"Daily", "Monthly", "Yearly"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_month1, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Retrieve expense data for the current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        CollectionReference expenseRef = db.collection("expense");
        Query expenseQuery = expenseRef.whereEqualTo("date", currentDate);
        expenseQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Entry> expenseEntries = new ArrayList<>();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                double expenseAmount = document.getDouble("amount");
                // Retrieve other necessary fields

                // Create Entry object and add it to expenseEntries list
                expenseEntries.add(new Entry(document.getLong("date"), (float) expenseAmount));
            }

            // Call a method to display the LineChart with expenseEntries data
            displayLineChart(expenseEntries, "Expenses");
        }).addOnFailureListener(e -> {
            // Handle the failure
        });



    }
    private void displayLineChart(List<Entry> entries, String label) {
        LineChart lineChart = findViewById(R.id.chart);

        LineDataSet lineDataSet = new LineDataSet(entries, label);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawValues(true);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // Customize the chart's appearance and behavior
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Format the x-value (e.g., date) as needed
                return ""; // Return the formatted x-value
            }
        });

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
        lineChart.animateY(1000, Easing.Linear);
        lineChart.invalidate();
    }

}