package com.example.mybudgetapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.util.List;

public class BudgetCardAdapter extends RecyclerView.Adapter<BudgetCardAdapter.ViewHolder> {
    private List<DocumentSnapshot> budgetCards;
    private static Context context;
    public BudgetCardAdapter(List<DocumentSnapshot> budgetCards, Context context) {
        this.budgetCards = budgetCards;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the budget_card_item_layout.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_card_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot budget = budgetCards.get(position);
        holder.bind(budget);
    }

    @Override
    public int getItemCount() {
        return budgetCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define the views within budget_card_item_layout.xml
        private TextView nameTextView, leftTextView, rightTextView;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            nameTextView = itemView.findViewById(R.id.tv_name);
            leftTextView = itemView.findViewById(R.id.tv_left);
            rightTextView = itemView.findViewById(R.id.tv_right);
            progressBar = itemView.findViewById(R.id.progressbar);
        }

        public void bind(DocumentSnapshot budget) {
            // Set the budget name to the nameTextView
            String budgetName = budget.getString("budget_name");
            nameTextView.setText(budgetName);

            // Retrieve the total expense amount for the budget name
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Fetch the expenses for the corresponding budget name from Firestore
            db.collection("expense")  // Replace with your actual collection name
                    .whereEqualTo("budget_name", budgetName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        double totalExpenseAmount = 0.0;  // Assuming total expense amount is stored as a double value

                        for (QueryDocumentSnapshot expenseSnapshot : queryDocumentSnapshots) {
                            double expenseAmount = expenseSnapshot.getDouble("amount");
                            totalExpenseAmount += expenseAmount;
                        }

                        // Set the total budget amount from the DocumentSnapshot
                        double budgetAmount = budget.getDouble("amount");

                        // Calculate the remaining amount
                        double remainingAmount = budgetAmount - totalExpenseAmount;

                        // Format the amounts with 2 decimal places
                        DecimalFormat decimalFormat = new DecimalFormat("#.00");
                        String formattedBudgetAmount = decimalFormat.format(budgetAmount);
                        String formattedRemainingAmount = decimalFormat.format(remainingAmount);

                        // Set the remaining amount to the leftTextView
                        leftTextView.setText("RM " + formattedRemainingAmount);

                        // Set the total expense amount to the rightTextView
                        rightTextView.setText("RM " + formattedBudgetAmount);

                        // Set the text color of the remaining amount based on its value
                        if (remainingAmount < 0) {
                            leftTextView.setTextColor(ContextCompat.getColor(context, R.color.red)); // Set to your desired red color resource
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle('"' +budgetName +'"' + " Budget Overspent")
                                    .setMessage("You have exceeded your budget! Please update your budget amount!")
                                    .setPositiveButton("OK", null)
                                    .show();
                        } else {
                            leftTextView.setTextColor(ContextCompat.getColor(context, R.color.black)); // Set to your desired default text color resource
                        }

                        // Set the progress bar value based on the remaining amount and total budget amount
                        int progress = (int) ((remainingAmount / budgetAmount) * 100);
                        progressBar.setProgress(progress);
                    })
                    .addOnFailureListener(e -> {
                        // Handle error fetching expenses from Firestore
                        Log.e("BudgetCardAdapter", "Failed to fetch expenses for budget: " + budgetName, e);
                    });
        }

    }
}
