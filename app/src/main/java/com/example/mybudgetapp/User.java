package com.example.mybudgetapp;

public class User {
    private static User user_instance;
    private static String user_id;
    private static String username;
    private static String full_name;
    private static String dob;
    private static boolean is_login = false;

    public static User getUser_instance() {
        if(user_instance == null){
            user_instance = new User();
        }
        return user_instance;
    }

    public static void unset_user_session(){
        User.user_id = null;
        User.full_name = null;
        User.username = null;
        User.is_login = false;
    }

    public static void setUser_instance(User user_instance) {
        User.user_instance = user_instance;
    }

    public static String getUser_id() {
        return user_id;
    }

    public static void setUser_id(String user_id) {
        User.user_id = user_id;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static String getFull_name() {
        return full_name;
    }

    public static void setFull_name(String full_name) {
        User.full_name = full_name;
    }

    public static String getDob() {
        return dob;
    }

    public static void setDob(String dob) {
        User.dob = dob;
    }

    public static boolean getIs_login() {
        return is_login;
    }

    public static void setIs_login(boolean is_login) {
        User.is_login = is_login;
    }
}