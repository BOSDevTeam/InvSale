package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.invsale.adapter.ListItemProductAdapter;
import com.bosictsolution.invsale.data.ProductData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    TextView tvSubMenu;
    RecyclerView rvProduct;
    ExtendedFloatingActionButton fab;
    ListItemProductAdapter listItemProductAdapter;
    List<ProductData> lstProduct=new ArrayList<>();
    private Context context=this;
    BottomSheetBehavior sheetBehavior;
    LinearLayout bottom_sheet;
    ImageButton btnBottomSheetClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle("Main Menu 1");

        createProduct();
        setAdapter();

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

    private void createProduct(){
        ProductData data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);

        data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);

        data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);

        data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);

        data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);

        data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);

        data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);

        data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);

        data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);

        data=new ProductData();
        data.setProductName("Product ABC");
        data.setSalePrice(2000);
        lstProduct.add(data);
    }

    private void setAdapter(){
        listItemProductAdapter=new ListItemProductAdapter(lstProduct,context,bottom_sheet);
        rvProduct.setAdapter(listItemProductAdapter);
        rvProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    private void setLayoutResource() {
        tvSubMenu = findViewById(R.id.tvSubMenu);
        rvProduct = findViewById(R.id.rvProduct);
        bottom_sheet=findViewById(R.id.bottom_sheet);
        btnBottomSheetClose=findViewById(R.id.btnBottomSheetClose);
        fab=findViewById(R.id.fab);
    }
}