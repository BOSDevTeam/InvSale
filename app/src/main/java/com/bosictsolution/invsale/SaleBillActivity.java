package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.bluetooth.BtDeviceListAdapter;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.VoucherSettingData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SaleBillActivity extends AppCompatActivity {

    ImageView imgLogo;
    TextView tvTitle1, tvTitle2, tvTitle3, tvTitle4, tvTitle5, tvTitle6,
            tvSlipID, tvDate, tvCustomer, tvSalePerson, tvMessage1, tvMessage2, tvMessage3,
            tvSubtotal, tvLabelTax, tvTax, tvLabelCharges, tvCharges, tvTotal, tvLabelVoucherDiscount, tvVoucherDiscount,
            tvAdvancedPay, tvGrandTotal, tvLabelPercent, tvPercentAmount, tvPercentGrandTotal, tvBankPay, tvShowGrandTotal;
    LinearLayout layoutList, layoutAdvancedPay, layoutPercent, layoutPercentGrandTotal, layoutBill, layoutTax, layoutCharges,
            layoutSubtotal, layoutPayment, layoutShowGrandTotal;
    Button btnBack, btnPrint;
    AppSetting appSetting = new AppSetting();
    int locationId, slipId, saleId;
    String customerName, clientName, editDate;
    private ProgressDialog progressDialog;
    private Context context = this;
    DatabaseAccess db;
    SharedPreferences sharedpreferences;
    ConnectionLiveData connectionLiveData;
    public static BluetoothAdapter BA;
    private BtDeviceListAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    boolean isCredit, isSaleEdit, isReprint;
    static List<SaleMasterData> lstSaleMasterData=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_bill);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.primary_500));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        checkConnection();

        Intent intent = getIntent();
        isReprint = intent.getBooleanExtra("IsReprint", false);
        if (!isReprint) {
            locationId = intent.getIntExtra("LocationID", 0);
            customerName = intent.getStringExtra("CustomerName");
            slipId = intent.getIntExtra("SlipID", 0);
            isCredit = intent.getBooleanExtra("IsCredit", false);
            isSaleEdit = intent.getBooleanExtra("IsSaleEdit", false);

            if (isSaleEdit) {
                setTitle(getResources().getString(R.string.sale_edited));
                editDate = intent.getStringExtra("SaleDateTime");
                btnBack.setText(getResources().getString(R.string.back));
                Drawable img = getResources().getDrawable(R.drawable.ic_arrow_back_24);
                img.setBounds(0, 0, 60, 60);
                btnBack.setCompoundDrawables(img, null, null, null);
            } else setTitle(getResources().getString(R.string.sale_completed));

            fillData();
        } else {
            setTitle(getResources().getString(R.string.reprint));
            btnBack.setVisibility(View.INVISIBLE);
            layoutShowGrandTotal.setVisibility(View.GONE);
            saleId = intent.getIntExtra("SaleID", 0);
            getSaleBySaleID(saleId);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saleCompleted();
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appSetting.checkAndRequestBluetoothOn(SaleBillActivity.this, BA)) {
                    if (appSetting.checkBluetoothDevice(BA, db.getPrinterAddress(), context, bluetoothAdapter, deviceAdapter)) {
                        Intent i = new Intent(SaleBillActivity.this, SalePrintActivity.class);
                        i.putExtra("PaperWidth", db.getPaperWidth());
                        i.putExtra("LocationID", locationId);
                        i.putExtra("SlipID", slipId);
                        i.putExtra("CustomerName", customerName);
                        i.putExtra("IsCredit", isCredit);
                        i.putExtra("IsSaleEdit", isSaleEdit);
                        i.putExtra("EditDate", editDate);
                        i.putExtra("IsReprint", isReprint);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.printer_not_found), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void init() {
        connectionLiveData = new ConnectionLiveData(context);
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        db = new DatabaseAccess(context);
        progressDialog = new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
        BA = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter = new BtDeviceListAdapter(getApplicationContext(), null);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void checkConnection() {
        connectionLiveData.observe(this, new Observer<ConnectionData>() {
            @Override
            public void onChanged(ConnectionData connectionData) {
                if (!connectionData.getIsConnected())
                    appSetting.showSnackBar(findViewById(R.id.layoutRoot));
            }
        });
    }

    private void fillData() {
        clientName = sharedpreferences.getString(AppConstant.CLIENT_NAME, "");
        setVoucherSetting(db.getVoucherSettingByLocation(locationId));
        SaleMasterData saleMasterData = db.getMasterSale();
        saleMasterData.setSlipID(slipId);
        saleMasterData.setCustomerName(customerName);
        saleMasterData.setClientName(clientName);
        if (!isSaleEdit) saleMasterData.setSaleDateTime(appSetting.getTodayDate());
        else saleMasterData.setSaleDateTime(editDate);

        fillMasterSale(saleMasterData);
        fillTranSale(db.getTranSale());
    }

    private void setVoucherSetting(VoucherSettingData data) {
        if (data.getHeaderName().length() != 0) tvTitle1.setText(data.getHeaderName());
        else tvTitle1.setVisibility(View.GONE);
       /* if (data.getHeaderDesp().length() != 0) tvTitle2.setText(data.getHeaderDesp());
        else tvTitle2.setVisibility(View.GONE);
        if (data.getHeaderPhone().length() != 0) tvTitle3.setText(data.getHeaderPhone());
        else tvTitle3.setVisibility(View.GONE);
        if (data.getHeaderAddress().length() != 0) tvTitle4.setText(data.getHeaderAddress());
        else tvTitle4.setVisibility(View.GONE);*/
        if (data.getOtherHeader1().length() != 0) tvTitle5.setText(data.getOtherHeader1());
        else tvTitle5.setVisibility(View.GONE);
        if (data.getOtherHeader2().length() != 0) tvTitle6.setText(data.getOtherHeader2());
        else tvTitle6.setVisibility(View.GONE);
        if (data.getFooterMessage1().length() != 0) tvMessage1.setText(data.getFooterMessage1());
        else tvMessage1.setVisibility(View.GONE);
        if (data.getFooterMessage2().length() != 0) tvMessage2.setText(data.getFooterMessage2());
        else tvMessage2.setVisibility(View.GONE);
        if (data.getFooterMessage3().length() != 0) tvMessage3.setText(data.getFooterMessage3());
        else tvMessage3.setVisibility(View.GONE);

        if (data.getVoucherLogoUrl().length() != 0)
            Picasso.with(context).load(data.getVoucherLogoUrl()).into(imgLogo);
        else imgLogo.setVisibility(View.GONE);
    }

    private void fillMasterSale(SaleMasterData data) {
        tvSlipID.setText(getResources().getString(R.string.slip_no) + data.getSlipID());
        tvSalePerson.setText(data.getClientName());
        tvCustomer.setText(data.getCustomerName());

        if(isSaleEdit)tvDate.setText(editDate);
        else if(isReprint)tvDate.setText(data.getSaleDateTime());
        else tvDate.setText(appSetting.getTodayDate());

        if (data.getTax() == 0 && data.getCharges() == 0)
            layoutSubtotal.setVisibility(View.GONE);
        else tvSubtotal.setText(appSetting.df.format(data.getSubtotal()));

        if (data.getTax() != 0) {
            tvLabelTax.setText(getResources().getString(R.string.tax) + "(" + data.getTax() + "%)" + getResources().getString(R.string.colon_sign));
            tvTax.setText(appSetting.df.format(data.getTaxAmt()));
        } else layoutTax.setVisibility(View.GONE);

        if (data.getCharges() != 0) {
            tvLabelCharges.setText(getResources().getString(R.string.charges) + "(" + data.getCharges() + "%)" + getResources().getString(R.string.colon_sign));
            tvCharges.setText(appSetting.df.format(data.getChargesAmt()));
        } else layoutCharges.setVisibility(View.GONE);

        tvTotal.setText(appSetting.df.format(data.getSubtotal() + data.getTaxAmt() + data.getChargesAmt()));

        if (data.getVouDisPercent() != 0)
            tvLabelVoucherDiscount.setText(getResources().getString(R.string.voucher_discount) + "(" + data.getVouDisPercent() + "%)" + getResources().getString(R.string.colon_sign));
        tvVoucherDiscount.setText(appSetting.df.format(data.getVoucherDiscount()));

        if (data.getAdvancedPay() != 0) {
            layoutAdvancedPay.setVisibility(View.VISIBLE);
            tvAdvancedPay.setText(appSetting.df.format(data.getAdvancedPay()));
        } else
            layoutAdvancedPay.setVisibility(View.GONE);

        int grandTotal = (data.getSubtotal() + data.getTaxAmt() + data.getChargesAmt()) - (data.getAdvancedPay() + data.getVoucherDiscount());
        tvGrandTotal.setText(appSetting.df.format(grandTotal));

        if (data.getPaymentPercent() != 0) {
            layoutPercent.setVisibility(View.VISIBLE);
            layoutPercentGrandTotal.setVisibility(View.VISIBLE);
            tvPercentAmount.setText(appSetting.df.format(data.getPayPercentAmt()));
            tvPercentGrandTotal.setText(appSetting.df.format(data.getGrandtotal()));
            tvLabelPercent.setText(getResources().getString(R.string.percent) + "(" + data.getPaymentPercent() + "%)" + getResources().getString(R.string.colon_sign));
            tvShowGrandTotal.setText(appSetting.df.format(data.getGrandtotal()));
        } else {
            layoutPercent.setVisibility(View.GONE);
            layoutPercentGrandTotal.setVisibility(View.GONE);
            tvShowGrandTotal.setText(appSetting.df.format(grandTotal));
        }

        if (data.getBankPaymentID() != 0) {
            layoutPayment.setVisibility(View.VISIBLE);
            tvBankPay.setText(db.getBankPaymentName(data.getBankPaymentID()));
        } else {
            layoutPayment.setVisibility(View.GONE);
        }
    }

    private void fillTranSale(List<SaleTranData> lstSaleTran) {
        for (int i = 0; i < lstSaleTran.size(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.item_sale_bill, null);
            TextView tvProductName = row.findViewById(R.id.tvProductName);
            TextView tvPrice = row.findViewById(R.id.tvPrice);
            TextView tvQuantity = row.findViewById(R.id.tvQuantity);
            TextView tvNumber = row.findViewById(R.id.tvNumber);
            TextView tvAmount = row.findViewById(R.id.tvAmount);
            TextView tvDiscount = row.findViewById(R.id.tvDiscount);

            tvProductName.setText(lstSaleTran.get(i).getProductName());
            tvPrice.setText(appSetting.df.format(lstSaleTran.get(i).getSalePrice()));
            tvQuantity.setText(String.valueOf(lstSaleTran.get(i).getQuantity()));
            tvNumber.setText(String.valueOf(lstSaleTran.get(i).getNumber()));
            tvAmount.setText(appSetting.df.format(lstSaleTran.get(i).getAmount()));
            tvDiscount.setText(appSetting.df.format(lstSaleTran.get(i).getDiscount()));

            layoutList.addView(row);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saleCompleted();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saleCompleted();
    }

    private void saleCompleted() {
        if (PayDetailActivity.activity != null) PayDetailActivity.activity.finish();
        if (isSaleEdit) {
            if (SaleActivity.activity != null) SaleActivity.activity.finish();
        } else SaleActivity.isSaleCompleted = true;
        finish();
    }

    private void setLayoutResource() {
        layoutList = findViewById(R.id.layoutList);
        layoutAdvancedPay = findViewById(R.id.layoutAdvancedPay);
        layoutPercent = findViewById(R.id.layoutPercent);
        layoutPercentGrandTotal = findViewById(R.id.layoutPercentGrandTotal);
        layoutBill = findViewById(R.id.layoutBill);
        layoutPayment = findViewById(R.id.layoutPayment);
        imgLogo = findViewById(R.id.imgLogo);
        tvTitle1 = findViewById(R.id.tvTitle1);
        tvTitle2 = findViewById(R.id.tvTitle2);
        tvTitle3 = findViewById(R.id.tvTitle3);
        tvTitle4 = findViewById(R.id.tvTitle4);
        tvTitle5 = findViewById(R.id.tvTitle5);
        tvTitle6 = findViewById(R.id.tvTitle6);
        tvSlipID = findViewById(R.id.tvSlipID);
        tvDate = findViewById(R.id.tvDate);
        tvCustomer = findViewById(R.id.tvCustomer);
        tvSalePerson = findViewById(R.id.tvSalePerson);
        tvMessage1 = findViewById(R.id.tvMessage1);
        tvMessage2 = findViewById(R.id.tvMessage2);
        tvMessage3 = findViewById(R.id.tvMessage3);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvLabelTax = findViewById(R.id.tvLabelTax);
        tvTax = findViewById(R.id.tvTax);
        tvLabelCharges = findViewById(R.id.tvLabelCharges);
        tvCharges = findViewById(R.id.tvCharges);
        tvTotal = findViewById(R.id.tvTotal);
        tvLabelVoucherDiscount = findViewById(R.id.tvLabelVoucherDiscount);
        tvVoucherDiscount = findViewById(R.id.tvVoucherDiscount);
        tvAdvancedPay = findViewById(R.id.tvAdvancedPay);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvLabelPercent = findViewById(R.id.tvLabelPercent);
        tvPercentAmount = findViewById(R.id.tvPercentAmount);
        tvPercentGrandTotal = findViewById(R.id.tvPercentGrandTotal);
        btnBack = findViewById(R.id.btnBack);
        btnPrint = findViewById(R.id.btnPrint);
        layoutTax = findViewById(R.id.layoutTax);
        layoutCharges = findViewById(R.id.layoutCharges);
        layoutSubtotal = findViewById(R.id.layoutSubtotal);
        tvBankPay = findViewById(R.id.tvBankPay);
        tvShowGrandTotal = findViewById(R.id.tvShowGrandTotal);
        layoutShowGrandTotal = findViewById(R.id.layoutShowGrandTotal);
    }

    private void getSaleBySaleID(int saleId) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));

        Api.getClient().getSaleBySaleID(saleId).enqueue(new Callback<List<SaleMasterData>>() {
            @Override
            public void onResponse(Call<List<SaleMasterData>> call, Response<List<SaleMasterData>> response) {
                progressDialog.dismiss();
                if (response.body() == null) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    return;
                }
                lstSaleMasterData = response.body();
                setVoucherSetting(db.getVoucherSettingByLocation(lstSaleMasterData.get(0).getLocationID()));
                fillMasterSale(lstSaleMasterData.get(0));
                fillTranSale(lstSaleMasterData.get(0).getLstSaleTran());
            }

            @Override
            public void onFailure(Call<List<SaleMasterData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}