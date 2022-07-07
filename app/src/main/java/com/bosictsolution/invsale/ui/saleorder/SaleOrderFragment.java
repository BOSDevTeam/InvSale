package com.bosictsolution.invsale.ui.saleorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bosictsolution.invsale.CategoryActivity;
import com.bosictsolution.invsale.adapter.ListItemSaleAdapter;
import com.bosictsolution.invsale.adapter.ListItemSaleOrderAdapter;
import com.bosictsolution.invsale.data.SaleOrderMasterData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.databinding.FragmentSaleOrderBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SaleOrderFragment extends Fragment {

    private SaleOrderViewModel saleOrderViewModel;
    private FragmentSaleOrderBinding binding;
    ListItemSaleOrderAdapter listItemSaleOrderAdapter;
    List<SaleOrderMasterData> lstSaleOrder=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        saleOrderViewModel =
                new ViewModelProvider(this).get(SaleOrderViewModel.class);

        binding = FragmentSaleOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView rvSaleOrder = binding.rvSaleOrder;
        final FloatingActionButton fab=binding.fab;

        setAdapter();
        saleOrderViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), CategoryActivity.class);
                startActivity(i);
            }
        });
        return root;
    }

    private void setAdapter(){
        SaleOrderMasterData data=new SaleOrderMasterData();
        data.setYear("2022");
        data.setDay("27");
        data.setMonth("Jun");
        data.setOrderNumber("SO0001");
        data.setGrandTotal(6600);
        data.setCustomerName("CustomerA");
        lstSaleOrder.add(data);

        data=new SaleOrderMasterData();
        data.setYear("2022");
        data.setDay("26");
        data.setMonth("Jun");
        data.setOrderNumber("SO0001");
        data.setGrandTotal(6600);
        data.setCustomerName("CustomerA");
        lstSaleOrder.add(data);

        data=new SaleOrderMasterData();
        data.setYear("2022");
        data.setDay("26");
        data.setMonth("Jun");
        data.setOrderNumber("SO0001");
        data.setGrandTotal(6600);
        data.setCustomerName("CustomerB");
        lstSaleOrder.add(data);

        listItemSaleOrderAdapter=new ListItemSaleOrderAdapter(lstSaleOrder,getContext());
        binding.rvSaleOrder.setAdapter(listItemSaleOrderAdapter);
        binding.rvSaleOrder.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}