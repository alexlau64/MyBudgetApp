package com.example.mybudgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseDetail extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user = User.getUser_instance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        String expenseId = getIntent().getStringExtra("expense_id");
        db.collection("expense")
                .whereEqualTo("expense_id", expenseId)
                .whereEqualTo("user_id", user.getUser_id())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Expense expense = queryDocumentSnapshots.getDocuments().get(0).toObject(Expense.class);

                            TextView amountTextView = findViewById(R.id.txtamount);
                            double amount = expense.getAmount();
                            String formattedAmount = "RM " + String.format("%.2f", amount);
                            amountTextView.setText(formattedAmount);

                            TextView nameTextView = findViewById(R.id.txtname);
                            nameTextView.setText(expense.getExpense_name());

                            TextView categorytextView = findViewById(R.id.txtbudgetname);
                            categorytextView.setText(expense.getBudget_name());

                            TextView descriptionTextView = findViewById(R.id.txtdescription);
                            descriptionTextView.setText(expense.getDescription());

                            TextView datetimeTextView = findViewById(R.id.txtdatetime);
                            Timestamp datetimeTimestamp = expense.getDate();
                            Date datetimeDate = datetimeTimestamp.toDate();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                            String formattedDate = dateFormat.format(datetimeDate);
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                            String formattedTime = timeFormat.format(datetimeDate);
                            String datetimeString = formattedDate + " " + formattedTime;
                            datetimeTextView.setText(datetimeString);

                            ImageView imageView = findViewById(R.id.imageView);
                            String imageUrl = expense.getImage_url();
                            Glide.with(ExpenseDetail.this).load(imageUrl).into(imageView);

                        } else {
                            Toast.makeText(ExpenseDetail.this, "Expense not found", Toast.LENGTH_SHORT).show();
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