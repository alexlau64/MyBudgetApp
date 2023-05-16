package com.example.mybudgetapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ExpenseViewHolder> {
    private List<DocumentSnapshot> expenseList;
    private Context context;

    public MyAdapter(List<DocumentSnapshot> expenseList, Context context) {
        this.expenseList = expenseList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_layout, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        DocumentSnapshot expense = expenseList.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, amountTextView, dateTextView;
        String expenseId;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ExpenseDetail.class);
                    intent.putExtra("expense_id", expenseId);
                    context.startActivity(intent);
                }
            });
        }

        public void bind(DocumentSnapshot expense) {
            nameTextView.setText(expense.getString("expense_name"));
            double amount = expense.getDouble("amount");
            amountTextView.setText(String.format("RM%.2f", amount));

            // Convert the Timestamp to a Date object
            Timestamp timestamp = expense.getTimestamp("date");
            Date date = timestamp.toDate();

            // Set the desired time zone (UTC+8)
            TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

            // Create a Calendar object and set the time zone
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(timeZone);
            calendar.setTime(date);

            // Format the Date object as a string
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
            dateFormat.setTimeZone(timeZone);
            String dateString = dateFormat.format(calendar.getTime());

            // Set the formatted date in the text view
            dateTextView.setText(dateString);
            expenseId = expense.getString("expense_id");
        }
    }
}
