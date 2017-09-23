package com.promact.akansh.samplefirebaserestapp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Akansh on 13-09-2017.
 */

public class MyApp extends Application {
    private NetworkStatus networkStatus;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        IntentFilter intentFilter = new IntentFilter("android.intent.action" +
                ".MAIN");
        networkStatus = new NetworkStatus();

        getApplicationContext().registerReceiver(networkStatus, intentFilter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        getApplicationContext().unregisterReceiver(this.networkStatus);
    }
}
