package com.bosictsolution.invsale.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.adapter.ListItemSaleTranAdapter;
import com.bosictsolution.invsale.data.SaleTranData;

import java.util.ArrayList;
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
    List<SaleTranData> lstSale=new ArrayList<>();

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

        rvSaleItem=root.findViewById(R.id.rvSaleItem);

        setAdapter();

        return root;
    }

    private void setAdapter(){
        SaleTranData data=new SaleTranData();
        data.setProductName("ProductABC");
        data.setQuantity(20);
        data.setTotalAmount(6600);
        lstSale.add(data);

        data=new SaleTranData();
        data.setProductName("ProductABC");
        data.setQuantity(20);
        data.setTotalAmount(6600);
        lstSale.add(data);

        data=new SaleTranData();
        data.setProductName("ProductABC");
        data.setQuantity(20);
        data.setTotalAmount(6600);
        lstSale.add(data);

        data=new SaleTranData();
        data.setProductName("ProductABC");
        data.setQuantity(20);
        data.setTotalAmount(6600);
        lstSale.add(data);

        listItemSaleTranAdapter=new ListItemSaleTranAdapter(lstSale,getContext());
        rvSaleItem.setAdapter(listItemSaleTranAdapter);
        rvSaleItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}