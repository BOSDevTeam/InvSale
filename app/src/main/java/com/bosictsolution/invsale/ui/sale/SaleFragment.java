package com.bosictsolution.invsale.ui.sale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bosictsolution.invsale.adapter.ExpandableListAdapter;
import com.bosictsolution.invsale.adapter.GeneralExpandableListAdapter;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.SubMenuData;
import com.bosictsolution.invsale.fragment.DatePickerFragment;
import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.SaleActivity;
import com.bosictsolution.invsale.adapter.SaleTabAdapter;
import com.bosictsolution.invsale.databinding.FragmentSaleBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaleFragment extends Fragment {

    TextView tvDate,tvCategory;
    LinearLayout layoutCategory;
    private SaleViewModel saleViewModel;
    private FragmentSaleBinding binding;
    public static final int REQUEST_CODE = 11; // Used to identify the result
    String selectedDate;
    FragmentManager fm;
    List<MainMenuData> lstMainMenu=new ArrayList<>();
    List<SubMenuData> lstSubMenu=new ArrayList<>();
    List<String> listDataHeader;
    HashMap<String,List<String>> listDataChild;
    GeneralExpandableListAdapter generalExpandableListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        saleViewModel =
                new ViewModelProvider(this).get(SaleViewModel.class);

        binding = FragmentSaleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);

        final TabLayout tabLayout = binding.tabLayout;
        final ViewPager viewPager=binding.viewPager;
        final FloatingActionButton fab=binding.fab;
        tvDate=binding.tvDate;
        tvCategory=binding.tvCategory;
        layoutCategory=binding.layoutCategory;
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.sale_summary)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.sale_item)));

        fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager();

        SaleTabAdapter adapter=new SaleTabAdapter(getContext(),getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0)layoutCategory.setVisibility(View.GONE);
                else if(tab.getPosition()==1)layoutCategory.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), SaleActivity.class);
                startActivity(i);
            }
        });

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
                newFragment.setTargetFragment(SaleFragment.this, REQUEST_CODE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem item=menu.findItem(R.id.action_search);
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

        super.onCreateOptionsMenu(menu, inflater);
    }
}