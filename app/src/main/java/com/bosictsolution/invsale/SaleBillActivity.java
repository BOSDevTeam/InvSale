package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.List;

public class SaleBillActivity extends AppCompatActivity {

    ImageView imgLogo;
    TextView tvTitle1,tvTitle2,tvTitle3,tvTitle4,tvTitle5,tvTitle6,
            tvSlipID,tvDate,tvCustomer,tvSalePerson,tvMessage1,tvMessage2,tvMessage3,
            tvSubtotal,tvLabelTax,tvTax,tvLabelCharges,tvCharges,tvTotal,tvLabelVoucherDiscount,tvVoucherDiscount,
            tvAdvancedPay,tvGrandTotal,tvLabelPercent,tvPercentAmount,tvPercentGrandTotal;
    LinearLayout layoutList,layoutAdvancedPay,layoutPercent,layoutPercentGrandTotal,layoutBill,layoutTax,layoutCharges,layoutSubtotal;
    Button btnBack,btnPrint;
    AppSetting appSetting=new AppSetting();
    int locationId,slipId;
    String customerName,clientName;
    private ProgressDialog progressDialog;
    private Context context=this;
    DatabaseAccess db;
    SharedPreferences sharedpreferences;
    ConnectionLiveData connectionLiveData;
    public static BluetoothAdapter BA;
    private BtDeviceListAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    boolean isCredit;

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
        setTitle(getResources().getString(R.string.sale_completed));

        Intent intent=getIntent();
        locationId=intent.getIntExtra("LocationID",0);
        customerName=intent.getStringExtra("CustomerName");
        slipId=intent.getIntExtra("SlipID",0);
        isCredit=intent.getBooleanExtra("IsCredit",false);

        checkConnection();
        fillData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saleCompleted();
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(appSetting.checkAndRequestBluetoothOn(SaleBillActivity.this,BA)){
                    if (appSetting.checkBluetoothDevice(BA,db.getPrinterAddress(),context,bluetoothAdapter,deviceAdapter)){
                        Intent i=new Intent(SaleBillActivity.this,SalePrintActivity.class);
                        i.putExtra("PaperWidth",db.getPaperWidth());
                        i.putExtra("LocationID",locationId);
                        i.putExtra("SlipID",slipId);
                        i.putExtra("CustomerName",customerName);
                        i.putExtra("IsCredit",isCredit);
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(context,getResources().getString(R.string.printer_not_found),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void init(){
        connectionLiveData = new ConnectionLiveData(context);
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        db=new DatabaseAccess(context);
        progressDialog =new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
        BA = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter = new BtDeviceListAdapter(getApplicationContext(), null);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
        clientName=sharedpreferences.getString(AppConstant.CLIENT_NAME,"");
        getVoucherSetting(locationId);
    }

    private void getVoucherSetting(int locationId){
        setVoucherSetting(db.getVoucherSettingByLocation(locationId));
        getMasterSale();
        getTranSale();
    }

    private void setVoucherSetting(VoucherSettingData data) {
        if (data.getHeaderName().length() != 0) tvTitle1.setText(data.getHeaderName());
        else tvTitle1.setVisibility(View.GONE);
        if (data.getHeaderDesp().length() != 0) tvTitle2.setText(data.getHeaderDesp());
        else tvTitle2.setVisibility(View.GONE);
        if (data.getHeaderPhone().length() != 0) tvTitle3.setText(data.getHeaderPhone());
        else tvTitle3.setVisibility(View.GONE);
        if (data.getHeaderAddress().length() != 0) tvTitle4.setText(data.getHeaderAddress());
        else tvTitle4.setVisibility(View.GONE);
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

    private void getMasterSale(){
        tvSlipID.setText(getResources().getString(R.string.slip_no)+slipId);
        tvSalePerson.setText(clientName);
        tvCustomer.setText(customerName);
        tvDate.setText(appSetting.getTodayDate());
        SaleMasterData data=db.getMasterSale();

        if(data.getTax() == 0 && data.getCharges() == 0)
            layoutSubtotal.setVisibility(View.GONE);
        else tvSubtotal.setText(appSetting.df.format(data.getSubtotal()));

        if(data.getTax() != 0){
            tvLabelTax.setText(getResources().getString(R.string.tax) + "(" + data.getTax() + "%)"+getResources().getString(R.string.colon_sign));
            tvTax.setText(appSetting.df.format(data.getTaxAmt()));
        }else layoutTax.setVisibility(View.GONE);

        if(data.getCharges() != 0) {
            tvLabelCharges.setText(getResources().getString(R.string.charges) + "(" + data.getCharges() + "%)"+getResources().getString(R.string.colon_sign));
            tvCharges.setText(appSetting.df.format(data.getChargesAmt()));
        }else layoutCharges.setVisibility(View.GONE);

        tvTotal.setText(appSetting.df.format(data.getSubtotal()+data.getTaxAmt()+data.getChargesAmt()));

        if (data.getVouDisPercent() != 0)
            tvLabelVoucherDiscount.setText(getResources().getString(R.string.voucher_discount) + "(" + data.getVouDisPercent() + "%)"+getResources().getString(R.string.colon_sign));
        tvVoucherDiscount.setText(appSetting.df.format(data.getVoucherDiscount()));

        if (data.getAdvancedPay() !=0) {
            layoutAdvancedPay.setVisibility(View.VISIBLE);
            tvAdvancedPay.setText(appSetting.df.format(data.getAdvancedPay()));
        } else
            layoutAdvancedPay.setVisibility(View.GONE);

        int grandTotal = (data.getSubtotal()+data.getTaxAmt()+data.getChargesAmt()) - (data.getAdvancedPay() + data.getVoucherDiscount());
        tvGrandTotal.setText(appSetting.df.format(grandTotal));

        if (data.getPaymentPercent() != 0) {
            layoutPercent.setVisibility(View.VISIBLE);
            layoutPercentGrandTotal.setVisibility(View.VISIBLE);
            tvPercentAmount.setText(appSetting.df.format(data.getPayPercentAmt()));
            tvPercentGrandTotal.setText(appSetting.df.format(data.getGrandtotal()));
            tvLabelPercent.setText(getResources().getString(R.string.percent) + "(" + data.getPaymentPercent() + "%)"+getResources().getString(R.string.colon_sign));
        } else {
            layoutPercent.setVisibility(View.GONE);
            layoutPercentGrandTotal.setVisibility(View.GONE);
        }
    }

    private void getTranSale() {
        List<SaleTranData> lstSaleTran = db.getTranSale();
        for (int i = 0; i < lstSaleTran.size(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.item_sale_bill, null);
            TextView tvProductName = row.findViewById(R.id.tvProductName);
            TextView tvPrice = row.findViewById(R.id.tvPrice);
            TextView tvQuantity = row.findViewById(R.id.tvQuantity);
            TextView tvNumber = row.findViewById(R.id.tvNumber);
            TextView tvAmount = row.findViewById(R.id.tvAmount);

            tvProductName.setText(lstSaleTran.get(i).getProductName());
            tvPrice.setText(appSetting.df.format(lstSaleTran.get(i).getSalePrice()));
            tvQuantity.setText(String.valueOf(lstSaleTran.get(i).getQuantity()));
            tvNumber.setText(String.valueOf(lstSaleTran.get(i).getNumber()));
            tvAmount.setText(appSetting.df.format(lstSaleTran.get(i).getAmount()));

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

    private void saleCompleted(){
        if(PayDetailActivity.activity!=null) PayDetailActivity.activity.finish();
        SaleActivity.isSaleCompleted=true;
        finish();
    }

    private void setLayoutResource(){
        layoutList=findViewById(R.id.layoutList);
        layoutAdvancedPay=findViewById(R.id.layoutAdvancedPay);
        layoutPercent=findViewById(R.id.layoutPercent);
        layoutPercentGrandTotal=findViewById(R.id.layoutPercentGrandTotal);
        layoutBill=findViewById(R.id.layoutBill);
        imgLogo=findViewById(R.id.imgLogo);
        tvTitle1=findViewById(R.id.tvTitle1);
        tvTitle2=findViewById(R.id.tvTitle2);
        tvTitle3=findViewById(R.id.tvTitle3);
        tvTitle4=findViewById(R.id.tvTitle4);
        tvTitle5=findViewById(R.id.tvTitle5);
        tvTitle6=findViewById(R.id.tvTitle6);
        tvSlipID=findViewById(R.id.tvSlipID);
        tvDate=findViewById(R.id.tvDate);
        tvCustomer=findViewById(R.id.tvCustomer);
        tvSalePerson=findViewById(R.id.tvSalePerson);
        tvMessage1=findViewById(R.id.tvMessage1);
        tvMessage2=findViewById(R.id.tvMessage2);
        tvMessage3=findViewById(R.id.tvMessage3);
        tvSubtotal=findViewById(R.id.tvSubtotal);
        tvLabelTax=findViewById(R.id.tvLabelTax);
        tvTax=findViewById(R.id.tvTax);
        tvLabelCharges=findViewById(R.id.tvLabelCharges);
        tvCharges=findViewById(R.id.tvCharges);
        tvTotal=findViewById(R.id.tvTotal);
        tvLabelVoucherDiscount=findViewById(R.id.tvLabelVoucherDiscount);
        tvVoucherDiscount=findViewById(R.id.tvVoucherDiscount);
        tvAdvancedPay=findViewById(R.id.tvAdvancedPay);
        tvGrandTotal=findViewById(R.id.tvGrandTotal);
        tvLabelPercent=findViewById(R.id.tvLabelPercent);
        tvPercentAmount=findViewById(R.id.tvPercentAmount);
        tvPercentGrandTotal=findViewById(R.id.tvPercentGrandTotal);
        btnBack=findViewById(R.id.btnBack);
        btnPrint=findViewById(R.id.btnPrint);
        layoutTax=findViewById(R.id.layoutTax);
        layoutCharges=findViewById(R.id.layoutCharges);
        layoutSubtotal=findViewById(R.id.layoutSubtotal);
    }
}