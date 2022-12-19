package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bosictsolution.invsale.bluetooth.BondBtActivity;
import com.bosictsolution.invsale.bluetooth.BtDeviceListAdapter;
import com.bosictsolution.invsale.bluetooth.BtService;
import com.bosictsolution.invsale.bluetooth.print.PrintUtil;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.DatabaseAccess;

public class BTPrinterSettingActivity extends AppCompatActivity {

    Button btnFindPrinter,btnTestPrint,btnSavePrinter;
    EditText etBluetoothPrinter;
    RadioButton rdo58,rdo80;
    DatabaseAccess db;
    public static BluetoothAdapter BA;
    private BtDeviceListAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    final Context context = this;
    public static int paperWidth;
    AppSetting appSetting=new AppSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btprinter_setting);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.primary_500));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.bluetooth_printer_setting));
        fillData();

        btnFindPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(BTPrinterSettingActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(BTPrinterSettingActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                        return;
                    }
                }
                if (appSetting.checkAndRequestBluetoothOn(BTPrinterSettingActivity.this, BA)) {
                    Intent i = new Intent(BTPrinterSettingActivity.this, BondBtActivity.class);
                    startActivity(i);
                }
            }
        });
        btnTestPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateControl()) {
                    paperWidth=getPaperWidth();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (appSetting.checkAndRequestBluetoothOn(BTPrinterSettingActivity.this,BA)) {
                                if (appSetting.checkBluetoothDevice(BA,etBluetoothPrinter.getText().toString(),context,bluetoothAdapter,deviceAdapter)) {
                                    Intent intent = new Intent(getApplicationContext(), BtService.class);
                                    intent.setAction(PrintUtil.ACTION_PRINT_BITMAP);
                                    startService(intent);
                                }
                            }
                        }
                    }, 1000);
                }
            }
        });
        btnSavePrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateControl()){
                    String printerAddress=etBluetoothPrinter.getText().toString();
                    int paperWidth=getPaperWidth();
                    if(db.insertBluetoothPrinter(printerAddress,paperWidth))
                        Toast.makeText(context,getResources().getString(R.string.success),Toast.LENGTH_LONG).show();
                }
            }
        });
        rdo58.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)rdo80.setChecked(false);
            }
        });
        rdo80.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)rdo58.setChecked(false);
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

    @Override
    public void onResume() {
        super.onResume();
        if (BondBtActivity.bluetoothAddress != null) {
            if (BondBtActivity.bluetoothAddress.length() != 0) {
                etBluetoothPrinter.setText(BondBtActivity.bluetoothAddress);
            }
        }
    }

    private boolean validateControl(){
        if(etBluetoothPrinter.getText().toString().length()==0){
            etBluetoothPrinter.setError(getResources().getString(R.string.printer_not_found));
            return false;
        }else if(!rdo58.isChecked() && !rdo80.isChecked()){
            Toast.makeText(context,getResources().getString(R.string.choose_paper_width),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private int getPaperWidth() {
        int paperWidth = 0;
        if (rdo58.isChecked()) paperWidth = 58;
        else if (rdo80.isChecked()) paperWidth = 80;
        return paperWidth;
    }

    private void fillData() {
        etBluetoothPrinter.setText(db.getPrinterAddress());
        if (db.getPaperWidth() == 58) rdo58.setChecked(true);
        else if (db.getPaperWidth() == 80) rdo80.setChecked(true);
    }

    private void init() {
        db = new DatabaseAccess(context);
        BA = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter = new BtDeviceListAdapter(getApplicationContext(), null);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void setLayoutResource(){
        btnFindPrinter=findViewById(R.id.btnFindPrinter);
        btnTestPrint=findViewById(R.id.btnTestPrint);
        btnSavePrinter=findViewById(R.id.btnSavePrinter);
        etBluetoothPrinter=findViewById(R.id.etBluetoothPrinter);
        rdo58=findViewById(R.id.rdo58);
        rdo80=findViewById(R.id.rdo80);
    }
}