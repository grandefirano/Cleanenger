package com.grandefirano.cleanenger.single_items;

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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
