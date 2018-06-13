package com.app.newsagni.connection.callbacks;

import java.util.ArrayList;
import java.util.List;

import com.app.newsagni.model.Category;
import com.app.newsagni.model.Post;

public class CallbackCategoryDetails {

    public String status = "";
    public int count = -1;
    public int pages = -1;
    public Category category = null;
    public List<Post> posts = new ArrayList<>();
}
