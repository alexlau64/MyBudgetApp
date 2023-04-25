package com.example.mybudgetapp;

public class Category {
    private static Category category_instance;
    private String categoryId;
    private String categoryName;
    private String description;
    private String userId;

    public static Category getCategory_instance() {
        if(category_instance == null){
            category_instance = new Category();
        }
        return category_instance;
    }
    public Category() {
        // Required empty public constructor for Firestore serialization
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}