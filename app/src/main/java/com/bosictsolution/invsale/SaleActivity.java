package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.bosictsolution.invsale.adapter.ListItemProductInfoAdapter;
import com.bosictsolution.invsale.adapter.ListItemSaleAdapter;
import com.bosictsolution.invsale.adapter.MenuExpandableListAdapter;
import com.bosictsolution.invsale.adapter.ProductExpandableListAdapter;
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

        setAdapter();

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
                            showProductInfoDialog(etSearch.getText().toString());
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
                    if(etSearch.getText().toString().length()!=0) showProductInfoDialog(etSearch.getText().toString());
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

    private void setAdapter(){
        SaleTranData data=new SaleTranData();
        data.setNumber(1);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setAmount(2000);
        lstSaleTran.add(data);

        data=new SaleTranData();
        data.setNumber(2);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setAmount(2000);
        lstSaleTran.add(data);

        data=new SaleTranData();
        data.setNumber(3);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setAmount(2000);
        lstSaleTran.add(data);

        listItemSaleAdapter=new ListItemSaleAdapter(this,lstSaleTran,true);
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
        setProductToExpList(expList);

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

    private void setProductToExpList(ExpandableListView expList){
        ProductExpandableListAdapter expListAdapter;
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
        expListAdapter=new ProductExpandableListAdapter(this,listDataHeader,listDataChild);
        expList.setAdapter(expListAdapter);
    }

    private void createProduct(){
        ProductData data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1001");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1002");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1003");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1004");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1005");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1006");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1007");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1008");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1009");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(1);
        data.setProductCode("1010");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(2);
        data.setProductCode("1011");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(3);
        data.setProductCode("1012");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(4);
        data.setProductCode("1013");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(5);
        data.setProductCode("1014");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setSubMenuID(6);
        data.setProductCode("1015");
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
        data.setProductCode("1001");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setProductCode("1002");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setProductCode("1003");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setProductCode("1004");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setProductCode("1005");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setProductCode("1006");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setProductCode("1007");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setProductCode("1008");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setProductCode("1009");
        data.setProductName("Product ABC");
        lstProduct.add(data);

        data=new ProductData();
        data.setProductCode("1010");
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