package com.app.newsagni.model;

import java.io.Serializable;

import com.app.newsagni.realm.table.CategoryRealm;

public class Category implements Serializable {

    public int id = -1;
    public String slug = "";
    public String title = "";
    public String description = "";
    public int parent = -1;
    public int post_count = -1;

    public CategoryRealm getObjectRealm(){
        CategoryRealm c = new CategoryRealm();
        c.id = id;
        c.slug = slug;
        c.title = title;
        c.description = description;
        c.parent = parent;
        c.post_count = post_count;
        return c;
    }
}
