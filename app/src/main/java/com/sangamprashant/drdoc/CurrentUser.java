package com.sangamprashant.drdoc;

public class CurrentUser {
    private static CurrentUser instance;
    private String userId;
    private String name;
    private String email;
    private String userName;
    private String account;
    // Add any other required user information
    private String token;
    private String Photo;

    private CurrentUser() {
        // Private constructor to prevent instantiation
    }

    public static synchronized CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public void setUser(String userId, String name, String email, String userName, String account, String token) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userName = userName;
        this.account = account;
        // Set any other required user information
        this.token = token;

    }
    public void setProfile(String Photo){
        this.Photo = Photo;
    }

    public void clearUser() {
        this.userId = null;
        this.name = null;
        this.email = null;
        this.userName = null;
        this.account = null;
        // Clear any other user information
        this.token = null;
        this.Photo = null;
    }

    // Add getters for user information
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccount() {
        return account;
    }
    // Add getters for other user information
    public String getToken() {
        return token;
    }
    public String getPhoto() {
        return Photo;
    }
}