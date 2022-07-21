package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.bosictsolution.invsale.adapter.ListItemSaleAdapter;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.listener.ListItemSaleListener;

import java.util.ArrayList;
import java.util.List;

public class SaleOrderDetailActivity extends AppCompatActivity implements ListItemSaleListener {

    ListView lvItemSaleOrder;
    ListItemSaleAdapter listItemSaleAdapter;
    List<SaleTranData> lstSaleTran=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_detail);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.order_detail));

        setAdapter();
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

        listItemSaleAdapter=new ListItemSaleAdapter(this,lstSaleTran,false);
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

    private void setLayoutResource() {
        lvItemSaleOrder=findViewById(R.id.lvItemSaleOrder);
    }

    @Override
    public void onQuantityClickListener(int position, TextView tvQuantity, TextView tvAmount) {

    }

    @Override
    public void onRemoveClickListener(int position) {

    }
}