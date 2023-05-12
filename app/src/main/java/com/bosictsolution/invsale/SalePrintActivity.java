package com.bosictsolution.invsale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.invsale.bluetooth.print.GPrinterCommand;
import com.bosictsolution.invsale.bluetooth.print.PrintPic;
import com.bosictsolution.invsale.bluetooth.print.PrintQueue;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.VoucherSettingData;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SalePrintActivity extends AppCompatActivity {

    ImageView imgLogo;
    TextView tvTitle1,tvTitle2,tvTitle3,tvTitle4,tvTitle5,tvTitle6,
            tvSlipID,tvDate,tvCustomer,tvSalePerson,tvMessage1,tvMessage2,tvMessage3,
            tvSubtotal,tvLabelTax,tvTax,tvLabelCharges,tvCharges,tvTotal,tvLabelVoucherDiscount,tvVoucherDiscount,
            tvAdvancedPay,tvGrandTotal,tvLabelPercent,tvPercentAmount,tvPercentGrandTotal,tvLabelSubtotal,tvLabelTotal,
            tvLabelAdvancedPay,tvLabelGrandTotal,tvHeaderNo,tvHeaderProduct,tvHeaderQuantity,tvHeaderPrice,tvHeaderAmount,
            tvNumber,tvProductName,tvQuantity,tvPrice,tvAmount;
    LinearLayout layoutList,layoutAdvancedPay,layoutPercent,layoutPercentGrandTotal,layoutPrint,layoutTax,layoutCharges,layoutSubtotal;
    int paperWidth;
    private ProgressDialog progressDialog;
    private Context context=this;
    AppSetting appSetting=new AppSetting();
    int locationId,slipId;
    String customerName,clientName;
    DatabaseAccess db;
    SharedPreferences sharedpreferences;
    ConnectionLiveData connectionLiveData;
    boolean isCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_print);
        setLayoutResource();
        init();
        getSupportActionBar().hide();

        Intent i = getIntent();
        paperWidth = i.getIntExtra("PaperWidth", 0);
        locationId = i.getIntExtra("LocationID", 0);
        slipId = i.getIntExtra("SlipID", 0);
        customerName = i.getStringExtra("CustomerName");
        isCredit=i.getBooleanExtra("IsCredit",false);

        setLayoutPrintSize(paperWidth);
        if (paperWidth == 58) setHeaderTextSize(8);
        else if (paperWidth == 80) setHeaderTextSize(10);
        checkConnection();
        fillData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(convertBillLayoutToBitmap()){
                    printBitmap();

                    if(isCredit){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                printBitmap();
                                printCompleted();
                            }
                        },5000);
                    }else{
                        printCompleted();
                    }

                }
            }
        }, 2000);
    }

    private void printCompleted(){
        if(PayDetailActivity.activity!=null) PayDetailActivity.activity.finish();
        SaleActivity.isSaleCompleted=true;
        finish();
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

    private boolean convertBillLayoutToBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(layoutPrint.getWidth(), layoutPrint.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layoutPrint.draw(canvas);
        saveBitmapToDirectory(bitmap);
        return true;
    }

    private String saveBitmapToDirectory(Bitmap bitmapImage) {
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/"+getResources().getString(R.string.storage_path));
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath = new File(directory, getResources().getString(R.string.sale_bill_png));

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logoPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void printBitmap() {
        Bitmap bitmap = null;
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/" +getResources().getString(R.string.storage_path));
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath = new File(directory, getResources().getString(R.string.sale_bill_png));
        if (filePath.exists()) bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
        PrintPic printPic = PrintPic.getInstance();
        printPic.length = 0;
        printPic.init(bitmap);
        if (null != bitmap) {
            if (bitmap.isRecycled()) {
                bitmap = null;
            } else {
                bitmap.recycle();
                bitmap = null;
            }
        }
        byte[] bytes = printPic.printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(com.bosictsolution.invsale.bluetooth.print.GPrinterCommand.reset);
        printBytes.add(com.bosictsolution.invsale.bluetooth.print.GPrinterCommand.print);
        printBytes.add(bytes);
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(com.bosictsolution.invsale.bluetooth.print.GPrinterCommand.print);
        printBytes.add(GPrinterCommand.cut);
        PrintQueue.getQueue(getApplicationContext()).add(printBytes);
    }

    private void fillData(){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.printing));
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
            tvProductName = row.findViewById(R.id.tvProductName);
            tvPrice = row.findViewById(R.id.tvPrice);
            tvQuantity = row.findViewById(R.id.tvQuantity);
            tvNumber = row.findViewById(R.id.tvNumber);
            tvAmount = row.findViewById(R.id.tvAmount);

            if (paperWidth == 58) setItemTextSize(8);
            else if (paperWidth == 80) setItemTextSize(10);

            tvProductName.setText(lstSaleTran.get(i).getProductName());
            tvPrice.setText(appSetting.df.format(lstSaleTran.get(i).getSalePrice()));
            tvQuantity.setText(String.valueOf(lstSaleTran.get(i).getQuantity()));
            tvNumber.setText(String.valueOf(lstSaleTran.get(i).getNumber()));
            tvAmount.setText(appSetting.df.format(lstSaleTran.get(i).getAmount()));

            layoutList.addView(row);
        }
    }

    private void init(){
        connectionLiveData = new ConnectionLiveData(context);
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        db=new DatabaseAccess(context);
        progressDialog =new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
    }

    private void setLayoutResource(){
        layoutPrint=findViewById(R.id.layoutPrint);
        layoutList=findViewById(R.id.layoutList);
        layoutAdvancedPay=findViewById(R.id.layoutAdvancedPay);
        layoutPercent=findViewById(R.id.layoutPercent);
        layoutPercentGrandTotal=findViewById(R.id.layoutPercentGrandTotal);
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
        tvLabelSubtotal=findViewById(R.id.tvLabelSubtotal);
        tvLabelTotal=findViewById(R.id.tvLabelTotal);
        tvLabelAdvancedPay=findViewById(R.id.tvLabelAdvancedPay);
        tvLabelGrandTotal=findViewById(R.id.tvLabelGrandTotal);
        tvHeaderNo=findViewById(R.id.tvHeaderNo);
        tvHeaderProduct=findViewById(R.id.tvHeaderProduct);
        tvHeaderQuantity=findViewById(R.id.tvHeaderQuantity);
        tvHeaderPrice=findViewById(R.id.tvHeaderPrice);
        tvHeaderAmount=findViewById(R.id.tvHeaderAmount);
        layoutTax=findViewById(R.id.layoutTax);
        layoutCharges=findViewById(R.id.layoutCharges);
        layoutSubtotal=findViewById(R.id.layoutSubtotal);
    }

    private void setLayoutPrintSize(int paperWidth) {
        ViewGroup.LayoutParams params = layoutPrint.getLayoutParams();
        if (paperWidth == 58)
            params.width = 400;
        else if (paperWidth == 80)
            params.width = 600;
        layoutPrint.setLayoutParams(params);
    }

    private void setHeaderTextSize(int size) {
        tvTitle1.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvTitle2.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvTitle3.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvTitle4.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvTitle5.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvTitle6.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvSlipID.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvCustomer.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvSalePerson.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvMessage1.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvMessage2.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvMessage3.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvSubtotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvLabelTax.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvTax.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvLabelCharges.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvCharges.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvTotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvLabelVoucherDiscount.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvVoucherDiscount.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvAdvancedPay.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvGrandTotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvLabelPercent.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvPercentAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvPercentGrandTotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvLabelSubtotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvLabelTotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvLabelAdvancedPay.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvLabelGrandTotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvHeaderNo.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvHeaderProduct.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvHeaderQuantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvHeaderPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvHeaderAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    private void setItemTextSize(int size) {
        tvProductName.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvQuantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }
}