package com.bosictsolution.invsale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function6;
import io.reactivex.functions.Function7;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.adapter.ListItemProductInfoAdapter;
import com.bosictsolution.invsale.adapter.GeneralExpandableListAdapter;
import com.bosictsolution.invsale.adapter.ListItemSaleAdapter;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.bluetooth.BtDeviceListAdapter;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.Confirmation;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.BankPaymentData;
import com.bosictsolution.invsale.data.CompanySettingData;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.LimitedDayData;
import com.bosictsolution.invsale.data.LocationData;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.PaymentData;
import com.bosictsolution.invsale.data.PaymentMethodData;
import com.bosictsolution.invsale.data.ProductData;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.StaffData;
import com.bosictsolution.invsale.data.SubMenuData;
import com.bosictsolution.invsale.data.VoucherSettingData;
import com.bosictsolution.invsale.listener.IConfirmation;
import com.bosictsolution.invsale.listener.ListItemProductInfoListener;
import com.bosictsolution.invsale.listener.ListItemSaleListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaleActivity extends AppCompatActivity implements ListItemSaleListener, ListItemProductInfoListener, IConfirmation {

    AppCompatEditText etSearch;
    Button btnPay;
    RecyclerView rvItemSale;
    ImageButton btnAllProduct, btnRemove;
    TextView tvTax, tvSubtotal, tvCharges, tvTotal;
    Spinner spMainMenu;
    ExpandableListView expList;
    ListItemSaleAdapter listItemSaleAdapter;
    List<SaleTranData> lstSaleTran = new ArrayList<>();
    private Context context = this;
    List<ProductData> lstProduct = new ArrayList<>();
    List<SubMenuData> lstSubMenu = new ArrayList<>();
    List<MainMenuData> lstMainMenu = new ArrayList<>();
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    android.app.AlertDialog productInfoDialog, productMenuDialog;
    private ProgressDialog progressDialog;
    int tax, charges, total, subtotal, taxAmount, chargesAmount, editSaleID,rowNo;
    AppSetting appSetting = new AppSetting();
    Confirmation confirmation = new Confirmation(this);
    DatabaseAccess db;
    public static boolean isSaleCompleted;
    ConnectionLiveData connectionLiveData;
    public static BluetoothAdapter BA;
    private BtDeviceListAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    boolean isSaleEdit;
    public static Activity activity;
    List<SaleTranData> lstSaleTranLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.primary_500));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);

        getTaxServiceCharges();
        checkConnection();
        loadData();

        Intent intent = getIntent();
        isSaleEdit = intent.getBooleanExtra("IsSaleEdit", false);

        if (isSaleEdit) {
            setTitle(getResources().getString(R.string.menu_sale_edit));
            editSaleID = intent.getIntExtra("SaleID", 0);
            lstSaleTranLog = new ArrayList<>();
            getTranSaleBySaleID(editSaleID);
        } else setTitle(getResources().getString(R.string.menu_sale));

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lstSaleTran.size() != 0) {
                    if (db.insertTranSale(lstSaleTran, isSaleEdit, lstSaleTranLog)) {
                        Intent i = new Intent(SaleActivity.this, PayDetailActivity.class);
                        i.putExtra("Subtotal", subtotal);
                        i.putExtra("Tax", tax);
                        i.putExtra("TaxAmount", taxAmount);
                        i.putExtra("Charges", charges);
                        i.putExtra("ChargesAmount", chargesAmount);
                        i.putExtra("Total", total);
                        i.putExtra("IsSaleEdit", isSaleEdit);
                        i.putExtra("SaleID", editSaleID);
                        startActivity(i);
                    }
                }
            }
        });
        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (etSearch.getText().toString().length() != 0) {
                            searchProductByValue(etSearch.getText().toString());
                        }
                    }
                }
                return false;
            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etSearch.getText().toString().length() != 0)
                        searchProductByValue(etSearch.getText().toString());
                }
                return false;
            }
        });
        btnAllProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductMenuDialog();
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lstSaleTran.size() != 0)
                    confirmation.showConfirmDialog(context, getResources().getString(R.string.delete_confirm_message));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSaleCompleted) {
            isSaleCompleted = false;
            lstSaleTran = new ArrayList<>();
            rowNo=0;
            setSaleTranAdapter();
        }
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

    private void init() {
        connectionLiveData = new ConnectionLiveData(context);
        db = new DatabaseAccess(context);
        activity = this;
        progressDialog = new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
        BA = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter = new BtDeviceListAdapter(getApplicationContext(), null);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.printer_info, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem printerInfoItem = menu.findItem(R.id.action_printer_info);
        if (!checkBluetoothPrinterStatus())
            printerInfoItem.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_print_disabled_24));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_printer_info:
                showBluetoothPrinterStatus();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showBluetoothPrinterStatus() {
        if (!db.isSetupBluetoothPrinter()) {
            Toast.makeText(context, context.getResources().getString(R.string.bluetooth_printer_setup_message), Toast.LENGTH_LONG).show();
        } else if (!appSetting.checkBluetoothOn(BA)) {
            Toast.makeText(context, context.getResources().getString(R.string.bluetooth_off_message), Toast.LENGTH_LONG).show();
        } else if (!appSetting.checkBluetoothDevice(BA, db.getPrinterAddress(), context, bluetoothAdapter, deviceAdapter)) {
            Toast.makeText(context, context.getResources().getString(R.string.bluetooth_printer_disconnect_message), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.bluetooth_printer_ready), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkBluetoothPrinterStatus() {
        if (!db.isSetupBluetoothPrinter()) return false;
        else if (!appSetting.checkBluetoothOn(BA)) return false;
        else if (!appSetting.checkBluetoothDevice(BA, db.getPrinterAddress(), context, bluetoothAdapter, deviceAdapter))
            return false;
        return true;
    }

    @Override
    public void onQuantityClickListener(int position, TextView tvQuantity, TextView tvAmount) {
        showNumberDialog(lstSaleTran.get(position).getProductName(), lstSaleTran.get(position).getQuantity(), position, tvQuantity, tvAmount, true, null, 0);
    }

    @Override
    public void onRemoveClickListener(int position) {
        /*lstSaleTran.remove(position);
        setSaleTranAdapter();*/
    }

    @Override
    public void onMoreClickListener(int position, TextView tvPrice, TextView tvAmount, TextView tvDiscount) {
        showSaleItemMenuDialog(lstSaleTran.get(position).getProductName(), position, tvPrice, tvAmount, tvDiscount);
    }

    @Override
    public void onItemLongClickListener(int position, TextView tvPrice, TextView tvAmount, TextView tvDiscount) {
        showSaleItemMenuDialog(lstSaleTran.get(position).getProductName(), position, tvPrice, tvAmount, tvDiscount);
    }

    @Override
    public void onItemClickListener(int position, List<ProductData> list) {
        setSaleTran(position, list, 1);
        setSaleTranAdapter();
        if (productInfoDialog != null) productInfoDialog.dismiss();
    }

    @Override
    public void setOnConfirmOKClick() {
        lstSaleTran = new ArrayList<>();
        setSaleTranAdapter();
    }

    private void showNumberDialog(String productName, int quantity, int position, TextView tvQuantity, TextView tvAmount, boolean isEdit, List<ProductData> lstProductBySubMenu, int productPosition) {
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
        final Button btnAdd = v.findViewById(R.id.btnAdd);

        tvTitle.setText(productName);

        if (isEdit) {
            btnAdd.setVisibility(View.GONE);
            btnOK.setVisibility(View.VISIBLE);
            tvInput.setText(String.valueOf(quantity));
        } else {
            btnAdd.setVisibility(View.VISIBLE);
            btnOK.setVisibility(View.GONE);
        }

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = tvInput.getText().toString();
                if (value.length() != 0) {
                    value = value.substring(0, value.length() - 1);
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
                if (!tvInput.getText().toString().equals("0") && tvInput.getText().toString().length() != 0) {
                    editQuantity(position, tvQuantity, tvAmount, Integer.parseInt(tvInput.getText().toString()));
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvInput.getText().toString().equals("0") && tvInput.getText().toString().length() != 0) {
                    alertDialog.dismiss();
                    setSaleTran(productPosition, lstProductBySubMenu, Integer.parseInt(tvInput.getText().toString()));
                    setSaleTranAdapter();
                    Toast.makeText(context, getResources().getString(R.string.added), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 0);
            }
        });
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 1);
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 2);
            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 3);
            }
        });
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 4);
            }
        });
        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 5);
            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 6);
            }
        });
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 7);
            }
        });
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 8);
            }
        });
        btnNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNumberValue(tvInput, 9);
            }
        });
    }

    private void showSaleItemMenuDialog(String productName, int position, TextView tvPrice, TextView tvAmount, TextView tvDiscount) {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_sale_item_menu, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final TextView tvTitle = v.findViewById(R.id.tvTitle);
        final TextView tvPayDiscount = v.findViewById(R.id.tvPayDiscount);
        final TextView tvEditDiscount = v.findViewById(R.id.tvEditDiscount);
        final TextView tvRemoveDiscount = v.findViewById(R.id.tvRemoveDiscount);
        final TextView tvPayFOC = v.findViewById(R.id.tvPayFOC);
        final TextView tvCancelFOC = v.findViewById(R.id.tvCancelFOC);
        final TextView tvDeleteItem = v.findViewById(R.id.tvDeleteItem);

        tvTitle.setText(productName);
        if (lstSaleTran.get(position).isFOC()) tvPayFOC.setVisibility(View.GONE);
        else tvCancelFOC.setVisibility(View.GONE);

        if (lstSaleTran.get(position).getDiscountPercent() == 0) {
            tvEditDiscount.setVisibility(View.GONE);
            tvRemoveDiscount.setVisibility(View.GONE);
        } else tvPayDiscount.setVisibility(View.GONE);

        dialog.setCancelable(true);
        android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        tvPayDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showDiscountDialog(position, tvDiscount, tvAmount, false);
            }
        });

        tvEditDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showDiscountDialog(position, tvDiscount, tvAmount, true);
            }
        });

        tvRemoveDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                lstSaleTran.get(position).setDiscountPercent(0);
                tvDiscount.setText("");
                calculateDiscountAmount(position, 0, tvAmount);
                calculateAmount();
            }
        });

        tvPayFOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                tvPrice.setText("0");
                tvAmount.setText("0");
                lstSaleTran.get(position).setDefaultSalePrice(lstSaleTran.get(position).getSalePrice());
                lstSaleTran.get(position).setSalePrice(0);
                lstSaleTran.get(position).setAmount(0);
                lstSaleTran.get(position).setFOC(true);
                calculateAmount();
            }
        });

        tvCancelFOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (lstSaleTran.get(position).isFOC()) {
                    tvPrice.setText(appSetting.df.format(lstSaleTran.get(position).getDefaultSalePrice()));
                    lstSaleTran.get(position).setSalePrice(lstSaleTran.get(position).getDefaultSalePrice());
                    lstSaleTran.get(position).setDefaultSalePrice(0);
                    lstSaleTran.get(position).setFOC(false);
                    int discountPercent = lstSaleTran.get(position).getDiscountPercent();
                    calculateDiscountAmount(position, discountPercent, tvAmount);
                    calculateAmount();
                }
            }
        });

        tvDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSaleEdit) {
                    if (lstSaleTran.get(position).isNewInEdit()) {
                        for (int i = 0; i < lstSaleTranLog.size(); i++) { // delete from log
                            if (lstSaleTranLog.get(i).getRowNo() == lstSaleTran.get(position).getRowNo()) {
                                lstSaleTranLog.remove(i);
                                break;
                            }
                        }
                    } else {
                        lstSaleTran.get(position).setActionCode(AppConstant.Action.DeleteActionCode);
                        lstSaleTran.get(position).setActionName(AppConstant.Action.DeleteActionName);
                        lstSaleTranLog.add(lstSaleTran.get(position));
                    }
                }
                lstSaleTran.remove(position);
                int number = 1;
                for (int i = 0; i < lstSaleTran.size(); i++) {
                    lstSaleTran.get(i).setNumber(number);
                    number += 1;
                }
                setSaleTranAdapter();
                alertDialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void showDiscountDialog(int position, TextView tvDiscount, TextView tvAmount, boolean isEdit) {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_discount, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final TextInputLayout inputDiscount = v.findViewById(R.id.inputDiscount);
        final EditText etDiscount = v.findViewById(R.id.etDiscount);
        final Button btnCancel = v.findViewById(R.id.btnCancel);
        final Button btnOK = v.findViewById(R.id.btnOK);

        if (isEdit)
            etDiscount.setText(String.valueOf(lstSaleTran.get(position).getDiscountPercent()));

        dialog.setCancelable(true);
        android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etDiscount.getText().toString().length() == 0) {
                    inputDiscount.setError(getResources().getString(R.string.enter_value));
                    return;
                }
                int discountPercent = Integer.parseInt(etDiscount.getText().toString());
                if (validatePercent(discountPercent)) {
                    lstSaleTran.get(position).setDiscountPercent(discountPercent);
                    tvDiscount.setText(discountPercent + "%");
                    calculateDiscountAmount(position, discountPercent, tvAmount);
                    calculateAmount();
                    alertDialog.dismiss();
                } else {
                    inputDiscount.setError(getResources().getString(R.string.enter_valid_value));
                }
            }
        });
    }

    private boolean validatePercent(int value) {
        if (value == 0 || value > 100) return false;
        return true;
    }

    private void showProductMenuDialog() {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_product_menu, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        spMainMenu = v.findViewById(R.id.spMainMenu);
        expList = v.findViewById(R.id.list);

        getMainMenu();

        dialog.setCancelable(true);
        productMenuDialog = dialog.create();
        productMenuDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productMenuDialog.dismiss();
            }
        });
        spMainMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (lstMainMenu.size() != 0)
                    getSubMenuByMainMenu(lstMainMenu.get(i).getMainMenuID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                List<ProductData> list = new ArrayList<>();

                int subMenuId = lstSubMenu.get(groupPosition).getSubMenuID();
                for (int i = 0; i < lstProduct.size(); i++) {
                    if (lstProduct.get(i).getSubMenuID() == subMenuId) {
                        list.add(lstProduct.get(i));
                    }
                }

                showNumberDialog(list.get(childPosition).getProductName(), 0, 0, null, null, false, list, childPosition);
                /*setSaleTran(childPosition,list);
                setSaleTranAdapter();
                Toast.makeText(context, getResources().getString(R.string.added), Toast.LENGTH_SHORT).show();*/
                //if(productMenuDialog!=null)productMenuDialog.dismiss();
                return false;
            }
        });
    }

    private void showProductInfoDialog(String keyword, List<ProductData> lstProduct) {
        ListItemProductInfoAdapter listItemProductInfoAdapter;
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_product_info, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final RecyclerView rvProduct = v.findViewById(R.id.rvProduct);
        final TextView tvKeyword = v.findViewById(R.id.tvKeyword);

        tvKeyword.setText(getResources().getString(R.string.start_keyword) + keyword);

        listItemProductInfoAdapter = new ListItemProductInfoAdapter(lstProduct, context);
        rvProduct.setAdapter(listItemProductInfoAdapter);
        rvProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listItemProductInfoAdapter.setOnListener(this);

        dialog.setCancelable(true);
        productInfoDialog = dialog.create();
        productInfoDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productInfoDialog.dismiss();
            }
        });
    }

    private void setNumberValue(TextView textView, int inputValue) {
        String value = textView.getText().toString();
        if (value.startsWith("0")) value = "";
        value += inputValue;
        textView.setText(value);
    }

    private void setDataToExpList(ExpandableListView expList) {
        GeneralExpandableListAdapter expListAdapter;
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        for (int i = 0; i < lstSubMenu.size(); i++) {
            int subMenuID = lstSubMenu.get(i).getSubMenuID();
            String subMenuName = lstSubMenu.get(i).getSubMenuName();

            List<String> lstProductName = new ArrayList<>();
            for (int j = 0; j < lstProduct.size(); j++) {
                if (lstProduct.get(j).getSubMenuID() == subMenuID) {
                    lstProductName.add(lstProduct.get(j).getProductName());
                }
            }
            listDataChild.put(subMenuName, lstProductName);
            listDataHeader.add(subMenuName);
        }
        expListAdapter = new GeneralExpandableListAdapter(this, listDataHeader, listDataChild);
        expList.setAdapter(expListAdapter);
    }

    private void setSaleTran(int position, List<ProductData> list, int quantity) {
        SaleTranData data = new SaleTranData();
        data.setRowNo(++rowNo);
        data.setNumber(lstSaleTran.size() + 1);
        data.setProductID(list.get(position).getProductID());
        data.setProductName(list.get(position).getProductName());
        data.setSalePrice(list.get(position).getSalePrice());
        data.setQuantity(quantity);
        data.setAmount(list.get(position).getSalePrice() * quantity);
        if (isSaleEdit) {
            data.setNewInEdit(true);
            data.setActionCode(AppConstant.Action.NewActionCode);
            data.setActionName(AppConstant.Action.NewActionName);
            lstSaleTranLog.add(data);
        }
        lstSaleTran.add(data);
    }

    private void setSaleTranAdapter() {
        while (rvItemSale.getItemDecorationCount() > 0) {
            rvItemSale.removeItemDecorationAt(0);
        }
        listItemSaleAdapter = new ListItemSaleAdapter(this, lstSaleTran, true);
        rvItemSale.setAdapter(listItemSaleAdapter);
        rvItemSale.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvItemSale.addItemDecoration(new DividerItemDecoration(rvItemSale.getContext(), DividerItemDecoration.VERTICAL));
        listItemSaleAdapter.setOnListener(this);
        etSearch.setText("");
        calculateAmount();
    }

    private void editQuantity(int position, TextView tvQuantity, TextView tvAmount, int quantity) {
        if (lstSaleTran.get(position).getQuantity() == quantity) return;

        int originalQuantity = 0;
        if (isSaleEdit && !lstSaleTran.get(position).isNewInEdit()) {
            originalQuantity = lstSaleTran.get(position).getQuantity();
        }

        tvQuantity.setText(String.valueOf(quantity));
        lstSaleTran.get(position).setQuantity(quantity);
        int discountPercent = lstSaleTran.get(position).getDiscountPercent();
        calculateDiscountAmount(position, discountPercent, tvAmount);
        calculateAmount();

        if (isSaleEdit && !lstSaleTran.get(position).isNewInEdit()) {
            boolean isExistingInLog = false;
            for (int i = 0; i < lstSaleTranLog.size(); i++) {
                if (lstSaleTranLog.get(i).getRowNo() == lstSaleTran.get(position).getRowNo()) { // again edit
                    lstSaleTranLog.get(i).setQuantity(quantity);
                    isExistingInLog = true;
                    break;
                }
            }
            if (!isExistingInLog) {
                lstSaleTran.get(position).setOrginalQuantity(originalQuantity);
                lstSaleTran.get(position).setActionCode(AppConstant.Action.EditActionCode);
                lstSaleTran.get(position).setActionName(AppConstant.Action.EditActionName);
                lstSaleTranLog.add(lstSaleTran.get(position));
            }
        }
    }

    private void calculateDiscountAmount(int position, int discountPercent, TextView tvAmount) {
        int salePrice = lstSaleTran.get(position).getSalePrice();
        int quantity = lstSaleTran.get(position).getQuantity();
        int discountAmount = ((salePrice * quantity) * discountPercent) / 100;
        int amount = (quantity * salePrice) - discountAmount;
        lstSaleTran.get(position).setDiscount(discountAmount);
        lstSaleTran.get(position).setAmount(amount);
        tvAmount.setText(appSetting.df.format(amount));
    }

    private void calculateAmount() {
        subtotal = 0;
        for (int i = 0; i < lstSaleTran.size(); i++) {
            subtotal += lstSaleTran.get(i).getAmount();
        }
        chargesAmount = (subtotal * charges) / 100;
        taxAmount = ((subtotal + chargesAmount) * tax) / 100;
        total = subtotal + taxAmount + chargesAmount;
        tvTax.setText(appSetting.df.format(taxAmount));
        tvCharges.setText(appSetting.df.format(chargesAmount));
        tvSubtotal.setText(appSetting.df.format(subtotal));
        tvTotal.setText(appSetting.df.format(total));
    }

    private void setMainMenu(Spinner spMainMenu) {
        String[] mainMenus = new String[lstMainMenu.size()];
        for (int i = 0; i < lstMainMenu.size(); i++) {
            mainMenus[i] = lstMainMenu.get(i).getMainMenuName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_item, mainMenus);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMainMenu.setAdapter(adapter);
    }

    private void getMainMenu() {
        lstMainMenu = db.getMainMenu();
        if (lstMainMenu.size() != 0)
            setMainMenu(spMainMenu);
        else
            Toast.makeText(context, getResources().getString(R.string.product_not_found), Toast.LENGTH_LONG).show();
    }

    private void getSubMenuByMainMenu(int mainMenuId) {
        lstSubMenu = db.getSubMenuByMainMenu(mainMenuId);
        if (lstSubMenu.size() != 0) {
            String subMenuIdList = "";
            for (int i = 0; i < lstSubMenu.size(); i++) {
                subMenuIdList += lstSubMenu.get(i).getSubMenuID() + ",";
            }
            if (subMenuIdList.length() != 0) {
                subMenuIdList = subMenuIdList.substring(0, subMenuIdList.length() - 1);
            }
            getProductBySubMenuList(subMenuIdList);
        } else {
            setDataToExpList(expList);
            Toast.makeText(context, getResources().getString(R.string.product_not_found_by_menu), Toast.LENGTH_LONG).show();
        }
    }

    private void getProductBySubMenuList(String subMenuIdList) {
        lstProduct = db.getProductBySubMenuList(subMenuIdList);
        setDataToExpList(expList);
    }

    private void getTaxServiceCharges() {
        CompanySettingData data = db.getTaxServiceCharges();
        if (data != null) {
            tax = data.getTax();
            charges = data.getServiceCharges();
        }
    }

    private void searchProductByValue(String value) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        List<ProductData> list = db.searchProductByValue(value);
        progressDialog.dismiss();
        if (list.size() == 0)
            Toast.makeText(context, getResources().getString(R.string.product_not_found_by_value), Toast.LENGTH_LONG).show();
        else if (list.size() > 1)
            showProductInfoDialog(value, list);
        else if (list.size() == 1) {
            setSaleTran(0, list, 1);
            setSaleTranAdapter();
        }
    }

    private void setLayoutResource() {
        btnPay = findViewById(R.id.btnPay);
        rvItemSale = findViewById(R.id.rvItemSale);
        etSearch = findViewById(R.id.etSearch);
        btnAllProduct = findViewById(R.id.btnAllProduct);
        tvTax = findViewById(R.id.tvTax);
        tvCharges = findViewById(R.id.tvCharges);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        btnRemove = findViewById(R.id.btnRemove);
    }

    private void loadData() {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Observable<List<PaymentData>> obPayment = Api.getClient().getPayment();
        Observable<List<PaymentMethodData>> obPaymentMethod = Api.getClient().getPaymentMethod();
        Observable<List<BankPaymentData>> obBankPayment = Api.getClient().getBankPayment();
        Observable<List<LimitedDayData>> obLimitedDay = Api.getClient().getLimitedDay();
        Observable<List<VoucherSettingData>> obVoucherSetting = Api.getClient().getVoucherSetting();
        Observable<List<StaffData>> obStaff = Api.getClient().getStaff();

        Observable<Boolean> result = io.reactivex.Observable.zip(
                obPayment.subscribeOn(Schedulers.io()), obPaymentMethod.subscribeOn(Schedulers.io()), obBankPayment.subscribeOn(Schedulers.io()),
                obLimitedDay.subscribeOn(Schedulers.io()), obVoucherSetting.subscribeOn(Schedulers.io()), obStaff.subscribeOn(Schedulers.io()),
                new Function6<List<PaymentData>, List<PaymentMethodData>, List<BankPaymentData>, List<LimitedDayData>, List<VoucherSettingData>, List<StaffData>, Boolean>() {
                    @NonNull
                    @Override
                    public Boolean apply( @NonNull List<PaymentData> paymentData, @NonNull List<PaymentMethodData> paymentMethodData, @NonNull List<BankPaymentData> bankPaymentData, @NonNull List<LimitedDayData> limitedDayData, @NonNull List<VoucherSettingData> voucherSettingData, @NonNull List<StaffData> staffData) throws Exception {
                        db.insertPayment(paymentData);
                        db.insertPaymentMethod(paymentMethodData);
                        db.insertBankPayment(bankPaymentData);
                        db.insertLimitedDay(limitedDayData);
                        db.insertVoucherSetting(voucherSettingData);
                        db.insertStaff(staffData);
                        progressDialog.dismiss();
                        return true;
                    }
                });

        result.observeOn(AndroidSchedulers.mainThread()).subscribeWith(new io.reactivex.Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.i("SaleActivity", "subscribe");
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                Log.i("SaleActivity", "next");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                progressDialog.dismiss();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
                Log.i("SaleActivity", "complete");
            }
        });
    }

    private void getTranSaleBySaleID(int saleId) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.get_edit_data));
        Api.getClient().getTranSaleBySaleID(saleId).enqueue(new Callback<List<SaleTranData>>() {
            @Override
            public void onResponse(Call<List<SaleTranData>> call, Response<List<SaleTranData>> response) {
                if (response.body() == null) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    return;
                }
                setSaleTranEditData(response.body());
                setSaleTranAdapter();
            }

            @Override
            public void onFailure(Call<List<SaleTranData>> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSaleTranEditData(List<SaleTranData> list) {
        SaleTranData data;
        for (int i = 0; i < list.size(); i++) {
            data = new SaleTranData();
            data.setRowNo(++rowNo);
            data.setNumber(i + 1);
            data.setProductID(list.get(i).getProductID());
            data.setProductName(list.get(i).getProductName());
            data.setSalePrice(list.get(i).getSalePrice());
            data.setQuantity(list.get(i).getQuantity());
            data.setAmount(list.get(i).getAmount());
            data.setDiscountPercent(list.get(i).getDiscountPercent());
            data.setDiscount(list.get(i).getDiscount());
            data.setFOC(list.get(i).isFOC());
            lstSaleTran.add(data);
        }
    }
}