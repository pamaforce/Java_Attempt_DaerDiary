package com.example.testapplication;

public class Note {
    private int id;
    private String title;
    private String content;
    private String time;

    public Note(String title, String content, String time,int id) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.id =id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return title + content + time;
    }

}
