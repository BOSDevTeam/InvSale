package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bosictsolution.invsale.adapter.ListItemSaleAdapter;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.listener.ListItemSaleListener;

import java.util.ArrayList;
import java.util.List;

public class SaleOrderSummaryActivity extends AppCompatActivity implements ListItemSaleListener {

    Button btnSendOrder;
    ListView lvItemSaleOrder;
    Spinner spPaymentMethod,spBankPayment;
    LinearLayout layoutOnlinePayment;
    ListItemSaleAdapter listItemSaleAdapter;
    List<SaleTranData> lstSaleTran=new ArrayList<>();

    String[] paymentMethods={"Cash in Hand", "Online Payment"};
    String[] bankPayments={"KBZ Pay", "CB Pay"};

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
        setLayoutOnlinePayment();

        btnSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SaleOrderSummaryActivity.this,SaleOrderSuccessActivity.class);
                startActivity(i);
                finish();
            }
        });
        spPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setLayoutOnlinePayment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setLayoutOnlinePayment(){
        if(spPaymentMethod.getSelectedItemPosition()==0)  // if payment method is cashInHand
            layoutOnlinePayment.setVisibility(View.GONE);
        else if(spPaymentMethod.getSelectedItemPosition()==1)  // if payment method is onlinePayment
            layoutOnlinePayment.setVisibility(View.VISIBLE);
    }

    private void fillData(){
        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentMethod.setAdapter(adapter);

        adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,bankPayments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBankPayment.setAdapter(adapter);
    }

    private void setAdapter(){
        SaleTranData data=new SaleTranData();
        data.setNumber(1);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setAmount(2000);
        lstSaleTran.add(data);

        data=new SaleTranData();
        data.setNumber(2);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setAmount(2000);
        lstSaleTran.add(data);

        data=new SaleTranData();
        data.setNumber(3);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setAmount(2000);
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
        btnSendOrder=findViewById(R.id.btnSendOrder);
        lvItemSaleOrder=findViewById(R.id.lvItemSaleOrder);
        layoutOnlinePayment=findViewById(R.id.layoutOnlinePayment);
        spPaymentMethod=findViewById(R.id.spPaymentMethod);
        spBankPayment=findViewById(R.id.spBankPayment);
    }

    @Override
    public void onQuantityClickListener(int position, TextView tvQuantity, TextView tvAmount) {

    }

    @Override
    public void onRemoveClickListener(int position) {

    }
}