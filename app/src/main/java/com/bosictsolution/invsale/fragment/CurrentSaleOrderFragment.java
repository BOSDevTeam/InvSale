package com.bosictsolution.invsale.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.adapter.ListItemSaleOrderAdapter;
import com.bosictsolution.invsale.data.SaleOrderMasterData;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrentSaleOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentSaleOrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListItemSaleOrderAdapter listItemSaleOrderAdapter;
    List<SaleOrderMasterData> lstSaleOrder=new ArrayList<>();
    RecyclerView rvSaleOrder;

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

        rvSaleOrder=root.findViewById(R.id.rvSaleOrder);

        setAdapter();

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
        rvSaleOrder.setAdapter(listItemSaleOrderAdapter);
        rvSaleOrder.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}