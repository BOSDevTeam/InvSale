package com.bosictsolution.invsale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.Confirmation;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.common.NavDrawerBadge;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.listener.IConfirmation;
import com.bosictsolution.invsale.service.NotiBroadcastReceiver;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bosictsolution.invsale.databinding.ActivityMainBinding;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements IConfirmation {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    TextView tvUserName, tvPhone;
    static NavigationView navigationView;
    SharedPreferences sharedpreferences;
    Confirmation confirmation=new Confirmation(this);
    ConnectionLiveData connectionLiveData;
    AppSetting appSetting=new AppSetting();
    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        connectionLiveData = new ConnectionLiveData(this);
        int clientId = sharedpreferences.getInt(AppConstant.CLIENT_ID, 0);
        boolean isAccessNoti= sharedpreferences.getBoolean(AppConstant.ACCESS_NOTI, false);

        connectionLiveData.observe(this, new Observer<ConnectionData>() {
            @Override
            public void onChanged(ConnectionData connectionData) {
                if (!connectionData.getIsConnected())
                    appSetting.showSnackBar(findViewById(R.id.drawer_layout));
            }
        });

        setSupportActionBar(binding.appBarMain.toolbar);
        /*binding.appBarMain.toolbar.setTitleTextColor(Color.WHITE);*/
        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        View headerView = binding.navView.getHeaderView(0);
        tvUserName = headerView.findViewById(R.id.tvUserName);
        tvPhone = headerView.findViewById(R.id.tvPhone);
        tvUserName.setText(sharedpreferences.getString(AppConstant.CLIENT_NAME, ""));
        tvPhone.setText(sharedpreferences.getString(AppConstant.CLIENT_PHONE, ""));
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_sale, R.id.nav_sale_order,R.id.nav_notification, R.id.nav_profile, R.id.nav_setting,R.id.nav_report,R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //getClientNotiCount(clientId);

        //if(isAccessNoti) startupStatusBarNoti();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavigationUI.onNavDestinationSelected(item, navController);
                if (item.getItemId() == R.id.nav_home) navController.navigate(R.id.nav_home);
                else if (item.getItemId() == R.id.nav_logout)
                    confirmation.showConfirmDialog(MainActivity.this, getResources().getString(R.string.exit_confirm_message),getResources().getString(R.string.exit_app));
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void setOnConfirmOKClick() {
        finish();
        System.exit(0);
    }

    public static void setNotificationBadge(int notiCount,Context context) {
        NavDrawerBadge badge = new NavDrawerBadge(context, navigationView,
                R.id.nav_notification,
                String.valueOf(notiCount > 99 ? "+" + 99 : notiCount),
                AppConstant.COLOR_WHITE, AppConstant.COLOR_RED, AppConstant.COLOR_RED);

        if (notiCount == 0)
            badge.setVisibility(View.GONE);
        else
            badge.setVisibility(View.VISIBLE);
    }

    private void getClientNotiCount(int clientId) {
        Api.getClient().getClientNotiCount(clientId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == null){
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    return;
                }
                int notiCount = response.body();
                setNotificationBadge(notiCount, context);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startupStatusBarNoti(){
        Intent alarmIntent = new Intent(context, NotiBroadcastReceiver.class);
        PendingIntent pendingIntent;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, AppConstant.NOTI_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_MUTABLE);
        }else{
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, AppConstant.NOTI_REQUEST_CODE, alarmIntent, 0);
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, AppConstant.NOTI_DURATION);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),  AppConstant.NOTI_DURATION * 60000, pendingIntent);
    }

    @Override
    public void onBackPressed() {
        confirmation.showConfirmDialog(MainActivity.this, getResources().getString(R.string.exit_confirm_message),getResources().getString(R.string.exit_app));
    }
}