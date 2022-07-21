package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.adapter.ListItemProductInfoAdapter;
import com.bosictsolution.invsale.adapter.ListItemSaleAdapter;
import com.bosictsolution.invsale.adapter.GeneralExpandableListAdapter;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.data.ProductData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.SubMenuData;
import com.bosictsolution.invsale.listener.ListItemSaleListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaleActivity extends AppCompatActivity implements ListItemSaleListener {

    EditText etSearch;
    Button btnPay;
    ListView lvItemSale;
    ImageButton btnAllProduct;
    ListItemSaleAdapter listItemSaleAdapter;
    List<SaleTranData> lstSaleTran=new ArrayList<>();
    private Context context=this;
    List<ProductData> lstProduct=new ArrayList<>();
    List<SubMenuData> lstSubMenu=new ArrayList<>();
    List<String> listDataHeader;
    HashMap<String,List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.menu_sale));

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SaleActivity.this,PayDetailActivity.class);
                startActivity(i);
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
    }

    private void setSaleTranAdapter() {
        listItemSaleAdapter = new ListItemSaleAdapter(this, lstSaleTran, true);
        lvItemSale.setAdapter(listItemSaleAdapter);
        listItemSaleAdapter.setOnListener(this);
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

    private void searchProductByValue(String value) {
        Api.getClient().searchProductByValue(value).enqueue(new Callback<List<ProductData>>() {
            @Override
            public void onResponse(Call<List<ProductData>> call, Response<List<ProductData>> response) {
                List<ProductData> list = response.body();
                if (list.size() == 0)
                    Toast.makeText(context, getResources().getString(R.string.product_not_found), Toast.LENGTH_LONG).show();
                else if (list.size() > 1)
                    showProductInfoDialog(value);
                else if (list.size() == 1) {
                    SaleTranData data = new SaleTranData();
                    data.setNumber(lstSaleTran.size() + 1);
                    data.setProductID(list.get(0).getProductID());
                    data.setProductName(list.get(0).getProductName());
                    data.setSalePrice(list.get(0).getSalePrice());
                    data.setQuantity(1);
                    data.setTotalAmount(list.get(0).getSalePrice());
                    lstSaleTran.add(data);
                    setSaleTranAdapter();
                }
            }

            @Override
            public void onFailure(Call<List<ProductData>> call, Throwable t) {

            }
        });
    }

    private void setLayoutResource(){
        btnPay=findViewById(R.id.btnPay);
        lvItemSale=findViewById(R.id.lvItemSale);
        etSearch=findViewById(R.id.etSearch);
        btnAllProduct=findViewById(R.id.btnAllProduct);
    }

    @Override
    public void onQuantityClickListener(int position, TextView tvQuantity, TextView tvAmount) {
            showNumberDialog();
    }

    @Override
    public void onRemoveClickListener(int position) {

    }

    private void showNumberDialog() {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_number, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void showProductMenuDialog() {
        String[] mainMenus={"Main Menu 1", "Main Menu 2", "Main Menu 3", "Main Menu 4"};

        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_product_menu, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final Spinner spMainMenu = v.findViewById(R.id.spMainMenu);
        final ExpandableListView expList = v.findViewById(R.id.list);

        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,mainMenus);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMainMenu.setAdapter(adapter);

        createSubMenu();
        createProduct();
        setDataToExpList(expList);

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
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

    private void createProduct(){
        ProductData data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1001");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1002");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1003");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1004");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1005");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1006");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1007");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1008");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1009");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setCode("1010");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(2);
        data.setCode("1011");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(3);
        data.setCode("1012");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(4);
        data.setCode("1013");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(5);
        data.setCode("1014");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(6);
        data.setCode("1015");
        data.setProductName("Product ABC");
        lstProduct.add(data);
    }

    private void createSubMenu(){
        SubMenuData data=new SubMenuData();
        data.setSubMenuID(1);
        data.setSubMenuName("Sub Menu 1/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(2);
        data.setSubMenuName("Sub Menu 2/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(3);
        data.setSubMenuName("Sub Menu 3/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(4);
        data.setSubMenuName("Sub Menu 4/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(5);
        data.setSubMenuName("Sub Menu 5/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(6);
        data.setSubMenuName("Sub Menu 6/1");
        lstSubMenu.add(data);
    }

    private void showProductInfoDialog(String keyword) {
        List<ProductData> lstProduct=new ArrayList<>();
        ListItemProductInfoAdapter listItemProductInfoAdapter;
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_product_info, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final RecyclerView rvProduct = v.findViewById(R.id.rvProduct);

        ProductData data=new ProductData();
        data.setCode("1001");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setCode("1002");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setCode("1003");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setCode("1004");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setCode("1005");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setCode("1006");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setCode("1007");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setCode("1008");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setCode("1009");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setCode("1010");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        listItemProductInfoAdapter=new ListItemProductInfoAdapter(lstProduct,context);
        rvProduct.setAdapter(listItemProductInfoAdapter);
        rvProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}