package com.app.newsagni.model;

import java.io.Serializable;

public class FcmNotif implements Serializable {
    private String title = "";
    private String content = "";
    private int post_id = -1;

    public FcmNotif() {
    }

    public FcmNotif(String title, String content, int post_id) {
        this.title = title;
        this.content = content;
        this.post_id = post_id;
    }

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

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }
}
