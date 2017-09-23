package com.promact.akansh.samplefirebaserestapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Akansh on 22-09-2017.
 */
public class ServiceManager {
    private Context context;

    public ServiceManager(Context context) {
        this.context = context;
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        return (null != networkInfo && networkInfo.isConnectedOrConnecting());
    }
}
