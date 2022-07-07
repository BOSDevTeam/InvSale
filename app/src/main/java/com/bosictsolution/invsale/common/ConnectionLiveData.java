package com.bosictsolution.invsale.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bosictsolution.invsale.data.ConnectionData;

import androidx.lifecycle.LiveData;

public class ConnectionLiveData extends LiveData<ConnectionData> {

    private Context context;
    private static final int WifiData = 1, MobileData = 2;

    public ConnectionLiveData(Context context) {
        this.context = context;
    }

    @Override
    protected void onActive() {
        super.onActive();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        context.unregisterReceiver(networkReceiver);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo activeNetwork = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    switch (activeNetwork.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            postValue(new ConnectionData(WifiData, true));
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            postValue(new ConnectionData(MobileData, true));
                            break;
                    }
                } else {
                    postValue(new ConnectionData(0, false));
                }
            }
        }
    };
}
