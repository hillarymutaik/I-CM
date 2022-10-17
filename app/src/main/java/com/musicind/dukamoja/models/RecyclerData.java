package com.musicind.dukamoja.models;

public class RecyclerData {

    private String title;
    private int imgid;
    int size;
    int duration;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public RecyclerData(String title, int imgid ){
        this.title = title;
        this.imgid = imgid;
//        this.duration = duration;
//        this.size = size;
    }
}
