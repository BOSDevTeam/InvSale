package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.Observer;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.BankPaymentData;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.CustomerData;
import com.bosictsolution.invsale.data.LimitedDayData;
import com.bosictsolution.invsale.data.LocationData;
import com.bosictsolution.invsale.data.PaymentData;
import com.bosictsolution.invsale.data.PaymentMethodData;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class PayDetailActivity extends AppCompatActivity {

    Spinner spCustomer, spLocation, spPayment, spPaymentMethod, spBankPayment, spLimitedDay;
    Button btnDollar, btnPercent, btnOK;
    LinearLayout layoutPaymentCredit, layoutPaymentMethod, layoutOnlinePayment;
    EditText etAdvancedPay, etVoucherDiscount, etPaymentPercent,etRemark;
    CheckBox chkAdvancedPay;
    TextInputLayout inputAdvancedPay,inputVoucherDiscount,inputPaymentPercent;
    List<CustomerData> lstCustomer = new ArrayList<>();
    List<LocationData> lstLocation = new ArrayList<>();
    List<PaymentData> lstPayment = new ArrayList<>();
    List<PaymentMethodData> lstPaymentMethod = new ArrayList<>();
    List<BankPaymentData> lstBankPayment = new ArrayList<>();
    List<LimitedDayData> lstLimitedDay = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Context context = this;
    int voucherDiscountType, discountPercentType = 1, discountAmountType = 2, total,
        subtotal,taxAmount,chargesAmount,clientId,tax,charges;
    AppSetting appSetting = new AppSetting();
    DatabaseAccess db;
    SharedPreferences sharedpreferences;
    public static Activity activity;
    ConnectionLiveData connectionLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.menu_sale));

        Intent i = getIntent();
        subtotal = i.getIntExtra("Subtotal", 0);
        tax = i.getIntExtra("Tax", 0);
        taxAmount = i.getIntExtra("TaxAmount", 0);
        charges = i.getIntExtra("Charges", 0);
        chargesAmount = i.getIntExtra("ChargesAmount", 0);
        total = i.getIntExtra("Total", 0);

        checkConnection();
        fillData();
        setLayoutPaymentCredit();
        setLayoutPaymentMethod();
        setLayoutOnlinePayment();

        spPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setLayoutPaymentCredit();
                setLayoutPaymentMethod();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        chkAdvancedPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkAdvancedPay(b);
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateControl())
                    showAmountGroupDialog(prepareSaleMasterData());
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
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        db=new DatabaseAccess(context);
        activity=this;
        progressDialog = new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
    }

    private void showAmountGroupDialog(SaleMasterData saleMasterData) {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_sale_amount_group, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final TextView tvTotal = v.findViewById(R.id.tvTotal);
        final TextView tvAdvancedPay = v.findViewById(R.id.tvAdvancedPay);
        final TextView tvVoucherDiscount = v.findViewById(R.id.tvVoucherDiscount);
        final TextView tvLabelVoucherDiscount = v.findViewById(R.id.tvLabelVoucherDiscount);
        final TextView tvPercentGrandTotal = v.findViewById(R.id.tvPercentGrandTotal);
        final TextView tvPercentAmount = v.findViewById(R.id.tvPercentAmount);
        final TextView tvLabelPercent = v.findViewById(R.id.tvLabelPercent);
        final TextView tvGrandTotal = v.findViewById(R.id.tvGrandTotal);
        final Button btnContinue = v.findViewById(R.id.btnContinue);
        final LinearLayout layoutAdvancedPay = v.findViewById(R.id.layoutAdvancedPay);
        final LinearLayout layoutPercent = v.findViewById(R.id.layoutPercent);
        final LinearLayout layoutPercentGrandTotal = v.findViewById(R.id.layoutPercentGrandTotal);

        tvTotal.setText(appSetting.df.format(total));

        if (saleMasterData.isAdvancedPay()) {
            layoutAdvancedPay.setVisibility(View.VISIBLE);
            tvAdvancedPay.setText(appSetting.df.format(saleMasterData.getAdvancedPay()));
        } else
            layoutAdvancedPay.setVisibility(View.GONE);

        if (saleMasterData.getVouDisPercent() != 0)
            tvLabelVoucherDiscount.setText(getResources().getString(R.string.voucher_discount_colon) + "(" + saleMasterData.getVouDisPercent() + "%)");
        tvVoucherDiscount.setText(appSetting.df.format(saleMasterData.getVoucherDiscount()));

        int grandTotal = saleMasterData.getTotal() - (saleMasterData.getAdvancedPay() + saleMasterData.getVoucherDiscount());
        tvGrandTotal.setText(appSetting.df.format(grandTotal));

        if (saleMasterData.getPaymentPercent() != 0) {
            layoutPercent.setVisibility(View.VISIBLE);
            layoutPercentGrandTotal.setVisibility(View.VISIBLE);
            tvPercentAmount.setText(appSetting.df.format(saleMasterData.getPayPercentAmt()));
            tvPercentGrandTotal.setText(appSetting.df.format(saleMasterData.getGrandtotal()));
            tvLabelPercent.setText(getResources().getString(R.string.percent) + "(" + saleMasterData.getPaymentPercent() + "%)"+getResources().getString(R.string.colon_sign));
        } else {
            layoutPercent.setVisibility(View.GONE);
            layoutPercentGrandTotal.setVisibility(View.GONE);
        }

        if (!saleMasterData.isDefaultCustomer())
            btnContinue.setText(getResources().getString(R.string.pay_confirm));

        dialog.setCancelable(true);
        android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.insertMasterSale(saleMasterData)) {
                    if (saleMasterData.isDefaultCustomer()) {
                        Intent i = new Intent(PayDetailActivity.this, CustomerActivity.class);
                        i.putExtra(AppSetting.EXTRA_MODULE_TYPE, AppConstant.SALE_MODULE_TYPE);
                        i.putExtra("LocationID", saleMasterData.getLocationID());
                        startActivity(i);
                    } else
                        insertSale();
                } else
                    Toast.makeText(context, getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void insertSale() {
        SaleMasterData data = db.getMasterSale();
        data.setLstSaleTran(db.getTranSale());
        data.setClientSale(true);
        data.setClientID(clientId);
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().insertSale(data).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                progressDialog.dismiss();
                if (response.body() == null) return;
                if (response.isSuccessful()) {
                    int slipId=response.body();
                    int position = spCustomer.getSelectedItemPosition();
                    String customerName = lstCustomer.get(position).getCustomerName();
                    Intent intent = new Intent(PayDetailActivity.this, SaleBillActivity.class);
                    intent.putExtra("LocationID", data.getLocationID());
                    intent.putExtra("CustomerName", customerName);
                    intent.putExtra("SlipID",slipId);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private SaleMasterData prepareSaleMasterData() {
        int advancedPay = 0, vouDisPercent = 0, vouDisAmount = 0, voucherDiscount = 0, payPercent = 0, payPercentAmt = 0, grandTotal,
                percentGrandTotal = 0, position, paymentId, customerId, locationId, limitedDayId = 0, payMethodId,
                bankPaymentId = 0;
        boolean isAdvancedPay = false, isDefaultCustomer;
        String remark;
        SaleMasterData saleMasterData = new SaleMasterData();

        position = spCustomer.getSelectedItemPosition();
        isDefaultCustomer = lstCustomer.get(position).isDefault();
        if (isDefaultCustomer) customerId = 0;
        else customerId = lstCustomer.get(position).getCustomerID();

        position = spLocation.getSelectedItemPosition();
        locationId = lstLocation.get(position).getLocationID();

        position = spPayment.getSelectedItemPosition();
        paymentId = lstPayment.get(position).getPaymentID();
        if (spPayment.getSelectedItemPosition() == 1) {  // payment credit
            position = spLimitedDay.getSelectedItemPosition();
            limitedDayId = lstLimitedDay.get(position).getLimitedDayID();
            if (chkAdvancedPay.isChecked()) {
                isAdvancedPay = true;
                advancedPay = Integer.parseInt(etAdvancedPay.getText().toString());
            }
        }

        if (voucherDiscountType == discountPercentType && etVoucherDiscount.getText().toString().length() != 0)
            vouDisPercent = Integer.parseInt(etVoucherDiscount.getText().toString());
        else if (voucherDiscountType == discountAmountType && etVoucherDiscount.getText().toString().length() != 0)
            vouDisAmount = Integer.parseInt(etVoucherDiscount.getText().toString());
        voucherDiscount = calculateVoucherDiscount(vouDisPercent);

        grandTotal = total - (advancedPay + voucherDiscount);

        position = spPaymentMethod.getSelectedItemPosition();
        payMethodId = lstPaymentMethod.get(position).getPayMethodID();
        if (spPaymentMethod.getSelectedItemPosition() == 1) {  // pay method online payment
            position = spBankPayment.getSelectedItemPosition();
            bankPaymentId = lstBankPayment.get(position).getBankPaymentID();
            if (etPaymentPercent.getText().toString().length() != 0)
                payPercent = Integer.parseInt(etPaymentPercent.getText().toString());
            if (payPercent != 0) {
                payPercentAmt = (grandTotal * payPercent) / 100;
                percentGrandTotal = grandTotal + payPercentAmt;
            }
        }

        remark = etRemark.getText().toString();

        saleMasterData.setDefaultCustomer(isDefaultCustomer);
        saleMasterData.setCustomerID(customerId);
        saleMasterData.setLocationID(locationId);
        saleMasterData.setPaymentID(paymentId);
        saleMasterData.setLimitedDayID(limitedDayId);
        saleMasterData.setAdvancedPay(isAdvancedPay);
        saleMasterData.setAdvancedPay(advancedPay);
        saleMasterData.setVouDisPercent(vouDisPercent);
        saleMasterData.setVouDisAmount(vouDisAmount);
        saleMasterData.setVoucherDiscount(voucherDiscount);
        saleMasterData.setPayMethodID(payMethodId);
        saleMasterData.setBankPaymentID(bankPaymentId);
        saleMasterData.setPaymentPercent(payPercent);
        saleMasterData.setPayPercentAmt(payPercentAmt);
        saleMasterData.setSubtotal(subtotal);
        saleMasterData.setTax(tax);
        saleMasterData.setTaxAmt(taxAmount);
        saleMasterData.setCharges(charges);
        saleMasterData.setChargesAmt(chargesAmount);
        saleMasterData.setTotal(total);
        if (payPercent != 0) saleMasterData.setGrandtotal(percentGrandTotal);
        else saleMasterData.setGrandtotal(grandTotal);
        saleMasterData.setRemark(remark);

        return saleMasterData;
    }

    private int calculateVoucherDiscount(int percent) {
        int voucherDiscount = 0;
        if (etVoucherDiscount.getText().toString().length() != 0) {
            if (voucherDiscountType == discountPercentType)
                voucherDiscount = (total * percent) / 100;
            else if (voucherDiscountType == discountAmountType)
                voucherDiscount = Integer.parseInt(etVoucherDiscount.getText().toString());
        }
        return voucherDiscount;
    }

    private boolean validateControl() {
        if (lstCustomer.size() == 0) {
            Toast.makeText(context, getResources().getString(R.string.customer_not_found), Toast.LENGTH_LONG).show();
            return false;
        }
        if (voucherDiscountType == discountPercentType) {
            if (etVoucherDiscount.getText().toString().length() != 0) {
                if (Integer.parseInt(etVoucherDiscount.getText().toString()) > 100) {
                    inputVoucherDiscount.setError(getResources().getString(R.string.invalid_percent_value));
                    return false;
                }
            }
        }
        if (spPayment.getSelectedItemPosition() == 1 && chkAdvancedPay.isChecked()) {
            if (etAdvancedPay.getText().toString().length() == 0) {
                inputAdvancedPay.setError(getResources().getString(R.string.enter_value));
                return false;
            } else if (Integer.parseInt(etAdvancedPay.getText().toString()) == 0) {
                inputAdvancedPay.setError(getResources().getString(R.string.enter_valid_value));
                return false;
            }
        }
        if (layoutOnlinePayment.isShown()) {
            if (etPaymentPercent.getText().toString().length() != 0) {
                if (Integer.parseInt(etPaymentPercent.getText().toString()) > 100) {
                    inputPaymentPercent.setError(getResources().getString(R.string.invalid_percent_value));
                    return false;
                }
            }
        }
        return true;
    }

    private void clearPaymentCredit() {
        chkAdvancedPay.setChecked(false);
        etAdvancedPay.setEnabled(false);
        etAdvancedPay.setText("");
    }

    private void changeVoucherDiscountType(int type) {
        voucherDiscountType = type;
        etVoucherDiscount.setText("");
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

    private void checkAdvancedPay(boolean checked) {
        if (checked) {
            etAdvancedPay.setEnabled(true);
            layoutPaymentMethod.setVisibility(View.VISIBLE);
        } else {
            etAdvancedPay.setText("");
            etAdvancedPay.setEnabled(false);
            layoutPaymentMethod.setVisibility(View.GONE);
            etPaymentPercent.setText("");
        }
    }

    private void setLayoutOnlinePayment() {
        etPaymentPercent.setText("");
        if (spPaymentMethod.getSelectedItemPosition() == 0)  // if payment method is cashInHand
            layoutOnlinePayment.setVisibility(View.GONE);
        else if (spPaymentMethod.getSelectedItemPosition() == 1) // if payment method is onlinePayment
            layoutOnlinePayment.setVisibility(View.VISIBLE);
    }

    private void setLayoutPaymentMethod() {
        etPaymentPercent.setText("");
        if (spPayment.getSelectedItemPosition() == 0) // cash
            layoutPaymentMethod.setVisibility(View.VISIBLE);
        else if (spPayment.getSelectedItemPosition() == 1)  // credit
            layoutPaymentMethod.setVisibility(View.GONE);
    }

    private void setLayoutPaymentCredit() {
        if (spPayment.getSelectedItemPosition() == 0) {  // cash
            layoutPaymentCredit.setVisibility(View.GONE);
            clearPaymentCredit();
        } else if (spPayment.getSelectedItemPosition() == 1)  // credit
            layoutPaymentCredit.setVisibility(View.VISIBLE);
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
        clientId=sharedpreferences.getInt(AppConstant.CLIENT_ID,0);
        voucherDiscountType = discountPercentType;
        getLocation();
        getPayment();
        getPaymentMethod();
        getBankPayment();
        getLimitedDay();
        getCustomer();
    }

    private void setCustomer() {
        String[] customers = new String[lstCustomer.size()];
        for (int i = 0; i < lstCustomer.size(); i++) {
            customers[i] = lstCustomer.get(i).getCustomerName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, customers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCustomer.setAdapter(adapter);
    }

    private void setLocation() {
        String[] locations = new String[lstLocation.size()];
        for (int i = 0; i < lstLocation.size(); i++) {
            locations[i] = lstLocation.get(i).getShortName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocation.setAdapter(adapter);
        spLocation.setEnabled(false);
    }

    private void setPayment() {
        String[] payments = new String[lstPayment.size()];
        for (int i = 0; i < lstPayment.size(); i++) {
            payments[i] = lstPayment.get(i).getKeyword();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, payments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPayment.setAdapter(adapter);
    }

    private void setPaymentMethod() {
        String[] paymentMethods = new String[lstPaymentMethod.size()];
        for (int i = 0; i < lstPaymentMethod.size(); i++) {
            paymentMethods[i] = lstPaymentMethod.get(i).getPayMethodName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentMethod.setAdapter(adapter);
    }

    private void setBankPayment() {
        String[] bankPayments = new String[lstBankPayment.size()];
        for (int i = 0; i < lstBankPayment.size(); i++) {
            bankPayments[i] = lstBankPayment.get(i).getBankPaymentName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, bankPayments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBankPayment.setAdapter(adapter);
    }

    private void setLimitedDay() {
        String[] limitedDays = new String[lstLimitedDay.size()];
        for (int i = 0; i < lstLimitedDay.size(); i++) {
            limitedDays[i] = lstLimitedDay.get(i).getLimitedDayName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, limitedDays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLimitedDay.setAdapter(adapter);
    }

    private void getCustomer() {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getCustomer().enqueue(new Callback<List<CustomerData>>() {
            @Override
            public void onResponse(Call<List<CustomerData>> call, Response<List<CustomerData>> response) {
                progressDialog.dismiss();
                if (response.body() == null) return;
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

    private void getLocation() {
        lstLocation = db.getLocation();
        setLocation();
    }

    private void getPayment() {
        lstPayment = db.getPayment();
        setPayment();
    }

    private void getPaymentMethod() {
        lstPaymentMethod = db.getPaymentMethod();
        setPaymentMethod();
    }

    private void getBankPayment() {
        lstBankPayment = db.getBankPayment();
        setBankPayment();
    }

    private void getLimitedDay() {
        lstLimitedDay = db.getLimitedDay();
        setLimitedDay();
    }

    private void setLayoutResource() {
        spCustomer = findViewById(R.id.spCustomer);
        spLocation = findViewById(R.id.spLocation);
        spPayment = findViewById(R.id.spPayment);
        spPaymentMethod = findViewById(R.id.spPaymentMethod);
        spBankPayment = findViewById(R.id.spBankPayment);
        spLimitedDay = findViewById(R.id.spLimitedDay);
        btnDollar = findViewById(R.id.btnDollar);
        btnPercent = findViewById(R.id.btnPercent);
        layoutPaymentCredit = findViewById(R.id.layoutPaymentCredit);
        layoutPaymentMethod = findViewById(R.id.layoutPaymentMethod);
        layoutOnlinePayment = findViewById(R.id.layoutOnlinePayment);
        etAdvancedPay = findViewById(R.id.etAdvancedPay);
        etVoucherDiscount = findViewById(R.id.etVoucherDiscount);
        etPaymentPercent = findViewById(R.id.etPaymentPercent);
        chkAdvancedPay = findViewById(R.id.chkAdvancedPay);
        btnOK = findViewById(R.id.btnOK);
        inputAdvancedPay = findViewById(R.id.inputAdvancedPay);
        inputVoucherDiscount = findViewById(R.id.inputVoucherDiscount);
        inputPaymentPercent = findViewById(R.id.inputPaymentPercent);
        etRemark = findViewById(R.id.etRemark);
    }
}