package com.bosictsolution.invsale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.data.ConnectionData;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    Spinner spDivision,spTownship;
    AppSetting appSetting=new AppSetting();
    String[] divisions={"Yangon", "Mandalay", "Ayeyarwaddy"};
    String[] townships={"Kamaryut", "Sanchaung", "Hlaing", "Yankin"};

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

        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,divisions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(adapter);

        adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,townships);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTownship.setAdapter(adapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void setLayoutResource(){
        btnRegister=findViewById(R.id.btnRegister);
        spDivision=findViewById(R.id.spDivision);
        spTownship=findViewById(R.id.spTownship);
    }
}