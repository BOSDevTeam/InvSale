package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.adapter.ListItemProductAdapter;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.ProductData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity implements ListItemProductAdapter.IListener {

    TextView tvSubMenu,tvProductName,tvPrice,tvQuantity,tvAlreadyInOrder,tvDescription;
    Button btnAddToOrder;
    ImageButton btnPlus,btnMinus;
    RecyclerView rvProduct;
    ExtendedFloatingActionButton fab;
    ListItemProductAdapter listItemProductAdapter;
    List<ProductData> lstProduct=new ArrayList<>();
    private Context context=this;
    BottomSheetBehavior sheetBehavior;
    LinearLayout bottom_sheet;
    ImageButton btnBottomSheetClose;
    DatabaseAccess db;
    private ProgressDialog progressDialog;
    String mainMenuName,subMenuName;
    int subMenuId,productId,salePrice,productPosition;
    String productName,description;
    AppSetting appSetting=new AppSetting();
    public static Activity activity;
    ConnectionLiveData connectionLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.primary_500));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);

        Intent i=getIntent();
        mainMenuName=i.getStringExtra("MainMenuName");
        subMenuName=i.getStringExtra("SubMenuName");
        subMenuId=i.getIntExtra("SubMenuID",subMenuId);

        setTitle(mainMenuName);
        checkConnection();
        fillData();

        sheetBehavior=BottomSheetBehavior.from(bottom_sheet);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ProductActivity.this, SaleOrderSummaryActivity.class);
                startActivity(i);
            }
        });
        btnBottomSheetClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(tvQuantity.getText().toString());
                quantity += 1;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(tvQuantity.getText().toString());
                if (quantity == 1) return;
                else quantity -= 1;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });
        btnAddToOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity=Integer.parseInt(tvQuantity.getText().toString());
                if(db.insertUpdateTranSaleOrder(productId,quantity)){
                    setFab();
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    ProductData productData=new ProductData();
                    productData.setProductID(productId);
                    productData.setProductName(productName);
                    productData.setDescription(description);
                    productData.setSalePrice(salePrice);
                    productData.setQuantity(quantity);
                    lstProduct.set(productPosition,productData);
                    listItemProductAdapter.setItem(lstProduct);
                    listItemProductAdapter.notifyDataSetChanged();
                    Toast.makeText(context,getResources().getString(R.string.added),Toast.LENGTH_LONG).show();
                }
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
    protected void onResume() {
        super.onResume();
        getProductBySubMenu(subMenuId);
        setAdapter();
        setFab();
    }

    private void setFab() {
        int totalSaleOrderItem = db.getTotalSaleOrderItem();
        int totalSaleOrderQty = db.getTotalSaleOrderQty();
        if (totalSaleOrderItem != 0) {
            fab.setText("Order:" + totalSaleOrderItem + "(" + totalSaleOrderQty + ") Items - " + db.getHomeCurrency() + getResources().getString(R.string.space) + appSetting.df.format(db.getTotalSaleOrderAmount()));
            fab.setVisibility(View.VISIBLE);
        } else fab.setVisibility(View.GONE);
    }

    private void init(){
        connectionLiveData = new ConnectionLiveData(context);
        activity=this;
        db=new DatabaseAccess(context);
        progressDialog =new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
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
        tvSubMenu.setText(subMenuName);
        getProductBySubMenu(subMenuId);
        setAdapter();
    }

    private void getProductBySubMenu(int subMenuId){
        lstProduct=db.getProductBySubMenu(subMenuId);
    }

    private void setAdapter(){
        listItemProductAdapter=new ListItemProductAdapter(lstProduct,context);
        rvProduct.setAdapter(listItemProductAdapter);
        rvProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listItemProductAdapter.setListener(this);
    }

    private void setLayoutResource() {
        tvSubMenu = findViewById(R.id.tvSubMenu);
        rvProduct = findViewById(R.id.rvProduct);
        bottom_sheet=findViewById(R.id.bottom_sheet);
        btnBottomSheetClose=findViewById(R.id.btnBottomSheetClose);
        tvProductName = findViewById(R.id.tvProductName);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvAlreadyInOrder = findViewById(R.id.tvAlreadyInOrder);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnAddToOrder = findViewById(R.id.btnAddToOrder);
        fab=findViewById(R.id.fab);
    }

    @Override
    public void onProductClicked(int position) {
        productPosition = position;
        productId = lstProduct.get(position).getProductID();
        productName = lstProduct.get(position).getProductName();
        description = lstProduct.get(position).getDescription();
        salePrice = lstProduct.get(position).getSalePrice();
        tvProductName.setText(productName);
        tvPrice.setText(db.getHomeCurrency() + context.getResources().getString(R.string.space) + appSetting.df.format(salePrice));

        if (description != null && description.length() != 0) {
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);
        } else tvDescription.setVisibility(View.GONE);

        int quantity = db.getSaleOrderQuantityByProduct(productId);
        if (quantity == 0) {
            tvAlreadyInOrder.setVisibility(View.GONE);
            tvQuantity.setText("1");
        } else {
            tvAlreadyInOrder.setVisibility(View.VISIBLE);
            tvQuantity.setText(String.valueOf(quantity));
        }
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}