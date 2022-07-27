package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.adapter.ListItemProductInfoAdapter;
import com.bosictsolution.invsale.adapter.GeneralExpandableListAdapter;
import com.bosictsolution.invsale.adapter.ListItemSaleAdapterR;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.Confirmation;
import com.bosictsolution.invsale.data.CompanySettingData;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.ProductData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.SubMenuData;
import com.bosictsolution.invsale.listener.IConfirmation;
import com.bosictsolution.invsale.listener.ListItemProductInfoListener;
import com.bosictsolution.invsale.listener.ListItemSaleListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaleActivity extends AppCompatActivity implements ListItemSaleListener, ListItemProductInfoListener, IConfirmation {

    EditText etSearch;
    Button btnPay;
    RecyclerView rvItemSale;
    ImageButton btnAllProduct,btnRemove;
    TextView tvTax,tvSubtotal,tvCharges,tvTotal;
    Spinner spMainMenu;
    ExpandableListView expList;
    ListItemSaleAdapterR listItemSaleAdapterR;
    List<SaleTranData> lstSaleTran=new ArrayList<>();
    private Context context=this;
    List<ProductData> lstProduct=new ArrayList<>();
    List<SubMenuData> lstSubMenu=new ArrayList<>();
    List<MainMenuData> lstMainMenu=new ArrayList<>();
    List<String> listDataHeader;
    HashMap<String,List<String>> listDataChild;
    android.app.AlertDialog productInfoDialog,productMenuDialog;
    private ProgressDialog progressDialog;
    int tax, charges, total;
    AppSetting appSetting=new AppSetting();
    Confirmation confirmation=new Confirmation(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.menu_sale));

        getCompanySetting();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lstSaleTran.size()!=0) {
                    Intent i = new Intent(SaleActivity.this, PayDetailActivity.class);
                    i.putExtra("Total",total);
                    startActivity(i);
                }
            }
        });
        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT=2;
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(etSearch.getText().toString().length()!=0){
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
                    if(etSearch.getText().toString().length()!=0) searchProductByValue(etSearch.getText().toString());
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
    public void onQuantityClickListener(int position, TextView tvQuantity, TextView tvAmount) {
        showNumberDialog(lstSaleTran.get(position).getProductName(),position,tvQuantity,tvAmount);
    }

    @Override
    public void onRemoveClickListener(int position) {
        lstSaleTran.remove(position);
        setSaleTranAdapter();
    }

    @Override
    public void onItemLongClickListener(int position, TextView tvPrice, TextView tvAmount) {
        showSaleItemMenuDialog(lstSaleTran.get(position).getProductName(), position, tvPrice, tvAmount);
    }

    @Override
    public void onItemClickListener(int position,List<ProductData> list) {
        setSaleTran(position,list);
        setSaleTranAdapter();
        if(productInfoDialog!=null)productInfoDialog.dismiss();
    }

    @Override
    public void setOnConfirmOKClick() {
        lstSaleTran=new ArrayList<>();
        setSaleTranAdapter();
    }

    private void showNumberDialog(String productName,int position,TextView tvQuantity, TextView tvAmount) {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_number, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
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

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

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
                if (!tvInput.getText().toString().equals("0")){
                    editQuantity(position, tvQuantity, tvAmount, Integer.parseInt(tvInput.getText().toString()));
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

    private void showSaleItemMenuDialog(String productName,int position,TextView tvPrice, TextView tvAmount) {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_sale_item_menu, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final TextView tvTitle = v.findViewById(R.id.tvTitle);
        final CheckBox chkFOC = v.findViewById(R.id.chkFOC);
        final Button btnOK = v.findViewById(R.id.btnOK);

        tvTitle.setText(productName);
        if(tvPrice.getText().toString()=="0")chkFOC.setChecked(true);

        dialog.setCancelable(true);
        android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if(chkFOC.isChecked()){
                    tvPrice.setText("0");
                    tvAmount.setText("0");
                    lstSaleTran.get(position).setDefaultSalePrice(lstSaleTran.get(position).getSalePrice());
                    lstSaleTran.get(position).setSalePrice(0);
                    lstSaleTran.get(position).setTotalAmount(0);
                    lstSaleTran.get(position).setFOC(true);
                    calculateAmount();
                }else{
                    if(lstSaleTran.get(position).isFOC()){
                        tvPrice.setText(String.valueOf(lstSaleTran.get(position).getDefaultSalePrice()));
                        tvAmount.setText(String.valueOf(lstSaleTran.get(position).getQuantity()*lstSaleTran.get(position).getDefaultSalePrice()));
                        lstSaleTran.get(position).setSalePrice(lstSaleTran.get(position).getDefaultSalePrice());
                        lstSaleTran.get(position).setTotalAmount(lstSaleTran.get(position).getQuantity()*lstSaleTran.get(position).getDefaultSalePrice());
                        lstSaleTran.get(position).setDefaultSalePrice(0);
                        lstSaleTran.get(position).setFOC(false);
                        calculateAmount();
                    }
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
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
                List<ProductData> list=new ArrayList<>();

                int subMenuId = lstSubMenu.get(groupPosition).getSubMenuID();
                for (int i = 0; i < lstProduct.size(); i++) {
                    if (lstProduct.get(i).getSubMenuID() == subMenuId) {
                        list.add(lstProduct.get(i));
                    }
                }

                setSaleTran(childPosition,list);
                setSaleTranAdapter();
                if(productMenuDialog!=null)productMenuDialog.dismiss();
                return false;
            }
        });
    }

    private void showProductInfoDialog(String keyword,List<ProductData> lstProduct) {
        ListItemProductInfoAdapter listItemProductInfoAdapter;
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_product_info, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final RecyclerView rvProduct = v.findViewById(R.id.rvProduct);
        final TextView tvKeyword = v.findViewById(R.id.tvKeyword);

        tvKeyword.setText(getResources().getString(R.string.start_keyword)+keyword);

        listItemProductInfoAdapter=new ListItemProductInfoAdapter(lstProduct,context);
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

    private void setNumberValue(TextView textView,int inputValue) {
        String value = textView.getText().toString();
        if(value.startsWith("0"))value="";
        value += inputValue;
        textView.setText(value);
    }

    private void setDataToExpList(ExpandableListView expList){
        GeneralExpandableListAdapter expListAdapter;
        listDataHeader=new ArrayList<>();
        listDataChild=new HashMap<>();
        for(int i=0;i<lstSubMenu.size();i++){
            int subMenuID=lstSubMenu.get(i).getSubMenuID();
            String subMenuName=lstSubMenu.get(i).getSubMenuName();

            List<String> lstProductName=new ArrayList<>();
            for(int j=0;j<lstProduct.size();j++){
                if(lstProduct.get(j).getSubMenuID()==subMenuID){
                    lstProductName.add(lstProduct.get(j).getProductName());
                }
            }
            if(lstProductName.size()!=0){
                listDataChild.put(subMenuName, lstProductName);
                listDataHeader.add(subMenuName);
            }
        }
        expListAdapter=new GeneralExpandableListAdapter(this,listDataHeader,listDataChild);
        expList.setAdapter(expListAdapter);
    }

    private void setSaleTran(int position, List<ProductData> list) {
        SaleTranData data = new SaleTranData();
        data.setNumber(lstSaleTran.size() + 1);
        data.setProductID(list.get(position).getProductID());
        data.setProductName(list.get(position).getProductName());
        data.setSalePrice(list.get(position).getSalePrice());
        data.setQuantity(1);
        data.setTotalAmount(list.get(position).getSalePrice());
        lstSaleTran.add(data);
    }

    private void setSaleTranAdapter(){
        while (rvItemSale.getItemDecorationCount() > 0) {
            rvItemSale.removeItemDecorationAt(0);
        }
        listItemSaleAdapterR = new ListItemSaleAdapterR(this, lstSaleTran, true);
        rvItemSale.setAdapter(listItemSaleAdapterR);
        rvItemSale.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvItemSale.addItemDecoration(new DividerItemDecoration(rvItemSale.getContext(), DividerItemDecoration.VERTICAL));
        listItemSaleAdapterR.setOnListener(this);
        etSearch.setText("");
        calculateAmount();
    }

    private void editQuantity(int position,TextView tvQuantity, TextView tvAmount,int quantity){
        tvQuantity.setText(String.valueOf(quantity));
        tvAmount.setText(appSetting.df.format(quantity*lstSaleTran.get(position).getSalePrice()));
        lstSaleTran.get(position).setQuantity(quantity);
        lstSaleTran.get(position).setTotalAmount(quantity*lstSaleTran.get(position).getSalePrice());
        calculateAmount();
    }

    private void calculateAmount() {
        int subtotal = 0, taxAmount, chargesAmount;
        for (int i = 0; i < lstSaleTran.size(); i++) {
            subtotal += lstSaleTran.get(i).getTotalAmount();
        }
        taxAmount = (subtotal * tax) / 100;
        chargesAmount = (subtotal * charges) / 100;
        total = subtotal + taxAmount + chargesAmount;
        tvTax.setText(appSetting.df.format(taxAmount));
        tvCharges.setText(appSetting.df.format(chargesAmount));
        tvSubtotal.setText(appSetting.df.format(subtotal));
        tvTotal.setText(appSetting.df.format(total));
    }

    private void setMainMenu(Spinner spMainMenu){
        String[] mainMenus = new String[lstMainMenu.size()];
        for (int i = 0; i < lstMainMenu.size(); i++) {
            mainMenus[i] = lstMainMenu.get(i).getMainMenuName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, mainMenus);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMainMenu.setAdapter(adapter);
    }

    private void getMainMenu() {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getMainMenu().enqueue(new Callback<List<MainMenuData>>() {
            @Override
            public void onResponse(Call<List<MainMenuData>> call, Response<List<MainMenuData>> response) {
                progressDialog.dismiss();
                lstMainMenu = response.body();
                if (lstMainMenu.size() != 0)
                    setMainMenu(spMainMenu);
                else
                    Toast.makeText(context, getResources().getString(R.string.product_not_found), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<MainMenuData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("SaleActivity", t.getMessage());
            }
        });
    }

    private void getSubMenuByMainMenu(int mainMenuId) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getSubMenuByMainMenu(mainMenuId).enqueue(new Callback<List<SubMenuData>>() {
            @Override
            public void onResponse(Call<List<SubMenuData>> call, Response<List<SubMenuData>> response) {
                lstSubMenu=response.body();
                if (lstSubMenu.size() != 0) {
                    String subMenuIdList="";
                    for(int i=0;i<lstSubMenu.size();i++){
                        subMenuIdList+=lstSubMenu.get(i).getSubMenuID()+",";
                    }
                    if(subMenuIdList.length()!=0){
                        subMenuIdList=subMenuIdList.substring(0,subMenuIdList.length()-1);
                    }
                    getProductBySubMenuList(subMenuIdList);
                } else {
                    progressDialog.dismiss();
                    setDataToExpList(expList);
                    Toast.makeText(context, getResources().getString(R.string.product_not_found_by_menu), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubMenuData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("SaleActivity", t.getMessage());
            }
        });
    }

    private void getProductBySubMenuList(String subMenuIdList) {
        Api.getClient().getProductBySubMenuList(subMenuIdList).enqueue(new Callback<List<ProductData>>() {
            @Override
            public void onResponse(Call<List<ProductData>> call, Response<List<ProductData>> response) {
                progressDialog.dismiss();
                lstProduct=response.body();
                setDataToExpList(expList);
            }

            @Override
            public void onFailure(Call<List<ProductData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("SaleActivity", t.getMessage());
            }
        });
    }

    private void getCompanySetting(){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getCompanySetting().enqueue(new Callback<CompanySettingData>() {
            @Override
            public void onResponse(Call<CompanySettingData> call, Response<CompanySettingData> response) {
                progressDialog.dismiss();
                CompanySettingData data=response.body();
                if(data!=null){
                    tax=data.getTax();
                    charges =data.getServiceCharges();
                }
            }

            @Override
            public void onFailure(Call<CompanySettingData> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("SaleActivity", t.getMessage());
            }
        });
    }

    private void searchProductByValue(String value) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().searchProductByValue(value).enqueue(new Callback<List<ProductData>>() {
            @Override
            public void onResponse(Call<List<ProductData>> call, Response<List<ProductData>> response) {
                progressDialog.dismiss();
                List<ProductData> list = response.body();
                if (list.size() == 0)
                    Toast.makeText(context, getResources().getString(R.string.product_not_found_by_value), Toast.LENGTH_LONG).show();
                else if (list.size() > 1)
                    showProductInfoDialog(value, list);
                else if (list.size() == 1){
                    setSaleTran(0, list);
                    setSaleTranAdapter();
                }
            }

            @Override
            public void onFailure(Call<List<ProductData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("SaleActivity", t.getMessage());
            }
        });
    }

    private void setLayoutResource(){
        btnPay=findViewById(R.id.btnPay);
        rvItemSale =findViewById(R.id.rvItemSale);
        etSearch=findViewById(R.id.etSearch);
        btnAllProduct=findViewById(R.id.btnAllProduct);
        tvTax=findViewById(R.id.tvTax);
        tvCharges=findViewById(R.id.tvCharges);
        tvSubtotal=findViewById(R.id.tvSubtotal);
        tvTotal=findViewById(R.id.tvTotal);
        btnRemove=findViewById(R.id.btnRemove);

        progressDialog =new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }
}