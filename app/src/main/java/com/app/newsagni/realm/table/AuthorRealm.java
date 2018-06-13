package com.app.newsagni.realm.table;

import com.app.newsagni.model.Author;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AuthorRealm extends RealmObject {

    @PrimaryKey
    public int id = -1;
    public String slug = "";
    public String name = "";
    public String first_name = "";
    public String last_name = "";
    public String nickname = "";
    public String url = "";
    public String description = "";

    public Author getOriginal() {
        Author a = new Author();
        a.id = id;
        a.slug = slug;
        a.name = name;
        a.first_name = first_name;
        a.last_name = last_name;
        a.nickname = nickname;
        a.url = url;
        a.description = description;
        return a;
    }

}
