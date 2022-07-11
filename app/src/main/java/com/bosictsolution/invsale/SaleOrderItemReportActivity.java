package com.bosictsolution.invsale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bosictsolution.invsale.adapter.ListItemSaleOrderTranAdapter;
import com.bosictsolution.invsale.data.SaleOrderTranData;

import java.util.ArrayList;
import java.util.List;

public class SaleOrderItemReportActivity extends AppCompatActivity {

    RecyclerView rvSaleOrderItem;
    ListItemSaleOrderTranAdapter listItemSaleOrderTranAdapter;
    List<SaleOrderTranData> lstSaleOrder=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_item_report);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.report_sale_order_item));

        setAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setAdapter(){
        SaleOrderTranData data=new SaleOrderTranData();
        data.setProductName("ProductABC");
        data.setQuantity(20);
        lstSaleOrder.add(data);

        data=new SaleOrderTranData();
        data.setProductName("ProductABC");
        data.setQuantity(20);
        lstSaleOrder.add(data);

        data=new SaleOrderTranData();
        data.setProductName("ProductABC");
        data.setQuantity(20);
        lstSaleOrder.add(data);

        data=new SaleOrderTranData();
        data.setProductName("ProductABC");
        data.setQuantity(20);
        lstSaleOrder.add(data);

        listItemSaleOrderTranAdapter=new ListItemSaleOrderTranAdapter(lstSaleOrder,this);
        rvSaleOrderItem.setAdapter(listItemSaleOrderTranAdapter);
        rvSaleOrderItem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void setLayoutResource(){
        rvSaleOrderItem=findViewById(R.id.rvSaleOrderItem);
    }
}