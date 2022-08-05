package com.bosictsolution.invsale.common;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AppSetting {
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
        Snackbar snackbar = Snackbar.make(view1, "No Internet Connection!", Snackbar.LENGTH_LONG);
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
}
