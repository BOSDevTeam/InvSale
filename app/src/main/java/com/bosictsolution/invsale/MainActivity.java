package com.bosictsolution.invsale;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.Confirmation;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.listener.IConfirmation;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
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

public class MainActivity extends AppCompatActivity implements IConfirmation {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    TextView tvUserName, tvPhone;
    static TextView notificationBadge;
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
        sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
        connectionLiveData = new ConnectionLiveData(this);
        int clientId = sharedpreferences.getInt(AppConstant.ClientID, 0);

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
        NavigationView navigationView = binding.navView;
        View headerView = binding.navView.getHeaderView(0);
        tvUserName = headerView.findViewById(R.id.tvUserName);
        tvPhone = headerView.findViewById(R.id.tvPhone);
        tvUserName.setText(sharedpreferences.getString(AppConstant.ClientName, ""));
        tvPhone.setText(sharedpreferences.getString(AppConstant.ClientPhone, ""));
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_sale, R.id.nav_sale_order,R.id.nav_notification, R.id.nav_profile, R.id.nav_setting,R.id.nav_report,R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        notificationBadge=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_notification));
        getClientNotiCount(clientId);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavigationUI.onNavDestinationSelected(item, navController);
                if(item.getItemId()==R.id.nav_logout){
                    confirmation.showConfirmDialog(MainActivity.this,getResources().getString(R.string.exit_confirm_message));
                    return true;
                }
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
        if(notificationBadge != null){
            if(notiCount == 0) notificationBadge.setText("");
            else {
                notificationBadge.setGravity(Gravity.CENTER_VERTICAL);
                notificationBadge.setTypeface(null, Typeface.BOLD);
                notificationBadge.setTextColor(context.getResources().getColor(R.color.red));
                notificationBadge.setText(String.valueOf(notiCount));
            }
        }
    }

    private void getClientNotiCount(int clientId){
        Api.getClient().getClientNotiCount(clientId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int notiCount= response.body();
                setNotificationBadge(notiCount,context);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}