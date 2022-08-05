package com.bosictsolution.invsale.ui.sale;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.SaleActivity;
import com.bosictsolution.invsale.adapter.SaleTabAdapter;
import com.bosictsolution.invsale.databinding.FragmentSaleBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class SaleFragment extends Fragment {

    private SaleViewModel saleViewModel;
    private FragmentSaleBinding binding;
    //FragmentManager fm;

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

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.sale_summary)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.sale_item)));

        //fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager();

        SaleTabAdapter adapter=new SaleTabAdapter(getContext(),getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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

        return root;
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