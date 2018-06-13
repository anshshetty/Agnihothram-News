package com.app.newsagni.model;

import com.app.newsagni.realm.table.AttachmentRealm;

import java.io.Serializable;

public class Attachment implements Serializable {
    public int id = -1;
    public String url;
    public String mime_type;

    public AttachmentRealm getObjectRealm() {
        AttachmentRealm a = new AttachmentRealm();
        a.id = id;
        a.url = url;
        a.mime_type = mime_type;
        return a;
    }
}
