package com.bosictsolution.invsale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.bosictsolution.invsale.common.AppSetting;

public class NoInternetActivity extends AppCompatActivity {

    ImageButton btnClose;
    Button btnRetry;
    Context context=this;
    AppSetting appSetting=new AppSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        getSupportActionBar().hide();
        setLayoutResource();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(appSetting.checkConnection(context)){
                    Intent intent = new Intent(context, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void setLayoutResource(){
        btnClose=findViewById(R.id.btnClose);
        btnRetry=findViewById(R.id.btnRetry);
    }
}