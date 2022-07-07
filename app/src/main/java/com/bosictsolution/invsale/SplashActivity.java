package com.bosictsolution.invsale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bosictsolution.invsale.common.AppSetting;

public class SplashActivity extends AppCompatActivity {

    AppSetting appSetting=new AppSetting();
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                if(appSetting.checkConnection(context)){
                    Intent i=new Intent(SplashActivity.this,RegisterActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i=new Intent(SplashActivity.this,NoInternetActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        }, 3000);
    }
}