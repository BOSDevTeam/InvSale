package com.bosictsolution.invsale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.DivisionData;
import com.bosictsolution.invsale.data.TownshipData;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    Spinner spDivision,spTownship;
    AppSetting appSetting=new AppSetting();
    List<DivisionData> lstDivision=new ArrayList<>();
    List<TownshipData> lstTownship=new ArrayList<>();
    private Context context=this;
    private ProgressDialog progressDialog;
    boolean divisionResult,townshipResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        setLayoutResource();

        ConnectionLiveData connectionLiveData=new ConnectionLiveData(this);
        connectionLiveData.observe(this, new Observer<ConnectionData>() {
            @Override
            public void onChanged(ConnectionData connectionData) {
                if(!connectionData.getIsConnected())appSetting.showSnackBar(findViewById(R.id.layoutRoot));
            }
        });

        fillData();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void fillData(){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        if(getDivision()){
            if(getTownshipByDivision(lstDivision.get(0).getDivisionID())){
                progressDialog.dismiss();
                setDivision();
                setTownship();
            }
        }
    }

    private boolean getDivision() {
        Api.getClient().getDivision().enqueue(new Callback<List<DivisionData>>() {
            @Override
            public void onResponse(Call<List<DivisionData>> call, Response<List<DivisionData>> response) {
                lstDivision = response.body();
                if (lstDivision.size() != 0) divisionResult =true;
            }

            @Override
            public void onFailure(Call<List<DivisionData>> call, Throwable t) {
                Log.e("RegisterActivity", t.getMessage());
                progressDialog.dismiss();
            }
        });
        return divisionResult;
    }

    private boolean getTownshipByDivision(int divisionId) {
        Api.getClient().getTownshipByDivision(divisionId).enqueue(new Callback<List<TownshipData>>() {
            @Override
            public void onResponse(Call<List<TownshipData>> call, Response<List<TownshipData>> response) {
                lstTownship = response.body();
                townshipResult=true;
            }

            @Override
            public void onFailure(Call<List<TownshipData>> call, Throwable t) {
                Log.e("RegisterActivity", t.getMessage());
                progressDialog.dismiss();
            }
        });
        return townshipResult;
    }

    private void setDivision(){
        String[] divisions = new String[lstDivision.size()];
        for (int i = 0; i < lstDivision.size(); i++) {
            divisions[i] = lstDivision.get(i).getDivisionName();
        }
        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, divisions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(adapter);
    }

    private void setTownship(){
        String[] townships = new String[lstTownship.size()];
        for (int i = 0; i < lstTownship.size(); i++) {
            townships[i] = lstTownship.get(i).getTownshipName();
        }
        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, townships);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTownship.setAdapter(adapter);
    }

    private void setLayoutResource(){
        btnRegister=findViewById(R.id.btnRegister);
        spDivision=findViewById(R.id.spDivision);
        spTownship=findViewById(R.id.spTownship);

        progressDialog =new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }
}