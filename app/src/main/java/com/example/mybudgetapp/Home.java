package com.example.mybudgetapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ImageView hamburgerMenu;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user = User.getUser_instance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try
        {
            this.getSupportActionBar().hide();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch (NullPointerException e){}

        ImageView imageView = findViewById(R.id.profileimage);
        DocumentReference userRef = db.collection("users").document(user.getUser_id());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String imageUrl = documentSnapshot.getString("image_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(Home.this).load(imageUrl).into(imageView);
                } else {
                    Glide.with(Home.this).load(R.drawable.user).into(imageView);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
            }
        });

        hamburgerMenu = findViewById(R.id.hamburgermenu);
        drawerLayout = findViewById(R.id.drawer_layout);

        //open drawer
        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        //close drawer
        View contentView = findViewById(android.R.id.content);
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            }
        });

        NavigationView navigationView = findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_budgets:
                        startActivity(new Intent(getApplicationContext(),BudgetActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_expenses:
                        startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_categories:
                        startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_predictions:
                        startActivity(new Intent(getApplicationContext(), PredictionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    default:
                        return false;
                }
            }
        });

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

        ImageView img = findViewById(R.id.profileimage);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // Create the chart and set initial data
        PieChart pieChart = findViewById(R.id.chart);
        PieDataSet dataSet = new PieDataSet(new ArrayList<>(), "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Customize the chart properties
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDrawCenterText(true); // Enable drawing text in the center hole
        pieChart.setCenterTextSize(16f); // Set the text size for the center hole
        pieChart.animateY(1000, Easing.EaseInOutCubic);

        // Customize the legend properties
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Set the legend alignment to bottom
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Set the legend alignment to center
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Set the legend orientation to horizontal
        legend.setTextSize(14f);
        legend.setDrawInside(false);
        legend.setEnabled(true);

        dataSet.setValueTextSize(14f);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("RM %.2f", value);
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
                // Retrieve expense data and calculate total amount per category
                db.collection("expense")
                        .whereEqualTo("user_id", User.getUser_id())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            List<PieEntry> entries = new ArrayList<>();
                            Map<String, Double> categoryTotals = new HashMap<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String dateString = document.getString("date");
                                String category = document.getString("category");
                                double amount = document.getDouble("amount");

                                // Convert the "date" field value to month format
                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                Date date;
                                try {
                                    date = format.parse(dateString);
                                    String month = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                    // Filter expenses based on selected month
                                    if (month.equalsIgnoreCase(selectedMonth)) {
                                        // Calculate total amount per category
                                        if (categoryTotals.containsKey(category)) {
                                            double currentTotal = categoryTotals.get(category);
                                            categoryTotals.put(category, currentTotal + amount);
                                        } else {
                                            categoryTotals.put(category, amount);
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            // Create pie entries with category and total amount
                            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                                String categoryName = entry.getKey();
                                double totalAmount = entry.getValue();
                                entries.add(new PieEntry((float) totalAmount, categoryName));
                            }
                            // Update the chart data
                            dataSet.setValues(entries);
                            if (entries.isEmpty()) {
                                pieChart.setCenterText("No Data Available to Show for Current Month"); // Set an empty string if there is no data
                                pieChart.setCenterTextColor(Color.RED); // Set the text color for the center hole
                            } else {
                                pieChart.setCenterText("Overall Expenses Based on Categories");
                                pieChart.setCenterTextColor(Color.BLACK); // Set the text color for the center hole
                            }
                            pieChart.notifyDataSetChanged();
                            pieChart.invalidate();
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors that occurred while retrieving expense data
                        });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}