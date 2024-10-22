package com.example.doanngon.DTO;

public class User {
    private String userID;
    private String userName,passWord,name,email,address,imageUrl;

    public User(String userID, String userName, String passWord, String name, String email, String address, String imageUrl) {
        this.userID = userID;
        this.userName = userName;
        this.passWord = passWord;
        this.name = name;
        this.email = email;
        this.address = address;
        this.imageUrl = imageUrl;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



}
