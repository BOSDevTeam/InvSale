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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.adapter.SaleOrderSummaryAdapter;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.CompanySettingData;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.CustomerData;
import com.bosictsolution.invsale.data.SaleOrderMasterData;
import com.bosictsolution.invsale.data.SaleOrderTranData;
import com.bosictsolution.invsale.listener.ListItemSaleListener;

import java.util.ArrayList;
import java.util.List;

public class SaleOrderSummaryActivity extends AppCompatActivity implements ListItemSaleListener {

    Button btnContinue;
    RecyclerView rvItemSaleOrder;
    TextView tvTax,tvSubtotal,tvCharges,tvTotal;
    EditText etRemark;
    Spinner spCustomer;
    SaleOrderSummaryAdapter saleOrderSummaryAdapter;
    LinearLayout layoutProcessCustomer;
    List<SaleOrderTranData> lstSaleOrderTran =new ArrayList<>();
    DatabaseAccess db;
    private ProgressDialog progressDialog;
    AppSetting appSetting=new AppSetting();
    private Context context=this;
    List<CustomerData> lstCustomer = new ArrayList<>();
    int tax, charges, total, subtotal , taxAmount, chargesAmount,clientId;
    SharedPreferences sharedpreferences;
    public static Activity activity;
    ConnectionLiveData connectionLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_summary);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.order_summary));

        checkConnection();
        fillData();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lstSaleOrderTran.size() == 0) return;
                if (lstCustomer.size() == 0) {
                    Toast.makeText(context, getResources().getString(R.string.customer_not_found), Toast.LENGTH_LONG).show();
                    return;
                }
                SaleOrderMasterData data = prepareSaleOrderMasterData();
                db.insertMasterSaleOrder(data);
                if (data.getCustomerID() == 0) {
                    Intent i = new Intent(SaleOrderSummaryActivity.this, CustomerActivity.class);
                    i.putExtra(AppConstant.extra_module_type, AppConstant.sale_order_module_type);
                    startActivity(i);
                } else
                    insertSaleOrder();
            }
        });
        spCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!lstCustomer.get(i).isDefault()){
                    btnContinue.setText(getResources().getString(R.string.order_confirm));
                    layoutProcessCustomer.setVisibility(View.GONE);
                }
                else {
                    btnContinue.setText(getResources().getString(R.string.str_continue));
                    layoutProcessCustomer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void insertSaleOrder(){
        SaleOrderMasterData data=db.getMasterSaleOrder();
        data.setLstSaleOrderTran(db.getTranSaleOrder());
        data.setClientID(clientId);
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().insertSaleOrder(data).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    db.deleteMasterSaleOrder();
                    db.deleteTranSaleOrder();
                    String orderNumber=response.body();
                    Intent i= new Intent(SaleOrderSummaryActivity.this, SaleOrderSuccessActivity.class);
                    i.putExtra("OrderNumber",orderNumber);
                    i.putExtra("Total",data.getTotal());
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init(){
        connectionLiveData = new ConnectionLiveData(context);
        activity=this;
        db=new DatabaseAccess(context);
        sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
        progressDialog =new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
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

    private void fillData(){
        clientId=sharedpreferences.getInt(AppConstant.ClientID,0);
        getCompanySetting();
        lstSaleOrderTran =db.getTranSaleOrder();
        setSaleOrderAdapter();
        calculateAmount();
        getCustomer();
    }

    private SaleOrderMasterData prepareSaleOrderMasterData(){
        int position,customerId;
        boolean isDefaultCustomer;
        String remark;

        position = spCustomer.getSelectedItemPosition();
        isDefaultCustomer = lstCustomer.get(position).isDefault();
        if (isDefaultCustomer) customerId = 0;
        else customerId = lstCustomer.get(position).getCustomerID();

        remark = etRemark.getText().toString();

        SaleOrderMasterData data=new SaleOrderMasterData();
        data.setCustomerID(customerId);
        data.setSubtotal(subtotal);
        data.setTax(tax);
        data.setTaxAmt(taxAmount);
        data.setCharges(charges);
        data.setChargesAmt(chargesAmount);
        data.setTotal(total);
        data.setRemark(remark);

        return data;
    }

    private void getCompanySetting(){
        CompanySettingData data=db.getTaxServiceCharges();
        if(data!=null){
            tax=data.getTax();
            charges =data.getServiceCharges();
        }
    }

    private void calculateAmount() {
        subtotal=0;
        for (int i = 0; i < lstSaleOrderTran.size(); i++) {
            subtotal += lstSaleOrderTran.get(i).getAmount();
        }
        taxAmount = (subtotal * tax) / 100;
        chargesAmount = (subtotal * charges) / 100;
        total = subtotal + taxAmount + chargesAmount;
        tvTax.setText(appSetting.df.format(taxAmount));
        tvCharges.setText(appSetting.df.format(chargesAmount));
        tvSubtotal.setText(appSetting.df.format(subtotal));
        tvTotal.setText(appSetting.df.format(total));
    }

    private void getCustomer() {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getCustomer().enqueue(new Callback<List<CustomerData>>() {
            @Override
            public void onResponse(Call<List<CustomerData>> call, Response<List<CustomerData>> response) {
                progressDialog.dismiss();
                lstCustomer = response.body();
                setCustomer();
            }

            @Override
            public void onFailure(Call<List<CustomerData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCustomer() {
        if(lstCustomer.size()!=0) {
            String[] customers = new String[lstCustomer.size()];
            for (int i = 0; i < lstCustomer.size(); i++) {
                customers[i] = lstCustomer.get(i).getCustomerName();
            }
            ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, customers);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCustomer.setAdapter(adapter);
        }
    }

    private void setSaleOrderAdapter(){
        while (rvItemSaleOrder.getItemDecorationCount() > 0) {
            rvItemSaleOrder.removeItemDecorationAt(0);
        }
        saleOrderSummaryAdapter =new SaleOrderSummaryAdapter(this, lstSaleOrderTran,true);
        rvItemSaleOrder.setAdapter(saleOrderSummaryAdapter);
        rvItemSaleOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvItemSaleOrder.addItemDecoration(new DividerItemDecoration(rvItemSaleOrder.getContext(), DividerItemDecoration.VERTICAL));
        saleOrderSummaryAdapter.setOnListener(this);
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
        rvItemSaleOrder=findViewById(R.id.rvItemSaleOrder);
        spCustomer=findViewById(R.id.spCustomer);
        tvTax=findViewById(R.id.tvTax);
        tvCharges=findViewById(R.id.tvCharges);
        tvSubtotal=findViewById(R.id.tvSubtotal);
        tvTotal=findViewById(R.id.tvTotal);
        etRemark=findViewById(R.id.etRemark);
        layoutProcessCustomer=findViewById(R.id.layoutProcessCustomer);
    }

    @Override
    public void onQuantityClickListener(int position, TextView tvQuantity, TextView tvAmount) {
        showNumberDialog(lstSaleOrderTran.get(position).getProductName(),lstSaleOrderTran.get(position).getQuantity(),position,tvQuantity,tvAmount);
    }

    @Override
    public void onRemoveClickListener(int position) {
        if(db.deleteTranSaleOrderByProduct(lstSaleOrderTran.get(position).getProductID())) {
            lstSaleOrderTran.remove(position);
            setSaleOrderAdapter();
            calculateAmount();
        }
    }

    @Override
    public void onItemLongClickListener(int position, TextView tvPrice, TextView tvAmount) {

    }

    private void showNumberDialog(String productName,int quantity,int position,TextView tvQuantity, TextView tvAmount) {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_number, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final ImageButton btnBackspace = v.findViewById(R.id.btnBackspace);
        final TextView tvTitle = v.findViewById(R.id.tvTitle);
        final TextView tvInput = v.findViewById(R.id.tvInput);
        final Button btnOK = v.findViewById(R.id.btnOK);
        final Button btnClear = v.findViewById(R.id.btnClear);
        final Button btnZero = v.findViewById(R.id.btnZero);
        final Button btnOne = v.findViewById(R.id.btnOne);
        final Button btnTwo = v.findViewById(R.id.btnTwo);
        final Button btnThree = v.findViewById(R.id.btnThree);
        final Button btnFour = v.findViewById(R.id.btnFour);
        final Button btnFive = v.findViewById(R.id.btnFive);
        final Button btnSix = v.findViewById(R.id.btnSix);
        final Button btnSeven = v.findViewById(R.id.btnSeven);
        final Button btnEight = v.findViewById(R.id.btnEight);
        final Button btnNine = v.findViewById(R.id.btnNine);

        tvTitle.setText(productName);
        tvInput.setText(String.valueOf(quantity));

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value=tvInput.getText().toString();
                if(value.length()!=0){
                    value=value.substring(0,value.length()-1);
                    tvInput.setText(value);
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvInput.setText("0");
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (!tvInput.getText().toString().equals("0") && tvInput.getText().toString().length()!=0) {
                    int quantity = Integer.parseInt(tvInput.getText().toString());
                    if (db.insertUpdateTranSaleOrder(lstSaleOrderTran.get(position).getProductID(), quantity)) {
                        updateControlByQuantity(position, tvQuantity, tvAmount, quantity);
                        updateListByQuantity(position, quantity);
                        calculateAmount();
                    }
                }
            }
        });
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,0);
            }
        });
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,1);
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,2);
            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,3);
            }
        });
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,4);
            }
        });
        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,5);
            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,6);
            }
        });
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,7);
            }
        });
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,8);
            }
        });
        btnNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput,9);
            }
        });
    }

    private void setNumberValue(TextView textView,int inputValue) {
        String value = textView.getText().toString();
        if(value.startsWith("0"))value="";
        value += inputValue;
        textView.setText(value);
    }

    private void updateControlByQuantity(int position, TextView tvQuantity, TextView tvAmount, int quantity) {
        tvQuantity.setText(String.valueOf(quantity));
        tvAmount.setText(appSetting.df.format(quantity * lstSaleOrderTran.get(position).getSalePrice()));
    }

    private void updateListByQuantity(int position,int quantity) {
        lstSaleOrderTran.get(position).setQuantity(quantity);
        lstSaleOrderTran.get(position).setAmount(quantity * lstSaleOrderTran.get(position).getSalePrice());
    }
}