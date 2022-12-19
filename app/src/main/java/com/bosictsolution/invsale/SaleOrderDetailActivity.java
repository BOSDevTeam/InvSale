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
import android.graphics.drawable.ColorDrawable;
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
import com.bosictsolution.invsale.data.SaleOrderMasterData;
import com.bosictsolution.invsale.data.SaleOrderTranData;

import java.util.List;

public class SaleOrderDetailActivity extends AppCompatActivity {

    RecyclerView rvItemSaleOrder;
    TextView tvOrderNumber,tvOrderDateTime,tvTax,tvSubtotal,tvCharges,tvTotal,tvRemark;
    LinearLayout layoutRemark;
    SaleOrderSummaryAdapter saleOrderSummaryAdapter;
    int saleOrderId;
    AppSetting appSetting=new AppSetting();
    private ProgressDialog progressDialog;
    private Context context=this;
    ConnectionLiveData connectionLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_detail);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.primary_500));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.order_detail));

        checkConnection();

        Intent i = getIntent();
        SaleOrderMasterData data = new SaleOrderMasterData();
        saleOrderId = i.getIntExtra("SaleOrderID", 0);
        boolean isFromNotification = i.getBooleanExtra("IsFromNotification", false);
        if (!isFromNotification) {
            data.setOrderNumber(i.getStringExtra("OrderNumber"));
            data.setOrderDateTime(i.getStringExtra("OrderDateTime"));
            data.setTaxAmt(i.getIntExtra("TaxAmt", 0));
            data.setChargesAmt(i.getIntExtra("ChargesAmt", 0));
            data.setSubtotal(i.getIntExtra("Subtotal", 0));
            data.setTotal(i.getIntExtra("Total", 0));
            data.setRemark(i.getStringExtra("Remark"));
            fillData(data);
        } else getMasterSaleOrderBySaleOrderID(saleOrderId);
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

    private void fillData(SaleOrderMasterData data) {
        tvOrderNumber.setText(getResources().getString(R.string.hash) + data.getOrderNumber());
        tvOrderDateTime.setText(data.getOrderDateTime());
        tvTax.setText(appSetting.df.format(data.getTaxAmt()));
        tvCharges.setText(appSetting.df.format(data.getChargesAmt()));
        tvSubtotal.setText(appSetting.df.format(data.getSubtotal()));
        tvTotal.setText(appSetting.df.format(data.getTotal()));
        if (data.getRemark().length() == 0) layoutRemark.setVisibility(View.GONE);
        else {
            layoutRemark.setVisibility(View.VISIBLE);
            tvRemark.setText(data.getRemark());
        }
        getTranSaleOrderBySaleOrderID(saleOrderId);
    }

    private void setAdapter(List<SaleOrderTranData> list){
        saleOrderSummaryAdapter=new SaleOrderSummaryAdapter(this, list,false);
        rvItemSaleOrder.setAdapter(saleOrderSummaryAdapter);
        rvItemSaleOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvItemSaleOrder.addItemDecoration(new DividerItemDecoration(rvItemSaleOrder.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void getMasterSaleOrderBySaleOrderID(int saleOrderId){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getMasterSaleOrderBySaleOrderID(saleOrderId).enqueue(new Callback<SaleOrderMasterData>() {
            @Override
            public void onResponse(Call<SaleOrderMasterData> call, Response<SaleOrderMasterData> response) {
                progressDialog.dismiss();
                if (response.body() == null){
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    return;
                }
                SaleOrderMasterData data=response.body();
                fillData(data);
            }

            @Override
            public void onFailure(Call<SaleOrderMasterData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getTranSaleOrderBySaleOrderID(int saleOrderId){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getTranSaleOrderBySaleOrderID(saleOrderId).enqueue(new Callback<List<SaleOrderTranData>>() {
            @Override
            public void onResponse(Call<List<SaleOrderTranData>> call, Response<List<SaleOrderTranData>> response) {
                progressDialog.dismiss();
                if (response.body() == null){
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    return;
                }
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
        tvTax=findViewById(R.id.tvTax);
        tvSubtotal=findViewById(R.id.tvSubtotal);
        tvCharges=findViewById(R.id.tvCharges);
        tvTotal=findViewById(R.id.tvTotal);
        tvRemark=findViewById(R.id.tvRemark);
        layoutRemark=findViewById(R.id.layoutRemark);
    }
}