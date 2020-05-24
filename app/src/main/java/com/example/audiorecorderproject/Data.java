package com.example.audiorecorderproject;

public class Data {
    String name,title,videoUri;

    public Data(String name, String title, String videoUri) {
        this.name = name;
        this.title = title;
        this.videoUri = videoUri;
    }
    public Data(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }
}
