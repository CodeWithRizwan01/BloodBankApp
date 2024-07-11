package com.example.bloodbank.ModelClasses;

public class DonorUsers {
    String name, email, password, number, location, gender, blood, profileImage,requestLocation,requestMessage,requestCity,requestBloodGroup,requestActiveNumber;
    private String userId;
    public DonorUsers() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getProfileImage() {
        return profileImage;
    }
    public String getUserId() {
        return userId;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public DonorUsers(String name, String email, String password, String number, String location, String gender, String blood, String profileImage) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.number = number;
        this.location = location;
        this.gender = gender;
        this.blood = blood;
        this.profileImage = profileImage;
    }
    // Constructor for Request data
    public DonorUsers(String requestLocation, String requestMessage, String requestCity, String requestBloodGroup, String requestActiveNumber) {
        this.requestLocation = requestLocation;
        this.requestMessage = requestMessage;
        this.requestCity = requestCity;
        this.requestBloodGroup = requestBloodGroup;
        this.requestActiveNumber = requestActiveNumber;

    }

    public String getRequestLocation() {
        return requestLocation;
    }

    public void setRequestLocation(String requestLocation) {
        this.requestLocation = requestLocation;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getRequestCity() {
        return requestCity;
    }

    public void setRequestCity(String requestCity) {
        this.requestCity = requestCity;
    }

    public String getRequestBloodGroup() {
        return requestBloodGroup;
    }

    public void setRequestBloodGroup(String requestBloodGroup) {
        this.requestBloodGroup = requestBloodGroup;
    }

    public String getRequestActiveNumber() {
        return requestActiveNumber;
    }

    public void setRequestActiveNumber(String requestActiveNumber) {
        this.requestActiveNumber = requestActiveNumber;
    }
}