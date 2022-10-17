package com.musicind.dukamoja.models;

public class CommentModel {
    int userImage;
    String name;
    String body;

    public CommentModel(String userName, String comment) {

        this.name = userName;
        this.body = comment;
    }

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return name;
    }

    public void setUserName(String userName) {
        this.name = userName;
    }

    public String getComment() {
        return body;
    }

    public void setComment(String comment) {
        this.body = comment;
    }
}
