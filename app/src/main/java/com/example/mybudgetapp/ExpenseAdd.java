package com.example.mybudgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class ExpenseAdd extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user = User.getUser_instance();
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private ImageView img;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_add);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        img = findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
            }
        });

        ImageView img = findViewById(R.id.back);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tvDate = findViewById(R.id.tv_date);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseAdd.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Update the EditText with the selected date
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                tvDate.setText(selectedDate);
                            }
                        }, year, month, day);
                // Show the date picker dialog
                datePickerDialog.show();
            }
        });

        // Get a reference to the TextView
        TextView tvTime = findViewById(R.id.tv_time);

// Set an OnClickListener on the TextView to show the time picker dialog
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current time
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ExpenseAdd.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Update the text view with the selected time
                                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                                tvTime.setText(selectedTime);
                            }
                        }, hour, minute, true);

                // Show the time picker dialog
                timePickerDialog.show();
            }
        });

        Spinner spinner = findViewById(R.id.spinner_budget);
        db.collection("budget")
                .whereEqualTo("user_id", user.getUser_id())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<String> spinnerItems = new ArrayList<>();
                        spinnerItems.add("Budget");
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String spinnerItem = document.getString("budget_name");
                            spinnerItems.add(spinnerItem);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ExpenseAdd.this, R.layout.spinner_item_month2, spinnerItems);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spinner = findViewById(R.id.spinner_budget);
                        spinner.setAdapter(adapter);
                        spinner.setSelection(0);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                    }
                });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBudget = parent.getItemAtPosition(position).toString();
                // Retrieve the category for the selected budget
                db.collection("budget")
                        .whereEqualTo("budget_name", selectedBudget)
                        .whereEqualTo("user_id", user.getUser_id())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                    category = document.getString("category");
                                    // Use the category variable as needed
                                    // For example, you can store it in a class variable or call a method with the category as an argument
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                            }
                        });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        EditText edtname = findViewById(R.id.edt_name);
        EditText edtdescription = findViewById(R.id.edt_description);
        EditText edtamount = findViewById(R.id.edt_amount);
        TextView tvdate = findViewById(R.id.tv_date);
        TextView tvtime = findViewById(R.id.tv_time);

        Button btncomplete = findViewById(R.id.btncomplete);
        btncomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtname.getText().toString();
                String description = edtdescription.getText().toString();
                double amount = Double.parseDouble(edtamount.getText().toString());
                String amountString  = String.valueOf(edtamount.getText().toString());
                String date = tvdate.getText().toString();
                String time = tvtime.getText().toString();
                String selectedBudget = spinner.getSelectedItem().toString();

                if (name.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter budget name", Toast.LENGTH_SHORT).show();
                } else if (description.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter description", Toast.LENGTH_SHORT).show();
                } else if (amountString.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                } else if (date.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter date", Toast.LENGTH_SHORT).show();
                } else if (time.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter time", Toast.LENGTH_SHORT).show();
                } else if (selectedBudget.equals("Budget")) {
                    Toast.makeText(getApplicationContext(), "Please select a budget", Toast.LENGTH_SHORT).show();
                } else {
                    // Parse the date and time to create a Date object
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8")); // Set the time zone to UTC+8
                    Date parsedDate = null;
                    try {
                        parsedDate = dateFormat.parse(date + " " + time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Convert Date to Timestamp
                    Timestamp timestamp = new Timestamp(parsedDate.getTime());

                    if (filePath != null) {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference ref = storageRef.child("expense/" + user.getUser_id() + "/" + UUID.randomUUID().toString());
                        ref.putFile(filePath)
                                .addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // Get the download URL of the uploaded image
                                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        double amount = Double.parseDouble(amountString);
                                                        String imageUrl = uri.toString();
                                                        String id = UUID.randomUUID().toString();
                                                        Map<String, Object> expense = new HashMap<>();
                                                        expense.put("expense_id", id);
                                                        expense.put("expense_name", name);
                                                        expense.put("description", description);
                                                        expense.put("amount", amount);
                                                        expense.put("date", timestamp);
                                                        expense.put("budget_name", selectedBudget);
                                                        expense.put("category", category);
                                                        expense.put("image_url", imageUrl);
                                                        expense.put("user_id", user.getUser_id());
                                                        db.collection("expense")
                                                                .add(expense)
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentReference documentReference) {
                                                                        Toast.makeText(getApplicationContext(), "Budget added successfully", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getApplicationContext(), "Failed to add budget: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                });
                                                Toast.makeText(ExpenseAdd.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ExpenseAdd.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}