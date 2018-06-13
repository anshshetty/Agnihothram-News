package com.app.newsagni.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import com.app.newsagni.model.Post;
import com.app.newsagni.realm.table.PostRealm;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    /** Variation RealmController Constructor -----------------------------------------------------
     */
    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {
        if (instance == null) instance = new RealmController(fragment.getActivity().getApplication());
        return instance;
    }
    public static RealmController with(Activity activity) {
        if (instance == null) instance = new RealmController(activity.getApplication());
        return instance;
    }
    public static RealmController with(Application application) {
        if (instance == null) instance = new RealmController(application);
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    /**
     * Object Post Transaction -----------------------------------------------------------------
     */
    //find all objects
    public List<Post> getPost() {
        RealmResults<PostRealm> realmResults = realm.where(PostRealm.class).findAll();
        realmResults = realmResults.sort("added_date", Sort.DESCENDING);
        List<Post> newList = new ArrayList<>();
        for (PostRealm c : realmResults){
            newList.add(c.getOriginal());
        }
        return newList;
    }

    //save single object
    public Post savePost(Post obj) {
        realm.beginTransaction();
        PostRealm newObj = obj.getObjectRealm();
        newObj.added_date = System.currentTimeMillis(); // set added time now
        newObj = realm.copyToRealmOrUpdate(newObj);
        realm.commitTransaction();
        return newObj != null ? newObj.getOriginal() : null;
    }

    //save collections object
    public List<Post> savePost(List<Post> objs) {
        realm.beginTransaction();
        List<PostRealm> realmList = new ArrayList<>();
        for (Post p : objs){
            realmList.add(p.getObjectRealm());
        }
        realmList = realm.copyToRealmOrUpdate(realmList);
        realm.commitTransaction();

        List<Post> newObjs = new ArrayList<>();
        for (PostRealm p : realmList){
            newObjs.add(p.getOriginal());
        }

        return newObjs;
    }

    //clear all object
    public boolean deleteAllPost() {
        realm.beginTransaction();
        boolean res = realm.where(PostRealm.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        return res;
    }

    //query get single object by id
    public Post getPost(int id) {
        PostRealm postRealm = realm.where(PostRealm.class).equalTo("id", id).findFirst();
        return postRealm != null ? postRealm.getOriginal() : null;
    }

    //delete object by id
    public void deletePost(int id) {
        realm.beginTransaction();
        realm.where(PostRealm.class).equalTo("id", id).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }

    //check if table is empty
    public boolean hasPost() {
        return ( realm.where(PostRealm.class).findAll().size() > 0 );
    }

    //get table size
    public int getPostSize() {
        return realm.where(PostRealm.class).findAll().size();
    }


}
