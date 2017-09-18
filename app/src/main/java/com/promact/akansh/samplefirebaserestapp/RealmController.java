package com.promact.akansh.samplefirebaserestapp;

import android.app.Application;
import android.support.v4.app.Fragment;

import com.promact.akansh.samplefirebaserestapp.pojo.ChatsRealm;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Akansh on 16-09-2017.
 */

public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }

        return instance;
    }

    public static RealmController getInstance() { return instance; }

    public Realm getRealm() { return realm; }

    public void refresh() { realm.refresh(); }

    public void clearAll() {
        realm.beginTransaction();
        realm.delete(ChatsRealm.class);
        realm.commitTransaction();
    }

    public RealmResults<ChatsRealm> getChats() {
        return realm.where(ChatsRealm.class).findAll();
    }

    public ChatsRealm getChatStatus() {
        return realm.where(ChatsRealm.class).equalTo("netAvailable", true).findFirst();
    }

    public Boolean hasChats() {
        return realm.isEmpty();
    }
}
