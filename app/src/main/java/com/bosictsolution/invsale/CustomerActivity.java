package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.bosictsolution.invsale.common.AppConstant;

public class CustomerActivity extends AppCompatActivity {

    Button btnConfirm;
    Spinner spDivision,spTownship;
    String[] divisions={"Yangon", "Mandalay", "Ayeyarwaddy"};
    String[] townships={"Kamaryut", "Sanchaung", "Hlaing", "Yankin"};
    short moduleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.customer_detail));

        Intent i=getIntent();
        moduleType=i.getShortExtra(AppConstant.extra_module_type,Short.MIN_VALUE);

        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,divisions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(adapter);

        adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,townships);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTownship.setAdapter(adapter);

        if(moduleType==AppConstant.sale_module_type)btnConfirm.setText(getResources().getString(R.string.pay_confirm));
        else if(moduleType==AppConstant.sale_order_module_type)btnConfirm.setText(getResources().getString(R.string.order_confirm));

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if(moduleType==AppConstant.sale_module_type) {
                    intent = new Intent(CustomerActivity.this, SaleBillActivity.class);
                }else if(moduleType==AppConstant.sale_order_module_type){
                    intent = new Intent(CustomerActivity.this, SaleOrderSuccessActivity.class);
                }
                if(intent!=null) {
                    startActivity(intent);
                    finish();
                }
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

    private void setLayoutResource(){
        spDivision=findViewById(R.id.spDivision);
        spTownship=findViewById(R.id.spTownship);
        btnConfirm=findViewById(R.id.btnConfirm);
    }
}