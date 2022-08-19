package com.bosictsolution.invsale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SaleOrderSuccessActivity extends AppCompatActivity {

    FloatingActionButton fab;
    TextView tvDate,tvOrderNumber,tvTotal;
    int total;
    String orderNumber;
    AppSetting appSetting=new AppSetting();
    DatabaseAccess db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_success);
        setLayoutResource();
        init();
        getSupportActionBar().hide();

        Intent i=getIntent();
        orderNumber=i.getStringExtra("OrderNumber");
        total=i.getIntExtra("Total",0);

        fillData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SaleOrderSummaryActivity.activity !=null)SaleOrderSummaryActivity.activity.finish();
                if(ProductActivity.activity !=null)ProductActivity.activity.finish();
                if(CategoryActivity.activity !=null)CategoryActivity.activity.finish();
                finish();
            }
        });
    }

    private void init(){
        db=new DatabaseAccess(this);
    }

    private void fillData(){
        tvDate.setText(appSetting.getTodayDate());
        tvOrderNumber.setText(orderNumber);
        tvTotal.setText(db.getHomeCurrency()+getResources().getString(R.string.space)+appSetting.df.format(total));
    }

    private void setLayoutResource(){
        fab=findViewById(R.id.fab);
        tvDate=findViewById(R.id.tvDate);
        tvOrderNumber=findViewById(R.id.tvOrderNumber);
        tvTotal=findViewById(R.id.tvTotal);
    }
}