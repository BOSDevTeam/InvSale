package com.bosictsolution.invsale.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.SplashActivity;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.NotificationData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.core.app.NotificationCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotiBroadcastReceiver extends BroadcastReceiver {

    AppSetting appSetting=new AppSetting();
    Context context;
    int clientId;
    public static NotificationManager mNotificationManager;
    public static int notificationId = 100;
    SharedPreferences sharedpreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        clientId=sharedpreferences.getInt(AppConstant.CLIENT_ID,0);
        if(appSetting.checkConnection(context)) {
            getClientNotification(clientId);
        }
    }

    private void getClientNotification(int clientId) {
        Api.getClient().getClientNotification(clientId, true).enqueue(new Callback<List<NotificationData>>() {
            @Override
            public void onResponse(Call<List<NotificationData>> call, Response<List<NotificationData>> response) {
                if (response.body() == null) return;
                List<NotificationData> list = response.body();
                if (list == null || list.size() == 0) return;

                List<NotificationData> lstNewProduct = new ArrayList<>();
                List<NotificationData> lstUpdateOrder = new ArrayList<>();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    lstNewProduct = list.stream().filter(c -> c.getNotiType() == AppConstant.NOTI_NEW_PRODUCT).collect(Collectors.toList());
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getNotiType() == AppConstant.NOTI_NEW_PRODUCT) {
                            lstNewProduct.add(list.get(i));
                        }
                    }
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    lstUpdateOrder = list.stream().filter(c -> c.getNotiType() == AppConstant.NOTI_UPDATE_ORDER).collect(Collectors.toList());
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getNotiType() == AppConstant.NOTI_UPDATE_ORDER) {
                            lstUpdateOrder.add(list.get(i));
                        }
                    }
                }

                if (lstNewProduct.size() != 0) {
                    String newProductIds = "";
                    if (lstNewProduct.size() == 1) {
                        showNotification(context, context.getResources().getString(R.string.app_name), lstNewProduct.get(0).getNotiMessage());
                        newProductIds = String.valueOf(lstNewProduct.get(0).getNotiID());
                    } else if (lstNewProduct.size() > 1) {
                        showNotification(context, context.getResources().getString(R.string.app_name), context.getResources().getString(R.string.app_name) + context.getResources().getString(R.string.updated_new_products));
                        for (int i = 0; i < lstNewProduct.size(); i++) {
                            newProductIds += lstNewProduct.get(i).getNotiID() + ",";
                        }
                        newProductIds = newProductIds.substring(0, newProductIds.length() - 1);
                    }
                    updateClientNotification(clientId, AppConstant.NOTI_NEW_PRODUCT, newProductIds, true);
                } else if (lstUpdateOrder.size() != 0) {
                    String updateOrderIds = "";
                    showNotification(context, context.getResources().getString(R.string.app_name), lstUpdateOrder.get(0).getNotiMessage());
                    updateOrderIds = String.valueOf(lstUpdateOrder.get(0).getNotiID());
                    updateClientNotification(clientId, AppConstant.NOTI_UPDATE_ORDER, updateOrderIds, true);
                }
            }

            @Override
            public void onFailure(Call<List<NotificationData>> call, Throwable t) {

            }
        });
    }

    private void updateClientNotification(int clientId,short notiType,String notiIds,boolean isStatusBarFinished) {
        Api.getClient().updateClientNotification(clientId, notiType, notiIds, isStatusBarFinished).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void showNotification(Context context,String title,String message) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "my_channel_01";
        CharSequence name = "my_channel";
        String description = "This is my channel";
        int important = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, name, important);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200});
            mChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(context.getResources().getColor(R.color.primary_500))
                .setContentTitle(title)
                .setContentText(message)
                .setChannelId(channelId);

        Intent resultIntent = new Intent(context, SplashActivity.class);
        //resultIntent.putExtra("ProductUpdate", isProductUpdate);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SplashActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_MUTABLE);

        builder.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, builder.build());
    }
}
