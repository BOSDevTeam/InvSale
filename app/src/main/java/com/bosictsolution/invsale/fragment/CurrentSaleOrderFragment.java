package com.bosictsolution.invsale.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.adapter.ListItemSaleOrderAdapter;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.DateFilter;
import com.bosictsolution.invsale.data.SaleOrderMasterData;
import com.bosictsolution.invsale.ui.saleorder.SaleOrderFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrentSaleOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentSaleOrderFragment extends Fragment implements SaleOrderFragment.onFragmentInteractionListener, DateFilter.IDateFilter {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView rvSaleOrder;
    ListItemSaleOrderAdapter listItemSaleOrderAdapter;
    List<SaleOrderMasterData> lstSaleOrder=new ArrayList<>();
    private ProgressDialog progressDialog;
    TextView tvDate,tvFromDate,tvToDate;
    String selectedDate,fromDate,toDate;
    AppSetting appSetting=new AppSetting();
    int clientId;
    SharedPreferences sharedpreferences;
    private SaleOrderFragment.onFragmentInteractionListener listener;
    DateFilter dateFilter=new DateFilter(this);

    public CurrentSaleOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentSaleOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentSaleOrderFragment newInstance(String param1, String param2) {
        CurrentSaleOrderFragment fragment = new CurrentSaleOrderFragment();
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
        View root = inflater.inflate(R.layout.fragment_current_sale_order, container, false);
        setLayoutResource(root);
        init();
        fillData();

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateFilter.showDateFilterDialog(getContext());
            }
        });

        return root;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if(childFragment instanceof SaleOrderFragment.onFragmentInteractionListener){
            listener=(SaleOrderFragment.onFragmentInteractionListener) childFragment;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check for the results
        if (requestCode == AppConstant.SPECIFIC_DATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            tvDate.setText(selectedDate);
            getMasterSaleOrderByDate();
        }else if (requestCode == AppConstant.FROM_DATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fromDate = data.getStringExtra("selectedDate");
            tvFromDate.setText(fromDate);
        }else if (requestCode == AppConstant.TO_DATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            toDate = data.getStringExtra("selectedDate");
            tvToDate.setText(toDate);
        }
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
                getMasterSaleOrderByFromToDate();
                alertDialog.dismiss();
            }
        });
        tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(AppConstant.FROM_DATE_REQUEST_CODE);
            }
        });
        tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(AppConstant.TO_DATE_REQUEST_CODE);
            }
        });
    }

    private void showDatePicker(int requestCode){
        // create the datePickerFragment
        AppCompatDialogFragment newFragment = new DatePickerFragment();
        // set the targetFragment to receive the results, specifying the request code
        newFragment.setTargetFragment(CurrentSaleOrderFragment.this, requestCode);
        // show the datePicker
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void setLayoutResource(View root){
        rvSaleOrder=root.findViewById(R.id.rvSaleOrder);
        tvDate=root.findViewById(R.id.tvDate);
    }

    private void init(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        sharedpreferences = getContext().getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
    }

    private void fillData(){
        selectedDate= appSetting.getTodayDate();
        tvDate.setText(selectedDate);
        clientId=sharedpreferences.getInt(AppConstant.ClientID,0);
        getMasterSaleOrderByDate();
    }

    private void setAdapter(List<SaleOrderMasterData> list){
        listItemSaleOrderAdapter=new ListItemSaleOrderAdapter(list,getContext());
        rvSaleOrder.setAdapter(listItemSaleOrderAdapter);
        rvSaleOrder.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void getMasterSaleOrderByDate(){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getMasterSaleOrderByDate(selectedDate,clientId,false).enqueue(new Callback<List<SaleOrderMasterData>>() {
            @Override
            public void onResponse(Call<List<SaleOrderMasterData>> call, Response<List<SaleOrderMasterData>> response) {
                progressDialog.dismiss();
                lstSaleOrder=response.body();
                setAdapter(lstSaleOrder);
            }

            @Override
            public void onFailure(Call<List<SaleOrderMasterData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMasterSaleOrderByFromToDate(){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getMasterSaleOrderByFromToDate(fromDate,toDate,clientId,false).enqueue(new Callback<List<SaleOrderMasterData>>() {
            @Override
            public void onResponse(Call<List<SaleOrderMasterData>> call, Response<List<SaleOrderMasterData>> response) {
                progressDialog.dismiss();
                lstSaleOrder=response.body();
                setAdapter(lstSaleOrder);
            }

            @Override
            public void onFailure(Call<List<SaleOrderMasterData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMasterSaleOrderByValue(String value){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getMasterSaleOrderByValue(value,clientId,false).enqueue(new Callback<List<SaleOrderMasterData>>() {
            @Override
            public void onResponse(Call<List<SaleOrderMasterData>> call, Response<List<SaleOrderMasterData>> response) {
                progressDialog.dismiss();
                lstSaleOrder=response.body();
                setAdapter(lstSaleOrder);
            }

            @Override
            public void onFailure(Call<List<SaleOrderMasterData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void refresh() {
        selectedDate= appSetting.getTodayDate();
        tvDate.setText(selectedDate);
        getMasterSaleOrderByDate();
    }

    @Override
    public void search(String value) {
        if (value.length() != 0)
            getMasterSaleOrderByValue(value);
    }

    @Override
    public void setOnDateClick() {
        showDatePicker(AppConstant.SPECIFIC_DATE_REQUEST_CODE);
    }

    @Override
    public void setOnFromToDateClick() {
        showFromToDateDialog();
    }
}