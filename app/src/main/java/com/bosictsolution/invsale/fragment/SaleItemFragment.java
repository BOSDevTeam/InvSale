package com.bosictsolution.invsale.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.bosictsolution.invsale.common.AppSetting;
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
    List<SaleTranData> lstSale=new ArrayList<>();
    TextView tvDate,tvCategory;
    public static final int REQUEST_CODE = 11; // Used to identify the result
    FragmentManager fm;
    AppSetting appSetting=new AppSetting();
    String selectedDate;
    List<MainMenuData> lstMainMenu=new ArrayList<>();
    List<SubMenuData> lstSubMenu=new ArrayList<>();
    List<String> listDataHeader;
    HashMap<String,List<String>> listDataChild;
    GeneralExpandableListAdapter generalExpandableListAdapter;

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
        tvDate=root.findViewById(R.id.tvDate);
        selectedDate= appSetting.getTodayDate();
        tvDate.setText(selectedDate);
        tvCategory=root.findViewById(R.id.tvCategory);

        fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager();

        setAdapter();

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

    private void showCategoryFilterDialog() {
        LayoutInflater reg = LayoutInflater.from(getContext());
        View v = reg.inflate(R.layout.dialog_category_filter, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final ExpandableListView expList = v.findViewById(R.id.list);

        createMainMenu();
        createSubMenu();
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
    }

    private void createMainMenu(){
        lstMainMenu=new ArrayList<>();

        MainMenuData data=new MainMenuData();
        data.setMainMenuID(0);
        data.setMainMenuName("All");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(1);
        data.setMainMenuName("Main Menu 1");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(2);
        data.setMainMenuName("Main Menu 2");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(3);
        data.setMainMenuName("Main Menu 3");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(4);
        data.setMainMenuName("Main Menu 4");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(5);
        data.setMainMenuName("Main Menu 5");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(6);
        data.setMainMenuName("Main Menu 6");
        lstMainMenu.add(data);
    }

    private void createSubMenu(){
        lstSubMenu=new ArrayList<>();

        SubMenuData data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(0);
        data.setSubMenuName("");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("All");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 1/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 2/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 3/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 4/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 5/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 6/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(2);
        data.setSubMenuName("All");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(2);
        data.setSubMenuName("Sub Menu 1/2");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(3);
        data.setSubMenuName("All");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(3);
        data.setSubMenuName("Sub Menu 1/3");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(4);
        data.setSubMenuName("All");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(4);
        data.setSubMenuName("Sub Menu 1/4");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(5);
        data.setSubMenuName("All");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(5);
        data.setSubMenuName("Sub Menu 1/5");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(6);
        data.setSubMenuName("All");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(6);
        data.setSubMenuName("Sub Menu 1/6");
        lstSubMenu.add(data);
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
                // create the datePickerFragment
                AppCompatDialogFragment newFragment = new DatePickerFragment();
                // set the targetFragment to receive the results, specifying the request code
                newFragment.setTargetFragment(SaleItemFragment.this, REQUEST_CODE);
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
        }
    }
}