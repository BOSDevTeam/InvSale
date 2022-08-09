package com.bosictsolution.invsale.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.adapter.GeneralExpandableListAdapter;
import com.bosictsolution.invsale.adapter.ListItemSaleTranAdapter;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.SubMenuData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaleItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaleItemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView rvSaleItem;
    ListItemSaleTranAdapter listItemSaleTranAdapter;
    TextView tvDate,tvCategory,tvFromDate,tvToDate,tvTotal;
    AppSetting appSetting=new AppSetting();
    String selectedDate,fromDate,toDate;
    int clientId,mainMenuId,subMenuId,date_filter_type,date_filter=1,from_to_date_filter=2;
    List<MainMenuData> lstMainMenu=new ArrayList<>();
    List<SubMenuData> lstSubMenu=new ArrayList<>();
    List<String> listDataHeader;
    HashMap<String,List<String>> listDataChild;
    GeneralExpandableListAdapter generalExpandableListAdapter;
    private ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    public static final int SPECIFIC_DATE_REQUEST_CODE = 11; // Used to identify the result
    public static final int FROM_DATE_REQUEST_CODE = 12;
    public static final int TO_DATE_REQUEST_CODE = 13;
    DatabaseAccess db;

    public SaleItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SaleItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaleItemFragment newInstance(String param1, String param2) {
        SaleItemFragment fragment = new SaleItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sale_item, container, false);
        setLayoutResource(root);
        init();
        fillData();

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateFilterDialog();
            }
        });

        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryFilterDialog();
            }
        });

        return root;
    }

    private void setAdapter(List<SaleTranData> list){
        listItemSaleTranAdapter=new ListItemSaleTranAdapter(list,getContext());
        rvSaleItem.setAdapter(listItemSaleTranAdapter);
        rvSaleItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void showCategoryFilterDialog() {
        LayoutInflater reg = LayoutInflater.from(getContext());
        View v = reg.inflate(R.layout.dialog_category_filter, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final ExpandableListView expList = v.findViewById(R.id.list);
        final TextView tvAll = v.findViewById(R.id.tvAll);

        getMainMenu();
        getSubMenuForCategoryFilter();
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
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainMenuId=0;
                subMenuId=0;
                tvCategory.setText(getResources().getString(R.string.all));
                if(date_filter_type==date_filter)getSaleItemByDate();
                else if(date_filter_type==from_to_date_filter)getSaleItemByFromToDate();
                alertDialog.dismiss();
            }
        });
        expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                List<SubMenuData> list=new ArrayList<>();
                mainMenuId = lstMainMenu.get(groupPosition).getMainMenuID();
                String mainMenuName=lstMainMenu.get(groupPosition).getMainMenuName();
                for (int i = 0; i < lstSubMenu.size(); i++) {
                    if (lstSubMenu.get(i).getMainMenuID() == mainMenuId) {
                        list.add(lstSubMenu.get(i));
                    }
                }
                subMenuId = list.get(childPosition).getSubMenuID();
                String subMenuName=list.get(childPosition).getSubMenuName();
                if(subMenuId==0)tvCategory.setText(mainMenuName);
                else tvCategory.setText(subMenuName);
                if(date_filter_type==date_filter)getSaleItemByDate();
                else if(date_filter_type==from_to_date_filter)getSaleItemByFromToDate();
                alertDialog.dismiss();
                return false;
            }
        });
    }

    private void getMainMenu(){
        lstMainMenu=new ArrayList<>();
        lstMainMenu=db.getMainMenu();
    }

    private void getSubMenuForCategoryFilter(){
        lstSubMenu=new ArrayList<>();
        lstSubMenu=db.getSubMenuForCategoryFilter();
    }

    private void setDataToExpList(ExpandableListView expList){
        listDataHeader=new ArrayList<>();
        listDataChild=new HashMap<>();
        for(int i=0;i<lstMainMenu.size();i++){
            int mainMenuID=lstMainMenu.get(i).getMainMenuID();
            String mainMenuName=lstMainMenu.get(i).getMainMenuName();

            List<String> lstSubMenuName=new ArrayList<>();
            for(int j=0;j<lstSubMenu.size();j++){
                if(lstSubMenu.get(j).getMainMenuID()==mainMenuID){
                    lstSubMenuName.add(lstSubMenu.get(j).getSubMenuName());
                }
            }
            if(lstSubMenuName.size()!=0){
                listDataChild.put(mainMenuName, lstSubMenuName);
                listDataHeader.add(mainMenuName);
            }
        }
        generalExpandableListAdapter=new GeneralExpandableListAdapter(getContext(),listDataHeader,listDataChild);
        expList.setAdapter(generalExpandableListAdapter);
    }

    private void showDateFilterDialog() {
        LayoutInflater reg = LayoutInflater.from(getContext());
        View v = reg.inflate(R.layout.dialog_date_filter, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final RadioButton rdoDate = v.findViewById(R.id.rdoDate);
        final RadioButton rdoFromToDate=v.findViewById(R.id.rdoFromToDate);

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        rdoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(SPECIFIC_DATE_REQUEST_CODE);
                alertDialog.dismiss();
            }
        });
        rdoFromToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rdoFromToDate.isChecked()){
                    showFromToDateDialog();
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void showFromToDateDialog() {
        LayoutInflater reg = LayoutInflater.from(getContext());
        View v = reg.inflate(R.layout.dialog_from_to_date, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
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
                getSaleItemByFromToDate();
                alertDialog.dismiss();
            }
        });
        tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(FROM_DATE_REQUEST_CODE);
            }
        });
        tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(TO_DATE_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check for the results
        if (requestCode == SPECIFIC_DATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            tvDate.setText(selectedDate);
            getSaleItemByDate();
        }else if (requestCode == FROM_DATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fromDate = data.getStringExtra("selectedDate");
            tvFromDate.setText(fromDate);
        }else if (requestCode == TO_DATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            toDate = data.getStringExtra("selectedDate");
            tvToDate.setText(toDate);
        }
    }

    private void getSaleItemByDate(){
        date_filter_type=date_filter;
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getSaleItemByDate(selectedDate,clientId,mainMenuId,subMenuId).enqueue(new Callback<List<SaleTranData>>() {
            @Override
            public void onResponse(Call<List<SaleTranData>> call, Response<List<SaleTranData>> response) {
                progressDialog.dismiss();
                List<SaleTranData> list=response.body();
                setAdapter(list);
                tvTotal.setText("MMK"+getContext().getResources().getString(R.string.space)+calculateAmountTotal(list));
            }

            @Override
            public void onFailure(Call<List<SaleTranData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("SaleItemFragment", t.getMessage());
            }
        });
    }

    private void getSaleItemByFromToDate(){
        date_filter_type=from_to_date_filter;
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getSaleItemByFromToDate(fromDate,toDate,clientId,mainMenuId,subMenuId).enqueue(new Callback<List<SaleTranData>>() {
            @Override
            public void onResponse(Call<List<SaleTranData>> call, Response<List<SaleTranData>> response) {
                progressDialog.dismiss();
                List<SaleTranData> list=response.body();
                setAdapter(list);
                tvTotal.setText("MMK"+getContext().getResources().getString(R.string.space)+calculateAmountTotal(list));
            }

            @Override
            public void onFailure(Call<List<SaleTranData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("SaleItemFragment", t.getMessage());
            }
        });
    }

    private void showDatePicker(int requestCode){
        // create the datePickerFragment
        AppCompatDialogFragment newFragment = new DatePickerFragment();
        // set the targetFragment to receive the results, specifying the request code
        newFragment.setTargetFragment(SaleItemFragment.this, requestCode);
        // show the datePicker
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void setLayoutResource(View root){
        rvSaleItem=root.findViewById(R.id.rvSaleItem);
        tvDate=root.findViewById(R.id.tvDate);
        tvCategory=root.findViewById(R.id.tvCategory);
        tvTotal=root.findViewById(R.id.tvTotal);
    }

    private void init(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        db=new DatabaseAccess(getContext());
        sharedpreferences = getContext().getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
    }

    private void fillData(){
        date_filter_type=date_filter;
        selectedDate= appSetting.getTodayDate();
        tvDate.setText(selectedDate);
        clientId=sharedpreferences.getInt(AppConstant.ClientID,0);
        getSaleItemByDate();
    }

    private void refresh(){
        date_filter_type=date_filter;
        selectedDate= appSetting.getTodayDate();
        tvDate.setText(selectedDate);
        mainMenuId=0;
        subMenuId=0;
        tvCategory.setText(getResources().getString(R.string.all));
        getSaleItemByDate();
    }

    private String calculateAmountTotal(List<SaleTranData> list) {
        int amountTotal = 0;
        String result = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            amountTotal = list.stream().mapToInt(x -> x.getTotalAmount()).sum();
        } else {
            for (int i = 0; i < list.size(); i++) {
                amountTotal += list.get(i).getTotalAmount();
            }
        }
        result = appSetting.df.format(amountTotal);
        return result;
    }
}