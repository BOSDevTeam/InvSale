package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.adapter.SaleOrderSummaryAdapter;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.SaleOrderTranData;

import java.util.List;

public class SaleOrderDetailActivity extends AppCompatActivity {

    RecyclerView rvItemSaleOrder;
    TextView tvOrderNumber,tvOrderDateTime,tvCustomer,tvTax,tvSubtotal,tvCharges,tvTotal,tvRemark;
    LinearLayout layoutRemark;
    SaleOrderSummaryAdapter saleOrderSummaryAdapter;
    int saleOrderId,tax,charges,subtotal,total;
    AppSetting appSetting=new AppSetting();
    private ProgressDialog progressDialog;
    private Context context=this;
    String remark,orderNumber,orderDataTime,customerName;
    ConnectionLiveData connectionLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_detail);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.order_detail));

        Intent i = getIntent();
        saleOrderId = i.getIntExtra("SaleOrderID", 0);
        orderNumber = i.getStringExtra("OrderNumber");
        orderDataTime = i.getStringExtra("OrderDateTime");
        customerName = i.getStringExtra("CustomerName");
        tax = i.getIntExtra("TaxAmt", 0);
        charges = i.getIntExtra("ChargesAmt", 0);
        subtotal = i.getIntExtra("Subtotal", 0);
        total = i.getIntExtra("Total", 0);
        remark = i.getStringExtra("Remark");

        checkConnection();
        fillData();
    }

    private void checkConnection(){
        connectionLiveData.observe(this, new Observer<ConnectionData>() {
            @Override
            public void onChanged(ConnectionData connectionData) {
                if (!connectionData.getIsConnected())
                    appSetting.showSnackBar(findViewById(R.id.layoutRoot));
            }
        });
    }

    private void fillData() {
        tvOrderNumber.setText(getResources().getString(R.string.hash) + orderNumber);
        tvOrderDateTime.setText(orderDataTime);
        tvCustomer.setText(customerName);
        tvTax.setText(appSetting.df.format(tax));
        tvCharges.setText(appSetting.df.format(charges));
        tvSubtotal.setText(appSetting.df.format(subtotal));
        tvTotal.setText(appSetting.df.format(total));
        if (remark.length() == 0) layoutRemark.setVisibility(View.GONE);
        else {
            layoutRemark.setVisibility(View.VISIBLE);
            tvRemark.setText(remark);
        }
        getTranSaleOrderBySaleOrderID(saleOrderId);
    }

    private void setAdapter(List<SaleOrderTranData> list){
        saleOrderSummaryAdapter=new SaleOrderSummaryAdapter(this, list,false);
        rvItemSaleOrder.setAdapter(saleOrderSummaryAdapter);
        rvItemSaleOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvItemSaleOrder.addItemDecoration(new DividerItemDecoration(rvItemSaleOrder.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void getTranSaleOrderBySaleOrderID(int saleOrderId){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getTranSaleOrderBySaleOrderID(saleOrderId).enqueue(new Callback<List<SaleOrderTranData>>() {
            @Override
            public void onResponse(Call<List<SaleOrderTranData>> call, Response<List<SaleOrderTranData>> response) {
                progressDialog.dismiss();
                List<SaleOrderTranData> list =response.body();
                setAdapter(list);
            }

            @Override
            public void onFailure(Call<List<SaleOrderTranData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
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

    private void init(){
        connectionLiveData = new ConnectionLiveData(context);
        progressDialog =new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
    }

    private void setLayoutResource() {
        rvItemSaleOrder=findViewById(R.id.rvItemSaleOrder);
        tvOrderNumber=findViewById(R.id.tvOrderNumber);
        tvOrderDateTime=findViewById(R.id.tvOrderDateTime);
        tvCustomer=findViewById(R.id.tvCustomer);
        tvTax=findViewById(R.id.tvTax);
        tvSubtotal=findViewById(R.id.tvSubtotal);
        tvCharges=findViewById(R.id.tvCharges);
        tvTotal=findViewById(R.id.tvTotal);
        tvRemark=findViewById(R.id.tvRemark);
        layoutRemark=findViewById(R.id.layoutRemark);
    }
}