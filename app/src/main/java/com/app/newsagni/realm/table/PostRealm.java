package com.app.newsagni.realm.table;

import java.util.ArrayList;

import com.app.newsagni.model.Post;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PostRealm extends RealmObject {

    @PrimaryKey
    public int id = -1;
    public String type = "";
    public String slug = "";
    public String url = "";
    public String status = "";
    public String title = "";
    public String title_plain = "";
    public String content = "";
    public String excerpt = "";
    public String date = "";
    public String modified = "";
    public String thumbnail = "";
    public int comment_count = -1;

    public long added_date = 0;

    public RealmList<CategoryRealm> categories;
    public AuthorRealm author = new AuthorRealm();
    public RealmList<CommentRealm> comments;
    public RealmList<AttachmentRealm> attachments;

    public Post getOriginal() {
        Post p = new Post();
        p.id = id;
        p.type = type;
        p.slug = slug;
        p.url = url;
        p.status = status;
        p.title = title;
        p.title_plain = title_plain;
        p.content = content;
        p.excerpt = excerpt;
        p.date = date;
        p.modified = modified;
        p.thumbnail = thumbnail;
        p.comment_count = comment_count;

        p.categories = new ArrayList<>();
        for (CategoryRealm c : categories) {
            p.categories.add(c.getOriginal());
        }

        p.comments = new ArrayList<>();
        for (CommentRealm c : comments) {
            p.comments.add(c.getOriginal());
        }

        p.author = author.getOriginal();

        p.attachments = new ArrayList<>();
        for (AttachmentRealm a : attachments) {
            p.attachments.add(a.getOriginal());
        }

        return p;
    }
}
