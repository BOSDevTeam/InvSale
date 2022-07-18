package com.bosictsolution.invsale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;

public class SplashActivity extends AppCompatActivity {

    AppSetting appSetting=new AppSetting();
    Context context=this;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (appSetting.checkConnection(context)) {
                    int clientId = sharedpreferences.getInt(AppConstant.ClientID, 0);
                    Intent i;
                    if (clientId == 0) {
                        i = new Intent(SplashActivity.this, RegisterActivity.class);
                    } else {
                        i = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashActivity.this, NoInternetActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, 3000);
    }
}