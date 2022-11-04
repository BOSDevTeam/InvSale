package com.bosictsolution.invsale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.adapter.ListItemSaleOrderTranAdapter;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.CategoryFilter;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.common.DateFilter;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.SaleOrderTranData;
import com.bosictsolution.invsale.data.SubMenuData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SaleOrderItemReportActivity extends AppCompatActivity implements CategoryFilter.ICategoryFilter, DateFilter.IDateFilter {

    RecyclerView rvSaleOrderItem;
    TextView tvDate,tvCategory,tvFromDate,tvToDate;
    ListItemSaleOrderTranAdapter listItemSaleOrderTranAdapter;
    private ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    DatabaseAccess db;
    private Context context=this;
    AppSetting appSetting=new AppSetting();
    String selectedDate,fromDate,toDate;
    int clientId,mainMenuId,subMenuId,date_filter_type,date_filter=1,from_to_date_filter=2,date_dialog_code;
    List<MainMenuData> lstMainMenu=new ArrayList<>();
    List<SubMenuData> lstSubMenu=new ArrayList<>();
    private final int DATE_PICKER_DIALOG=1;
    ConnectionLiveData connectionLiveData;
    CategoryFilter categoryFilter=new CategoryFilter(this);
    DateFilter dateFilter=new DateFilter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_order_item_report);
        setLayoutResource();
        init();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.report_sale_order_item));

        checkConnection();
        fillData();

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateFilter.showDateFilterDialog(context);
            }
        });

        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMainMenu();
                getSubMenuForCategoryFilter();
                categoryFilter.showCategoryFilterDialog(context,lstMainMenu,lstSubMenu);
            }
        });
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
        inflater.inflate(R.menu.search_refresh, menu);
        MenuItem itemSearch = menu.findItem(R.id.action_search);
        MenuItem itemRefresh=menu.findItem(R.id.action_refresh);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);

        itemRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                refresh();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        itemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
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

    private void refresh(){
        date_filter_type=date_filter;
        selectedDate= appSetting.getTodayDate();
        tvDate.setText(selectedDate);
        mainMenuId=0;
        subMenuId=0;
        tvCategory.setText(getResources().getString(R.string.all));
        getSaleOrderItemByDate();
    }

    private void search(String value){
        if (value.length() != 0)
            getSaleOrderItemByValue(value);
    }

    private void setAdapter(List<SaleOrderTranData> list){
        listItemSaleOrderTranAdapter=new ListItemSaleOrderTranAdapter(list,this);
        rvSaleOrderItem.setAdapter(listItemSaleOrderTranAdapter);
        rvSaleOrderItem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void getSaleOrderItemByDate(){
        date_filter_type=date_filter;
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getSaleOrderItemByDate(selectedDate,clientId,mainMenuId,subMenuId).enqueue(new Callback<List<SaleOrderTranData>>() {
            @Override
            public void onResponse(Call<List<SaleOrderTranData>> call, Response<List<SaleOrderTranData>> response) {
                progressDialog.dismiss();
                List<SaleOrderTranData> list=response.body();
                setAdapter(list);
            }

            @Override
            public void onFailure(Call<List<SaleOrderTranData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getSaleOrderItemByFromToDate(){
        date_filter_type=from_to_date_filter;
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getSaleOrderItemByFromToDate(fromDate,toDate,clientId,mainMenuId,subMenuId).enqueue(new Callback<List<SaleOrderTranData>>() {
            @Override
            public void onResponse(Call<List<SaleOrderTranData>> call, Response<List<SaleOrderTranData>> response) {
                progressDialog.dismiss();
                List<SaleOrderTranData> list=response.body();
                setAdapter(list);
            }

            @Override
            public void onFailure(Call<List<SaleOrderTranData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getSaleOrderItemByValue(String value){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getSaleOrderItemByValue(value,clientId).enqueue(new Callback<List<SaleOrderTranData>>() {
            @Override
            public void onResponse(Call<List<SaleOrderTranData>> call, Response<List<SaleOrderTranData>> response) {
                progressDialog.dismiss();
                List<SaleOrderTranData> list=response.body();
                setAdapter(list);
            }

            @Override
            public void onFailure(Call<List<SaleOrderTranData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init(){
        connectionLiveData = new ConnectionLiveData(context);
        progressDialog = new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
        db=new DatabaseAccess(context);
        sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
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
        date_filter_type=date_filter;
        selectedDate= appSetting.getTodayDate();
        tvDate.setText(selectedDate);
        clientId=sharedpreferences.getInt(AppConstant.ClientID,0);
        getSaleOrderItemByDate();
    }

    private void getMainMenu(){
        lstMainMenu=new ArrayList<>();
        lstMainMenu=db.getMainMenu();
    }

    private void getSubMenuForCategoryFilter(){
        lstSubMenu=new ArrayList<>();
        lstSubMenu=db.getSubMenuForCategoryFilter();
    }

    private void showFromToDateDialog() {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_from_to_date, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        tvFromDate = v.findViewById(R.id.tvFromDate);
        tvToDate=v.findViewById(R.id.tvToDate);
        final Button btnOK = v.findViewById(R.id.btnOK);
        final Button btnCancel = v.findViewById(R.id.btnCancel);

        selectedDate= appSetting.getTodayDate();
        fromDate=selectedDate;
        toDate= selectedDate;
        tvFromDate.setText(fromDate);
        tvToDate.setText(toDate);

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
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
                tvDate.setText(fromDate+"-"+toDate);
                getSaleOrderItemByFromToDate();
                alertDialog.dismiss();
            }
        });
        tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_dialog_code=AppConstant.FROM_DATE_REQUEST_CODE;
                showDialog(DATE_PICKER_DIALOG);
            }
        });
        tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_dialog_code=AppConstant.TO_DATE_REQUEST_CODE;
                showDialog(DATE_PICKER_DIALOG);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id){
        switch(id){
            case DATE_PICKER_DIALOG:
                return showDatePicker();
        }
        return super.onCreateDialog(id);
    }

    private DatePickerDialog showDatePicker() {
        final Calendar cCalendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(SaleOrderItemReportActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cCalendar.set(Calendar.YEAR, year);
                cCalendar.set(Calendar.MONTH, monthOfYear);
                cCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstant.DATE_FORMAT, Locale.US);
                if (date_dialog_code == AppConstant.SPECIFIC_DATE_REQUEST_CODE) {
                    selectedDate = dateFormat.format(cCalendar.getTime());
                    tvDate.setText(selectedDate);
                    getSaleOrderItemByDate();
                } else if (date_dialog_code == AppConstant.FROM_DATE_REQUEST_CODE) {
                    fromDate = dateFormat.format(cCalendar.getTime());
                    tvFromDate.setText(fromDate);
                } else if (date_dialog_code == AppConstant.TO_DATE_REQUEST_CODE) {
                    toDate = dateFormat.format(cCalendar.getTime());
                    tvToDate.setText(toDate);
                }
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        return datePicker;
    }

    private void setLayoutResource(){
        rvSaleOrderItem=findViewById(R.id.rvSaleOrderItem);
        tvDate=findViewById(R.id.tvDate);
        tvCategory=findViewById(R.id.tvCategory);
    }

    @Override
    public void setOnAllClick() {
        mainMenuId=0;
        subMenuId=0;
        tvCategory.setText(getResources().getString(R.string.all));
        if(date_filter_type==date_filter)getSaleOrderItemByDate();
        else if(date_filter_type==from_to_date_filter)getSaleOrderItemByFromToDate();
    }

    @Override
    public void setOnChildMenuClick(int groupPosition, int childPosition) {
        List<SubMenuData> list=new ArrayList<>();
        String subMenuName="";
        mainMenuId = lstMainMenu.get(groupPosition).getMainMenuID();
        String mainMenuName=lstMainMenu.get(groupPosition).getMainMenuName();
        for (int i = 0; i < lstSubMenu.size(); i++) {
            if (lstSubMenu.get(i).getMainMenuID() == mainMenuId) {
                list.add(lstSubMenu.get(i));
            }
        }
        if(list.size()!=0) {
            subMenuId = list.get(childPosition).getSubMenuID();
            subMenuName = list.get(childPosition).getSubMenuName();
        }
        if(subMenuId==0)tvCategory.setText(mainMenuName);
        else tvCategory.setText(subMenuName);
        if(date_filter_type==date_filter)getSaleOrderItemByDate();
        else if(date_filter_type==from_to_date_filter)getSaleOrderItemByFromToDate();
    }

    @Override
    public void setOnDateClick() {
        date_dialog_code=AppConstant.SPECIFIC_DATE_REQUEST_CODE;
        showDialog(DATE_PICKER_DIALOG);
    }

    @Override
    public void setOnFromToDateClick() {
        showFromToDateDialog();
    }
}