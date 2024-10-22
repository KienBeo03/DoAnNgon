package com.example.doanngon.DTO;

public class MonAn {
    String id;
    private String imageUrl;
//    private String imgAvatar;
//    private String userName;
    private String title;
    private String cookingTime;
    private boolean isFavorite;

    public MonAn() {

    }

    public MonAn(String imageUrl, String title, String cookingTime, boolean isFavorite) {
        this.imageUrl = imageUrl;
//        this.imgAvatar = imgAvatar;
//        this.userName = userName;
        this.title = title;
        this.cookingTime = cookingTime;
        this.isFavorite = isFavorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getimageUrl() {
        return imageUrl;
    }

    public void setimageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

//    public String getImgAvatar() {
//        return imgAvatar;
//    }
//
//    public void setImgAvatar(String imgAvatar) {
//        this.imgAvatar = imgAvatar;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public String getcookingTime() {
        return cookingTime;
    }

    public void setcookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
