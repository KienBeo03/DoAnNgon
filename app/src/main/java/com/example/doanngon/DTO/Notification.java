package com.example.doanngon.DTO;

public class Notification {
    private String title, content;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Notification(String title, String content) {
        this.title = title;
        this.content = content;
    }


}
