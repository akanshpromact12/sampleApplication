package com.promact.akansh.samplefirebaserestapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkStatus extends BroadcastReceiver {
    public final static String TAG = "NetworkStatus";

    public NetworkStatus() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MyService.class));

        if (checkInternet(context)) {
            Log.d(TAG, "Network available");
        } else {
            Log.d(TAG, "Network unavailable");
        }
    }

    public Boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        Boolean bool;
        Middleware middleware = new Middleware();

        if (serviceManager.isNetworkAvailable()) {
            bool = true;
            middleware.uploadChats();

        } else {
            bool = false;
        }
        return bool;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
