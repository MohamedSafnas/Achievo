package com.s23010675.achievo;

public class UserModel {
    private String username;
    private String email;

    // Firestore needs a public no-argument constructor
    public UserModel() {}

    public UserModel(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters & Setters (required by Firestore to deserialize)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
