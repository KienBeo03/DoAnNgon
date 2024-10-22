package com.example.doanngon.DTO;

public class Item {
    private String id;
    private String imageUrl;
    private String title;
    private String category;
    private boolean isFavorite;

    public Item() {
        // Firestore needs a public no-argument constructor
    }

    public Item(String imageUrl, String title,String category) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
