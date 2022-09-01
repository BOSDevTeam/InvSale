package com.bosictsolution.invsale.common;

import android.content.Context;

import androidx.lifecycle.LiveData;

public class BluetoothLiveData extends LiveData {

    private Context context;

    public BluetoothLiveData(Context context){
        this.context = context;
    }

    @Override
    protected void onActive() {
        super.onActive();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
    }
}
