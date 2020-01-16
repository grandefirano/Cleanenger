package com.grandefirano.cleanenger.singleItems;

public class UserData {

    private String email;
    private String profilePhoto;
    private String username;

    public UserData(String email, String username, String profilePhoto) {
        this.email = email;
        this.username = username;
        this.profilePhoto = profilePhoto;

    }

    public UserData() {
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public String getUsername() {
        return username;
    }
}
