package com.example.mybudgetapp;

public class Category {
    private static Category category_instance;
    private String category_id;
    private String category_name;
    private String description;
    private String user_id;

    public static Category getCategory_instance() {
        if(category_instance == null){
            category_instance = new Category();
        }
        return category_instance;
    }
    public Category() {
        // Required empty public constructor for Firestore serialization
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
