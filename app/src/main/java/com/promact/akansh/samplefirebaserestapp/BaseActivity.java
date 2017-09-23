package com.promact.akansh.samplefirebaserestapp;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Akansh on 22-09-2017.
 */

public class BaseActivity extends AppCompatActivity {
    NetworkStatus networkStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkStatus = new NetworkStatus();
        registerReceiver(networkStatus,
                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkStatus);
    }
}
