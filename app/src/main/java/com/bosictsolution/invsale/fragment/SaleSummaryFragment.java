package com.bosictsolution.invsale.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.adapter.ListItemSaleSummaryAdapter;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.ui.sale.SaleFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaleSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaleSummaryFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView rvSaleSummary;
    ListItemSaleSummaryAdapter listItemSaleSummaryAdapter;
    private ProgressDialog progressDialog;
    TextView tvDate;
    String selectedDate;
    AppSetting appSetting=new AppSetting();
    public static final int REQUEST_CODE = 11; // Used to identify the result
    FragmentManager fm;

    public SaleSummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SaleSummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaleSummaryFragment newInstance(String param1, String param2) {
        SaleSummaryFragment fragment = new SaleSummaryFragment();
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
        View root = inflater.inflate(R.layout.fragment_sale_summary, container, false);

        rvSaleSummary=root.findViewById(R.id.rvSaleSummary);
        tvDate=root.findViewById(R.id.tvDate);
        selectedDate= appSetting.getTodayDate();
        tvDate.setText(selectedDate);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager();

        getMasterSale();

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateFilterDialog();
            }
        });

        return root;
    }

    public void getMasterSale() {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getMasterSaleByDate(selectedDate).enqueue(new Callback<List<SaleMasterData>>() {
            @Override
            public void onResponse(Call<List<SaleMasterData>> call, Response<List<SaleMasterData>> response) {
                progressDialog.dismiss();
                List<SaleMasterData> list=response.body();
                setAdapter(list);
            }

            @Override
            public void onFailure(Call<List<SaleMasterData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("SaleSummaryFragment", t.getMessage());
            }
        });
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
                // create the datePickerFragment
                AppCompatDialogFragment newFragment = new DatePickerFragment();
                // set the targetFragment to receive the results, specifying the request code
                newFragment.setTargetFragment(SaleSummaryFragment.this, REQUEST_CODE);
                // show the datePicker
                newFragment.show(fm, "datePicker");
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
        final TextView tvFromDate = v.findViewById(R.id.tvFromDate);
        final TextView tvToDate=v.findViewById(R.id.tvToDate);
        final Button btnOK = v.findViewById(R.id.btnOK);
        final Button btnCancel = v.findViewById(R.id.btnCancel);

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
                alertDialog.dismiss();
            }
        });
        tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show calendar
            }
        });
        tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show calendar
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check for the results
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            tvDate.setText(selectedDate);
            getMasterSale();
        }
    }

    private void setAdapter(List<SaleMasterData> list){
        listItemSaleSummaryAdapter=new ListItemSaleSummaryAdapter(list,getContext());
        rvSaleSummary.setAdapter(listItemSaleSummaryAdapter);
        rvSaleSummary.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}