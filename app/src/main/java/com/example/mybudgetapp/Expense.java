package com.example.mybudgetapp;

import com.google.firebase.Timestamp;

public class Expense {
    private static Expense expense_instance;
    private String expense_id;
    private String expense_name;
    private String description;
    private double amount;
    private Timestamp date;
    private String time;
    private String budget_name;
    private String user_id;
    private String image_url;

    public static Expense getExpense_instance() {
        return expense_instance;
    }

    public static void setExpense_instance(Expense expense_instance) {
        Expense.expense_instance = expense_instance;
    }

    public String getExpense_id() {
        return expense_id;
    }

    public void setExpense_id(String expense_id) {
        this.expense_id = expense_id;
    }

    public String getExpense_name() {
        return expense_name;
    }

    public void setExpense_name(String expense_name) {
        this.expense_name = expense_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBudget_name() {
        return budget_name;
    }

    public void setBudget_name(String budget) {
        this.budget_name = budget;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
