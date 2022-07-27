package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.BankPaymentData;
import com.bosictsolution.invsale.data.CustomerData;
import com.bosictsolution.invsale.data.LimitedDayData;
import com.bosictsolution.invsale.data.LocationData;
import com.bosictsolution.invsale.data.PaymentData;
import com.bosictsolution.invsale.data.PaymentMethodData;

import java.util.ArrayList;
import java.util.List;

public class PayDetailActivity extends AppCompatActivity {

    Spinner spCustomer,spLocation,spPayment,spPaymentMethod,spBankPayment,spLimitedDay;
    Button btnLimitDayAdvancedPayOk,btnDollar,btnPercent,btnPaidOk,btnPaymentPercentOk,btnContinue;
    RadioButton rdoNoDiscount;
    LinearLayout layoutPaymentDebt,layoutPaymentMethod,layoutPaidChange,layoutOnlinePayment,layoutAdvancedPay;
    EditText etAdvancedPay,etVoucherDiscount;
    TextView tvAdvancedPay,tvTotal,tvVoucherDiscount,tvGrandTotal,tvGrandTotalBottom;

    List<CustomerData> lstCustomer=new ArrayList<>();
    List<LocationData> lstLocation=new ArrayList<>();
    List<PaymentData> lstPayment=new ArrayList<>();
    List<PaymentMethodData> lstPaymentMethod=new ArrayList<>();
    List<BankPaymentData> lstBankPayment=new ArrayList<>();
    List<LimitedDayData> lstLimitedDay=new ArrayList<>();
    private ProgressDialog progressDialog;
    private Context context=this;
    int voucherDiscountType,discountPercentType=1,discountAmountType=2,
            total,grandTotal, voucherDiscountAmount,advancedPay;
    AppSetting appSetting=new AppSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle("#S0001");

        Intent i=getIntent();
        total=i.getIntExtra("Total",0);

        fillData();
        setLayoutPaymentDebt();
        setLayoutPaymentMethod();
        setLayoutPaidChange();
        setLayoutOnlinePayment();
        setLayoutAdvancedPay();

        spPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setLayoutPaymentDebt();
                setLayoutPaymentMethod();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setLayoutPaidChange();
                setLayoutOnlinePayment();
                setLayoutAdvancedPay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnLimitDayAdvancedPayOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etAdvancedPay.getText().toString().length()==0)return;
                if (Integer.parseInt(etAdvancedPay.getText().toString()) != 0) {
                    if (Integer.parseInt(etAdvancedPay.getText().toString()) <= grandTotal) {
                        advancedPay = Integer.parseInt(etAdvancedPay.getText().toString());
                        tvAdvancedPay.setText(appSetting.df.format(Integer.parseInt(etAdvancedPay.getText().toString())));
                        calculateGrandTotal();
                        setLayoutPaymentMethod();
                    } else
                        Toast.makeText(context, getResources().getString(R.string.invalid_advanced_pay), Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, getResources().getString(R.string.enter_valid_value), Toast.LENGTH_LONG).show();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(PayDetailActivity.this,CustomerActivity.class);
                i.putExtra(AppConstant.extra_module_type,AppConstant.sale_module_type);
                startActivity(i);
            }
        });
        btnDollar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVoucherDiscountType(discountAmountType);
            }
        });
        btnPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVoucherDiscountType(discountPercentType);
            }
        });
        etVoucherDiscount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(etVoucherDiscount.getText().toString().length()!=0) {
                        int voucherDiscountValue = Integer.parseInt(etVoucherDiscount.getText().toString());
                        if (voucherDiscountType == discountPercentType) {
                            if (voucherDiscountValue > 100) {
                                Toast.makeText(context, getResources().getString(R.string.invalid_percent_value), Toast.LENGTH_LONG).show();
                            } else {
                                voucherDiscountAmount = (total * voucherDiscountValue) / 100;
                                rdoNoDiscount.setChecked(false);
                                tvVoucherDiscount.setText(appSetting.df.format(voucherDiscountAmount));
                                calculateGrandTotal();
                            }
                        } else if (voucherDiscountType == discountAmountType) {
                            if (voucherDiscountValue > grandTotal) {
                                Toast.makeText(context, getResources().getString(R.string.invalid_discount_amount), Toast.LENGTH_LONG).show();
                            } else {
                                voucherDiscountAmount = voucherDiscountValue;
                                rdoNoDiscount.setChecked(false);
                                tvVoucherDiscount.setText(appSetting.df.format(voucherDiscountAmount));
                                calculateGrandTotal();
                            }
                        }
                    }
                }
                return false;
            }
        });
        rdoNoDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rdoNoDiscount.isChecked())clearVoucherDiscount();
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

    private void clearVoucherDiscount(){
        voucherDiscountAmount=0;
        voucherDiscountType=discountPercentType;
        btnPercent.setBackground(getResources().getDrawable(R.drawable.btn_primary));
        btnPercent.setTextColor(getResources().getColor(R.color.white));
        btnDollar.setBackground(getResources().getDrawable(R.drawable.btn_gray));
        btnDollar.setTextColor(getResources().getColor(R.color.black_soft));
        etVoucherDiscount.setText("");
        etVoucherDiscount.setHint(getResources().getString(R.string.zero));
        tvVoucherDiscount.setText("0");
        calculateGrandTotal();
    }

    private void changeVoucherDiscountType(int type) {
        voucherDiscountType = type;
        rdoNoDiscount.setChecked(true);
        voucherDiscountAmount = 0;
        etVoucherDiscount.setText("");
        tvVoucherDiscount.setText("0");
        calculateGrandTotal();

        if (type == discountPercentType) {
            btnPercent.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            btnPercent.setTextColor(getResources().getColor(R.color.white));
            btnDollar.setBackground(getResources().getDrawable(R.drawable.btn_gray));
            btnDollar.setTextColor(getResources().getColor(R.color.black_soft));
            etVoucherDiscount.setHint(getResources().getString(R.string.zero));
        } else if (type == discountAmountType) {
            btnDollar.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            btnDollar.setTextColor(getResources().getColor(R.color.white));
            btnPercent.setBackground(getResources().getDrawable(R.drawable.btn_gray));
            btnPercent.setTextColor(getResources().getColor(R.color.black_soft));
            etVoucherDiscount.setHint(getResources().getString(R.string.amount));
        }
    }

    private void setLayoutAdvancedPay(){
        if(etAdvancedPay.getText().toString().length()==0)  // if not have advanced pay
            layoutAdvancedPay.setVisibility(View.GONE);
        else layoutAdvancedPay.setVisibility(View.VISIBLE);
    }

    private void setLayoutOnlinePayment(){
        if(spPaymentMethod.getSelectedItemPosition()==0)  // if payment method is cashInHand
            layoutOnlinePayment.setVisibility(View.GONE);
        else if(spPaymentMethod.getSelectedItemPosition()==1)  // if payment method is onlinePayment
            layoutOnlinePayment.setVisibility(View.VISIBLE);
    }

    private void setLayoutPaidChange(){
        if(spPaymentMethod.getSelectedItemPosition()==0)  // if payment method is cashInHand
            layoutPaidChange.setVisibility(View.VISIBLE);
        else if(spPaymentMethod.getSelectedItemPosition()==1)  // if payment method is onlinePayment
            layoutPaidChange.setVisibility(View.GONE);
    }

    private void setLayoutPaymentMethod(){
        if(spPayment.getSelectedItemPosition()==1 && etAdvancedPay.getText().toString().length()==0)
            layoutPaymentMethod.setVisibility(View.GONE);  // if payment is debt, if not have advanced pay
        else layoutPaymentMethod.setVisibility(View.VISIBLE);
    }

    private void setLayoutPaymentDebt(){
        if(spPayment.getSelectedItemPosition()==0)layoutPaymentDebt.setVisibility(View.GONE);
        else if(spPayment.getSelectedItemPosition()==1)layoutPaymentDebt.setVisibility(View.VISIBLE);
    }

    private void fillData(){
        voucherDiscountType=discountPercentType;
        tvTotal.setText(appSetting.df.format(total));
        calculateGrandTotal();
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        getCustomer();
    }

    private void calculateGrandTotal(){
        grandTotal=total-(advancedPay+voucherDiscountAmount);
        tvGrandTotal.setText(appSetting.df.format(grandTotal));
        tvGrandTotalBottom.setText(appSetting.df.format(grandTotal));
    }

    private void setCustomer(){
        String[] customers = new String[lstCustomer.size()];
        for (int i = 0; i < lstCustomer.size(); i++) {
            customers[i] = lstCustomer.get(i).getCustomerName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, customers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCustomer.setAdapter(adapter);
    }

    private void setLocation(){
        String[] locations = new String[lstLocation.size()];
        for (int i = 0; i < lstLocation.size(); i++) {
            locations[i] = lstLocation.get(i).getShortName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocation.setAdapter(adapter);
        spLocation.setEnabled(false);
    }

    private void setPayment(){
        String[] payments = new String[lstPayment.size()];
        for (int i = 0; i < lstPayment.size(); i++) {
            payments[i] = lstPayment.get(i).getKeyword();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, payments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPayment.setAdapter(adapter);
    }

    private void setPaymentMethod(){
        String[] paymentMethods = new String[lstPaymentMethod.size()];
        for (int i = 0; i < lstPaymentMethod.size(); i++) {
            paymentMethods[i] = lstPaymentMethod.get(i).getPayMethodName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentMethod.setAdapter(adapter);
    }

    private void setBankPayment(){
        String[] bankPayments = new String[lstBankPayment.size()];
        for (int i = 0; i < lstBankPayment.size(); i++) {
            bankPayments[i] = lstBankPayment.get(i).getBankPaymentName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, bankPayments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBankPayment.setAdapter(adapter);
    }

    private void setLimitedDay(){
        String[] limitedDays = new String[lstLimitedDay.size()];
        for (int i = 0; i < lstLimitedDay.size(); i++) {
            limitedDays[i] = lstLimitedDay.get(i).getLimitedDayName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, limitedDays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLimitedDay.setAdapter(adapter);
    }

    private void getCustomer(){
        Api.getClient().getCustomer().enqueue(new Callback<List<CustomerData>>() {
            @Override
            public void onResponse(Call<List<CustomerData>> call, Response<List<CustomerData>> response) {
                lstCustomer=response.body();
                setCustomer();
                getLocation();
            }

            @Override
            public void onFailure(Call<List<CustomerData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("PayDetailActivity", t.getMessage());
            }
        });
    }

    private void getLocation(){
        Api.getClient().getLocation().enqueue(new Callback<List<LocationData>>() {
            @Override
            public void onResponse(Call<List<LocationData>> call, Response<List<LocationData>> response) {
                lstLocation=response.body();
                setLocation();
                getPayment();
            }

            @Override
            public void onFailure(Call<List<LocationData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("PayDetailActivity", t.getMessage());
            }
        });
    }

    private void getPayment(){
        Api.getClient().getPayment().enqueue(new Callback<List<PaymentData>>() {
            @Override
            public void onResponse(Call<List<PaymentData>> call, Response<List<PaymentData>> response) {
                lstPayment=response.body();
                setPayment();
                getPaymentMethod();
            }

            @Override
            public void onFailure(Call<List<PaymentData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("PayDetailActivity", t.getMessage());
            }
        });
    }

    private void getPaymentMethod(){
        Api.getClient().getPaymentMethod().enqueue(new Callback<List<PaymentMethodData>>() {
            @Override
            public void onResponse(Call<List<PaymentMethodData>> call, Response<List<PaymentMethodData>> response) {
                lstPaymentMethod=response.body();
                setPaymentMethod();
                getBankPayment();
            }

            @Override
            public void onFailure(Call<List<PaymentMethodData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("PayDetailActivity", t.getMessage());
            }
        });
    }

    private void getBankPayment(){
        Api.getClient().getBankPayment().enqueue(new Callback<List<BankPaymentData>>() {
            @Override
            public void onResponse(Call<List<BankPaymentData>> call, Response<List<BankPaymentData>> response) {
                lstBankPayment=response.body();
                setBankPayment();
                getLimitedDay();
            }

            @Override
            public void onFailure(Call<List<BankPaymentData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("PayDetailActivity", t.getMessage());
            }
        });
    }

    private void getLimitedDay(){
        Api.getClient().getLimitedDay().enqueue(new Callback<List<LimitedDayData>>() {
            @Override
            public void onResponse(Call<List<LimitedDayData>> call, Response<List<LimitedDayData>> response) {
                progressDialog.dismiss();
                lstLimitedDay=response.body();
                setLimitedDay();
            }

            @Override
            public void onFailure(Call<List<LimitedDayData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("PayDetailActivity", t.getMessage());
            }
        });
    }

    private void setLayoutResource(){
        spCustomer=findViewById(R.id.spCustomer);
        spLocation=findViewById(R.id.spLocation);
        spPayment=findViewById(R.id.spPayment);
        spPaymentMethod=findViewById(R.id.spPaymentMethod);
        spBankPayment=findViewById(R.id.spBankPayment);
        spLimitedDay=findViewById(R.id.spLimitedDay);
        btnLimitDayAdvancedPayOk=findViewById(R.id.btnLimitDayAdvancedPayOk);
        btnDollar=findViewById(R.id.btnDollar);
        btnPercent=findViewById(R.id.btnPercent);
        btnPaidOk=findViewById(R.id.btnPaidOk);
        btnPaymentPercentOk=findViewById(R.id.btnPaymentPercentOk);
        btnContinue=findViewById(R.id.btnContinue);
        rdoNoDiscount=findViewById(R.id.rdoNoDiscount);
        layoutPaymentDebt=findViewById(R.id.layoutPaymentDebt);
        layoutPaymentMethod=findViewById(R.id.layoutPaymentMethod);
        layoutPaidChange=findViewById(R.id.layoutPaidChange);
        layoutOnlinePayment=findViewById(R.id.layoutOnlinePayment);
        layoutAdvancedPay=findViewById(R.id.layoutAdvancedPay);
        etAdvancedPay=findViewById(R.id.etAdvancedPay);
        etVoucherDiscount=findViewById(R.id.etVoucherDiscount);
        tvAdvancedPay=findViewById(R.id.tvAdvancedPay);
        tvTotal=findViewById(R.id.tvTotal);
        tvVoucherDiscount=findViewById(R.id.tvVoucherDiscount);
        tvGrandTotal=findViewById(R.id.tvGrandTotal);
        tvGrandTotalBottom=findViewById(R.id.tvGrandTotalBottom);

        progressDialog =new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }
}