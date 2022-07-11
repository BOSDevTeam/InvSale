package com.bosictsolution.invsale.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.adapter.ListItemSaleSummaryAdapter;
import com.bosictsolution.invsale.data.SaleMasterData;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaleSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaleSummaryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView rvSaleSummary;
    ListItemSaleSummaryAdapter listItemSaleSummaryAdapter;
    List<SaleMasterData> lstSale=new ArrayList<>();

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

        setAdapter();

        return root;
    }

    private void setAdapter(){
        SaleMasterData data=new SaleMasterData();
        data.setDate("08/07/2022");
        data.setSaleNumber("SO0001");
        data.setGrandTotal(6600);
        data.setCustomerName("CustomerA");
        lstSale.add(data);

        data=new SaleMasterData();
        data.setDate("08/07/2022");
        data.setSaleNumber("SO0001");
        data.setGrandTotal(6600);
        data.setCustomerName("CustomerA");
        lstSale.add(data);

        data=new SaleMasterData();
        data.setDate("08/07/2022");
        data.setSaleNumber("SO0001");
        data.setGrandTotal(6600);
        data.setCustomerName("CustomerA");
        lstSale.add(data);

        data=new SaleMasterData();
        data.setDate("08/07/2022");
        data.setSaleNumber("SO0001");
        data.setGrandTotal(6600);
        data.setCustomerName("CustomerA");
        lstSale.add(data);

        listItemSaleSummaryAdapter=new ListItemSaleSummaryAdapter(lstSale,getContext());
        rvSaleSummary.setAdapter(listItemSaleSummaryAdapter);
        rvSaleSummary.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}