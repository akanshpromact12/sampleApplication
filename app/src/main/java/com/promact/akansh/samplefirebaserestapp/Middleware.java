package com.promact.akansh.samplefirebaserestapp;

import android.util.Log;

import com.promact.akansh.samplefirebaserestapp.pojo.Chats;
import com.promact.akansh.samplefirebaserestapp.pojo.ChatsRealm;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Akansh on 16-09-2017.
 */

class Middleware {
    public Realm realm = Realm.getDefaultInstance();

    void addChats(ChatsRealm chats) {

        realm.beginTransaction();
        ChatsRealm cr = realm.createObject(ChatsRealm.class, UUID.randomUUID().toString());
        cr.setUserFrom(chats.getUserFrom());
        cr.setUserTo(chats.getUserTo());
        cr.setMsg(chats.getMsg());
        cr.setTime(chats.getTime());
        cr.setNetAvailable(chats.getNetAvailable());

        realm.commitTransaction();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                                .findAll();

                for (ChatsRealm obj : realmResults) {
                    Log.d(TAG, "execute: "+obj.getMsg());
                }

            }
        });
    }

    void searchFinallyUploadedMessages() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatsRealm> realmResults = realm.where(ChatsRealm.class)
                        .equalTo("netAvailable", true).findAll();
            }
        });
    }
}
