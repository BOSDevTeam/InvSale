package com.bosictsolution.invsale.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.bluetooth.BtDeviceListAdapter;
import com.bosictsolution.invsale.bluetooth.bt.BtUtil;
import com.bosictsolution.invsale.bluetooth.print.PrintQueue;
import com.bosictsolution.invsale.bluetooth.print.PrintUtil;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class AppSetting {
    public static String EXTRA_MODULE_TYPE;
    public DecimalFormat df= new DecimalFormat("#,###");

    public boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    public void showSnackBar(View view1) {
        Snackbar snackbar = Snackbar.make(view1, "Poor Internet Connection!", Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView textView = view.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.GREEN);
        snackbar.show();
    }

    public String getTodayDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstant.DATE_FORMAT);
        String date = dateFormat.format(Calendar.getInstance().getTime());
        return date.trim();
    }

    public void setupProgress(ProgressDialog progressDialog) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    public boolean checkBluetoothOn(BluetoothAdapter BA) {
        if (BA != null) {
            if (!BA.isEnabled()) return false;
            else return true;
        }
        return false;
    }

    public boolean checkAndRequestBluetoothOn(Activity activity, BluetoothAdapter BA) {
        if (BA != null && activity != null) {
            if (!BA.isEnabled()) {
                try {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activity.startActivityForResult(turnOn, 0);
                    Toast.makeText(activity, activity.getResources().getString(R.string.turn_on), Toast.LENGTH_LONG).show();
                }
                catch(Exception ex){
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean checkBluetoothDevice(BluetoothAdapter BA, String printerAddress, Context context, BluetoothAdapter bluetoothAdapter, BtDeviceListAdapter deviceAdapter) {
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();

        for (BluetoothDevice bt : pairedDevices) {
            String address = bt.getAddress();
            if (printerAddress.equals(address)) {
                if (connectBluetoothDevice(bt, context, bluetoothAdapter, deviceAdapter))
                    return true;
                else return false;
            }
        }
        return false;
    }

    private boolean connectBluetoothDevice(BluetoothDevice bt, Context context, BluetoothAdapter bluetoothAdapter, BtDeviceListAdapter deviceAdapter) {
        if (null == deviceAdapter) {
            return false;
        }
        final BluetoothDevice bluetoothDevice = bt;
        if (null == bluetoothDevice) {
            return false;
        }
        try {
            BtUtil.cancelDiscovery(bluetoothAdapter);
            PrintUtil.setDefaultBluetoothDeviceAddress(context, bluetoothDevice.getAddress());
            PrintUtil.setDefaultBluetoothDeviceName(context, bluetoothDevice.getName());
            if (null != deviceAdapter) {
                deviceAdapter.setConnectedDeviceAddress(bluetoothDevice.getAddress());
            }
            //if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(bluetoothDevice);
            //}
            PrintQueue.getQueue(context).disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            PrintUtil.setDefaultBluetoothDeviceAddress(context, "");
            PrintUtil.setDefaultBluetoothDeviceName(context, "");
            Toast.makeText(context, context.getResources().getString(R.string.bluetooth_tethering_fail), Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
