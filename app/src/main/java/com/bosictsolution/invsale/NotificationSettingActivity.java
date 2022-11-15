package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bosictsolution.invsale.common.AppConstant;

public class NotificationSettingActivity extends AppCompatActivity {

    Switch swtAccessNoti;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.notification_setting));

        fillData();

        swtAccessNoti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                if (isChecked) editor.putBoolean(AppConstant.ACCESS_NOTI, true);
                else editor.putBoolean(AppConstant.ACCESS_NOTI, false);
                editor.commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillData() {
        boolean isAccessNoti = sharedpreferences.getBoolean(AppConstant.ACCESS_NOTI, false);
        if (isAccessNoti) swtAccessNoti.setChecked(true);
        else swtAccessNoti.setChecked(false);
    }

    private void init(){
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
    }

    private void setLayoutResource(){
        swtAccessNoti=findViewById(R.id.swtAccessNoti);
    }
}