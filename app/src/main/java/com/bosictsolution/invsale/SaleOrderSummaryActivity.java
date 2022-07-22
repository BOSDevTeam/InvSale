package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bosictsolution.invsale.adapter.ListItemSaleAdapter;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.listener.ListItemSaleListener;

import java.util.ArrayList;
import java.util.List;

public class SaleOrderSummaryActivity extends AppCompatActivity implements ListItemSaleListener {

    Button btnContinue;
    ListView lvItemSaleOrder;
    Spinner spCustomer;
    ListItemSaleAdapter listItemSaleAdapter;
    List<SaleTranData> lstSaleTran=new ArrayList<>();

    String[] customers={"Walk in Client", "CustomerA", "CustomerB"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_summary);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.order_summary));

        fillData();
        setAdapter();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SaleOrderSummaryActivity.this,CustomerActivity.class);
                i.putExtra(AppConstant.extra_module_type,AppConstant.sale_order_module_type);
                startActivity(i);
            }
        });
    }

    private void fillData(){
        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,customers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCustomer.setAdapter(adapter);
    }

    private void setAdapter(){
        SaleTranData data=new SaleTranData();
        data.setNumber(1);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setTotalAmount(2000);
        lstSaleTran.add(data);

        data=new SaleTranData();
        data.setNumber(2);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setTotalAmount(2000);
        lstSaleTran.add(data);

        data=new SaleTranData();
        data.setNumber(3);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setTotalAmount(2000);
        lstSaleTran.add(data);

        listItemSaleAdapter=new ListItemSaleAdapter(this,lstSaleTran,true);
        lvItemSaleOrder.setAdapter(listItemSaleAdapter);
        listItemSaleAdapter.setOnListener(this);
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
        btnContinue=findViewById(R.id.btnContinue);
        lvItemSaleOrder=findViewById(R.id.lvItemSaleOrder);
        spCustomer=findViewById(R.id.spCustomer);
    }

    @Override
    public void onQuantityClickListener(int position, TextView tvQuantity, TextView tvAmount) {

    }

    @Override
    public void onRemoveClickListener(int position) {

    }

    @Override
    public void onItemLongClickListener(int position, TextView tvPrice, TextView tvAmount) {

    }
}