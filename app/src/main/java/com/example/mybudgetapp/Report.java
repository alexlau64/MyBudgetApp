package com.example.mybudgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.security.CodeSigner;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Report extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user = User.getUser_instance();
    AnyChartView anyChartView;
    Cartesian cartesian;
    @RequiresApi(api = Build.VERSION_CODES.O)
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

        Spinner spinner = findViewById(R.id.spinner);
        String[] options = new String[]{"Daily", "Monthly", "Yearly"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_month1, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        anyChartView = findViewById(R.id.chart);
        cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair().yLabel(true).yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Total Budget vs Expense");

        cartesian.yAxis(0).title("Amount (RM)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = options[position];
                if(selectedOption.equals("Daily")){
                    /*Calendar calendar = Calendar.getInstance();
                    String currentMonth = new SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.getTime());
                    db.collection("budget")
                            .whereEqualTo("month", currentMonth)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        double totalBudgetAmount = 0.0;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            double budgetAmount = document.getDouble("amount");
                                            totalBudgetAmount += budgetAmount;
                                        }
                                        int year = calendar.get(Calendar.YEAR);
                                        int month = calendar.get(Calendar.MONTH) + 1; // Months are zero-based in Calendar class
                                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);// Get the current day of the month
                                        Calendar startOfMonth = Calendar.getInstance();
                                        startOfMonth.set(year, month - 1, dayOfMonth, 0, 0, 0); // Set time to 00:00:00
                                        Calendar endOfMonth = Calendar.getInstance();
                                        endOfMonth.set(year, month - 1, dayOfMonth, 23, 59, 59); // Set time to 23:59:59
                                        Date startDate = startOfMonth.getTime();
                                        Date endDate = endOfMonth.getTime();
                                        double finalTotalBudgetAmount = totalBudgetAmount;
                                        db.collection("expense")
                                                .whereGreaterThanOrEqualTo("date", startDate)
                                                .whereLessThanOrEqualTo("date", endDate)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            double totalExpenseAmount = 0.0;
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                double expenseAmount = document.getDouble("amount");
                                                                totalExpenseAmount += expenseAmount;
                                                            }
                                                            List<DataEntry> seriesData = new ArrayList<>();
                                                            CustomDataEntry dataEntry = new CustomDataEntry(String.valueOf(dayOfMonth), finalTotalBudgetAmount, totalExpenseAmount);
                                                            seriesData.add(dataEntry);
                                                            updateChart(seriesData);
                                                        } else {
                                                            Log.d(TAG, "Error getting expenses: ", task.getException());
                                                        }
                                                    }
                                                });

                                    } else {
                                        Log.d(TAG, "Error getting budget: ", task.getException());
                                    }
                                }
                            });*/
                }
                else if (selectedOption.equals("Monthly")) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int finalYear = year;
                    List<DataEntry> seriesData = new ArrayList<>();
                    List<Task<?>> tasks = new ArrayList<>();

                    anyChartView.clear();
                    // Customize the chart appearance or set default options
                    anyChartView.setChart(null); // Set the chart to null
                    // Redraw or refresh the chart
                    anyChartView.invalidate();

                    for (int dayOfMonth = 1; dayOfMonth <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); dayOfMonth++) {
                        int finalDayOfMonth = dayOfMonth;
                        Calendar startOfDay = Calendar.getInstance();
                        startOfDay.set(finalYear, month - 1, finalDayOfMonth, 0, 0, 0);
                        Calendar endOfDay = Calendar.getInstance();
                        endOfDay.set(finalYear, month - 1, finalDayOfMonth, 23, 59, 59);
                        Date dayStartDate = startOfDay.getTime();
                        Date dayEndDate = endOfDay.getTime();

                        Task<QuerySnapshot> expenseTask = db.collection("expense")
                                .whereGreaterThanOrEqualTo("date", dayStartDate)
                                .whereLessThanOrEqualTo("date", dayEndDate)
                                .get();
                        tasks.add(expenseTask);
                    }

                    Task<QuerySnapshot> budgetTask = db.collection("budget")
                            .whereEqualTo("month", getMonthName(month))
                            .get();
                    tasks.add(budgetTask);

                    Tasks.whenAllComplete(tasks)
                            .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Task<?>>> task) {
                                    if (task.isSuccessful()) {
                                        List<Double> totalExpenseAmounts = new ArrayList<>();
                                        double totalBudgetAmount = 0.0;

                                        // Handle expense query results
                                        for (int i = 0; i < task.getResult().size() - 1; i++) {
                                            Task<?> expenseTask = task.getResult().get(i);

                                            if (expenseTask.isSuccessful()) {
                                                QuerySnapshot expenseSnapshot = (QuerySnapshot) expenseTask.getResult();
                                                double totalExpenseAmount = 0.0;
                                                for (QueryDocumentSnapshot expenseDocument : expenseSnapshot) {
                                                    double expenseAmount = expenseDocument.getDouble("amount");
                                                    totalExpenseAmount += expenseAmount;
                                                }
                                                totalExpenseAmounts.add(totalExpenseAmount);
                                            } else {
                                                Log.d(TAG, "Error getting expenses: ", expenseTask.getException());
                                            }
                                        }

                                        // Handle budget query result
                                        Task<?> budgetTask = task.getResult().get(task.getResult().size() - 1);
                                        if (budgetTask.isSuccessful()) {
                                            QuerySnapshot budgetSnapshot = (QuerySnapshot) budgetTask.getResult();
                                            for (QueryDocumentSnapshot document : budgetSnapshot) {
                                                double budgetAmount = document.getDouble("amount");
                                                totalBudgetAmount += budgetAmount;
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting budget: ", budgetTask.getException());
                                        }

                                        // Process the data
                                        for (int dayOfMonth = 1; dayOfMonth <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); dayOfMonth++) {
                                            int finalDayOfMonth = dayOfMonth;
                                            double totalExpenseAmount = totalExpenseAmounts.get(dayOfMonth - 1);
                                            CustomDataEntry dataEntry = new CustomDataEntry(String.valueOf(finalDayOfMonth), totalBudgetAmount, totalExpenseAmount);
                                            seriesData.add(dataEntry);
                                            if (finalDayOfMonth == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                                                updateChart(seriesData);
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "Error merging tasks: ", task.getException());
                                    }
                                }
                            });

                }
                else if (selectedOption.equals("Yearly")) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int finalYear = year;
                    List<DataEntry> seriesData = new ArrayList<>();
                    List<Task<?>> tasks = new ArrayList<>();

                    for (int month = 1; month <= 12; month++) {
                        int finalMonth = month;
                        Calendar startOfMonth = Calendar.getInstance();
                        startOfMonth.set(finalYear, finalMonth - 1, 1, 0, 0, 0); // Set time to the start of the month
                        Calendar endOfMonth = Calendar.getInstance();
                        endOfMonth.set(finalYear, finalMonth - 1, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59); // Set time to the end of the month
                        Date monthStartDate = startOfMonth.getTime();
                        Date monthEndDate = endOfMonth.getTime();

                        Task<QuerySnapshot> expenseTask = db.collection("expense")
                                .whereGreaterThanOrEqualTo("date", monthStartDate)
                                .whereLessThanOrEqualTo("date", monthEndDate)
                                .get();
                        tasks.add(expenseTask);

                        Task<QuerySnapshot> budgetTask = db.collection("budget")
                                .get();
                        tasks.add(budgetTask);
                    }

                    Tasks.whenAllComplete(tasks)
                            .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Task<?>>> task) {
                                    if (task.isSuccessful()) {
                                        List<Double> totalExpenseAmounts = new ArrayList<>();
                                        List<Double> totalBudgetAmounts = new ArrayList<>();

                                        // Handle query results
                                        for (int i = 0; i < task.getResult().size(); i += 2) {
                                            Task<?> expenseTask = task.getResult().get(i);
                                            Task<?> budgetTask = task.getResult().get(i);

                                            if (expenseTask.isSuccessful()) {
                                                QuerySnapshot expenseSnapshot = (QuerySnapshot) expenseTask.getResult();
                                                double totalExpenseAmount = 0.0;
                                                for (QueryDocumentSnapshot expenseDocument : expenseSnapshot) {
                                                    double expenseAmount = expenseDocument.getDouble("amount");
                                                    totalExpenseAmount += expenseAmount;
                                                }
                                                totalExpenseAmounts.add(totalExpenseAmount);
                                            } else {
                                                Log.d(TAG, "Error getting expenses: ", expenseTask.getException());
                                            }

                                            if (budgetTask.isSuccessful()) {
                                                QuerySnapshot budgetSnapshot = (QuerySnapshot) budgetTask.getResult();
                                                double totalBudgetAmount = 0.0;
                                                for (QueryDocumentSnapshot document : budgetSnapshot) {
                                                    double budgetAmount = document.getDouble("amount");
                                                    totalBudgetAmount += budgetAmount;
                                                }
                                                totalBudgetAmounts.add(totalBudgetAmount);
                                            } else {
                                                Log.d(TAG, "Error getting budget: ", budgetTask.getException());
                                            }
                                        }
                                        // Process the data
                                        for (int month = 1; month <= 12; month++) {
                                            int finalMonth = month;
                                            String monthName = getMonthName(finalMonth);
                                            double totalExpenseAmount = totalExpenseAmounts.get(month - 1);
                                            double totalBudgetAmount = totalBudgetAmounts.get(month - 1);
                                            CustomDataEntry dataEntry = new CustomDataEntry(String.valueOf(monthName), totalBudgetAmount, totalExpenseAmount);
                                            seriesData.add(dataEntry);
                                            if (finalMonth == 12) {
                                                updateChart(seriesData);
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "Error merging tasks: ", task.getException());
                                    }
                                }
                            });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when nothing is selected
            }
        });
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value2, Number value3) {
            super(x, value2);
            //setValue("value2", value2);
            setValue("value3", value3);
        }
    }

    private String getMonthName(int month) {
        String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        if (month >= 1 && month <= 12) {
            return monthNames[month - 1];
        }
        return "";
    }


    private void updateChart(List<DataEntry> seriesData) {
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Budget");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers().type(MarkerType.CIRCLE).size(4d);
        series2.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);
        series2.markers().enabled(true);
        series2.markers().type(MarkerType.CIRCLE).size(2d);

        Line series3 = cartesian.line(series3Mapping);
        series3.name("Expense");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers().type(MarkerType.CIRCLE).size(4d);
        series3.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);
        series3.markers().enabled(true);
        series3.markers().type(MarkerType.CIRCLE).size(2d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
    }

}