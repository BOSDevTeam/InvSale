package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.bosictsolution.invsale.adapter.ListItemLocationAdapter;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.LocationData;

import java.util.List;

public class LocationSettingActivity extends AppCompatActivity implements ListItemLocationAdapter.IListener{

    RecyclerView rvLocation;
    DatabaseAccess db;
    final Context context = this;
    ListItemLocationAdapter listItemLocationAdapter;
    List<LocationData> lstLocation;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_setting);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.primary_500));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.location_setting));
        fillData();
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
        int locationId = sharedpreferences.getInt(AppConstant.LOCATION_ID, 0);
        lstLocation = db.getLocation();

        if(locationId != 0){
            for(int i=0;i<lstLocation.size();i++){
                if(lstLocation.get(i).getLocationID() == locationId) {
                    lstLocation.get(i).setSelected(true);
                    break;
                }
            }
        }

        listItemLocationAdapter = new ListItemLocationAdapter(lstLocation,context);
        rvLocation.setAdapter(listItemLocationAdapter);
        rvLocation.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listItemLocationAdapter.setListener(this);
    }

    private void init() {
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        db = new DatabaseAccess(context);
    }

    private void setLayoutResource(){
        rvLocation=findViewById(R.id.rvLocation);
    }

    @Override
    public void onClicked(int position) {
        for(int i=0;i<lstLocation.size();i++){
            lstLocation.get(i).setSelected(false);
        }
        lstLocation.get(position).setSelected(true);
        listItemLocationAdapter.updateData(lstLocation);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(AppConstant.LOCATION_ID, lstLocation.get(position).getLocationID());
        editor.commit();
        Toast.makeText(context,getResources().getString(R.string.selected), Toast.LENGTH_LONG).show();
    }
}