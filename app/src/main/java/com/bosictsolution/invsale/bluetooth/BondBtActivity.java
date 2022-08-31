package com.bosictsolution.invsale.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.bluetooth.bt.BluetoothActivity;
import com.bosictsolution.invsale.bluetooth.bt.BtUtil;
import com.bosictsolution.invsale.bluetooth.print.PrintQueue;
import com.bosictsolution.invsale.bluetooth.print.PrintUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BondBtActivity extends BluetoothActivity {

    ImageView imgBondIcon;
    TextView txtBondTitle;
    TextView txtBondSummary;
    LinearLayout llBondSearch;
    ListView listBondDevice;

    private static final int OPEN_BLUETOOTH_REQUEST = 100;
    private BtDeviceListAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    public static String bluetoothAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond_bt);

        setLayoutResource();

        if (null == deviceAdapter) {
            deviceAdapter = new BtDeviceListAdapter(getApplicationContext(), null);
        }
        listBondDevice.setAdapter(deviceAdapter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        llBondSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDeviceOrOpenBluetooth();
            }
        });

        listBondDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bondDevice(position);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        searchDeviceOrOpenBluetooth();
    }

    private void setLayoutResource(){
        imgBondIcon=(ImageView)findViewById(R.id.img_bond_icon);
        txtBondTitle=(TextView) findViewById(R.id.txt_bond_title);
        txtBondSummary=(TextView)findViewById(R.id.txt_bond_summary);
        llBondSearch=(LinearLayout) findViewById(R.id.ll_bond_search);
        listBondDevice=(ListView) findViewById(R.id.list_bond_device);
    }

    private void init() {
        if (null != bluetoothAdapter) {
            Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
            if (null != deviceSet) {
                deviceAdapter.addDevices(new ArrayList<BluetoothDevice>(deviceSet));
            }
        }
        if (!BtUtil.isOpen(bluetoothAdapter)) {
            txtBondTitle.setText("Not Connected to a Bluetooth Printer");
            txtBondSummary.setText("System Bluetooth is Off,Open!");
        } else {
            if (!PrintUtil.isBondPrinter(this, bluetoothAdapter)) {
                //未绑定蓝牙打印机器
                txtBondTitle.setText("Not Connected to a Bluetooth Printer");
                txtBondSummary.setText("Search for Bluetooth Printer");
            } else {
                //已绑定蓝牙设备
                txtBondTitle.setText(getPrinterName() + "Connected");
                String blueAddress = PrintUtil.getDefaultBluethoothDeviceAddress(this);
                if (TextUtils.isEmpty(blueAddress)) {
                    blueAddress = "Search for Bluetooth Printer";
                }
                txtBondSummary.setText(blueAddress);
            }
        }
    }

    /**
     * search device, if bluetooth is closed, open it
     */
    private void searchDeviceOrOpenBluetooth() {
        if (!BtUtil.isOpen(bluetoothAdapter)) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, OPEN_BLUETOOTH_REQUEST);
        } else {
            BtUtil.searchDevices(bluetoothAdapter);
        }
    }

    private String getPrinterName() {
        String dName = PrintUtil.getDefaultBluetoothDeviceName(this);
        if (TextUtils.isEmpty(dName)) {
            dName = "Unknown Device";
        }
        return dName;
    }

    @Override
    protected void onStop() {
        super.onStop();
        BtUtil.cancelDiscovery(bluetoothAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_BLUETOOTH_REQUEST && resultCode == Activity.RESULT_OK) {
            init();
        } else if (requestCode == OPEN_BLUETOOTH_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            showToast("Refused to use Bluetooth ");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceAdapter = null;
        bluetoothAdapter = null;
    }

    private void bondDevice(final int position) {
        if (null == deviceAdapter) {
            return;
        }
        final BluetoothDevice bluetoothDevice = deviceAdapter.getItem(position);
        if (null == bluetoothDevice) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Binding " + getPrinterName(bluetoothDevice.getName()) + "?")
                .setMessage("Click Ok to bind Bluetooth Device")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            BtUtil.cancelDiscovery(bluetoothAdapter);
                            PrintUtil.setDefaultBluetoothDeviceAddress(getApplicationContext(), bluetoothDevice.getAddress());
                            PrintUtil.setDefaultBluetoothDeviceName(getApplicationContext(), bluetoothDevice.getName());
                            bluetoothAddress=bluetoothDevice.getAddress();
                            if (null != deviceAdapter) {
                                deviceAdapter.setConnectedDeviceAddress(bluetoothDevice.getAddress());
                            }
                            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                                init();
                                goPrinterSetting();
                            } else {
                                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                createBondMethod.invoke(bluetoothDevice);
                            }
                            PrintQueue.getQueue(getApplicationContext()).disconnect();
                            String name = bluetoothDevice.getName();
                        } catch (Exception e) {
                            e.printStackTrace();
                            PrintUtil.setDefaultBluetoothDeviceAddress(getApplicationContext(), "");
                            PrintUtil.setDefaultBluetoothDeviceName(getApplicationContext(), "");
                            showToast("Bluetooth Tethering Fail,Try Again");
                        }
                    }
                })
                .create()
                .show();
    }

    private String getPrinterName(String dName) {
        if (TextUtils.isEmpty(dName)) {
            dName = "Unknown Device";
        }
        return dName;
    }

    /**
     * go printer setting activity
     */
    private void goPrinterSetting() {
        this.finish();
    }

    @Override
    public void btStartDiscovery(Intent intent) {
        txtBondTitle.setText("Searching for Bluetooth Device…");
        txtBondSummary.setText("");
    }

    @Override
    public void btFinishDiscovery(Intent intent) {
        txtBondTitle.setText("The Search is Complete");
        txtBondSummary.setText("Click Search");
    }

    @Override
    public void btStatusChanged(Intent intent) {
        init();
    }

    @Override
    public void btFoundDevice(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (null != deviceAdapter && device != null) {
            deviceAdapter.addDevices(device);
        }
    }

    @Override
    public void btBondStatusChange(Intent intent) {
        init();
        if (PrintUtil.isBondPrinter(getApplicationContext(), bluetoothAdapter)) {
            goPrinterSetting();
        }
    }

    @Override
    public void btPairingRequest(Intent intent) {
        showToast("Binding Printer");
    }
}