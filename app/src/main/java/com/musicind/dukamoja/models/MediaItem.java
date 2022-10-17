package com.musicind.dukamoja.models;


//this class is gonna be used for caching to work with gson properly
public class MediaItem {

    private int id;
    private String file;
    private String name;
    private String description;
    private String url;

    public MediaItem(String file, String title, String description, String mediaUrl, int id) {
        this.file = file;
        this.name = title;
        this.description = description;
        this.url = mediaUrl;
        this.id =id;
    }

    public MediaItem() {
    }

    public String getId() {
        return file;
    }

    public void setId(String file) {
        this.file = file;
    }

    public String getTitle() {
        return file;
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaUrl() {
        return url;
    }

    public void setMediaUrl(String mediaUrl) {
        this.url = mediaUrl;
    }

    public int getFile() {
        return id;
    }

}
