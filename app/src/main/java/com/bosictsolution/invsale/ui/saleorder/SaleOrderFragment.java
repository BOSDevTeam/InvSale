package com.bosictsolution.invsale.ui.saleorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bosictsolution.invsale.CategoryActivity;
import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.adapter.SaleOrderTabAdapter;
import com.bosictsolution.invsale.databinding.FragmentSaleOrderBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class SaleOrderFragment extends Fragment {

    private SaleOrderViewModel saleOrderViewModel;
    private FragmentSaleOrderBinding binding;
    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton fab;
    int tabPosition;
    private SaleOrderFragment.onFragmentInteractionListener currentOrderListener,orderHistoryListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        saleOrderViewModel =
                new ViewModelProvider(this).get(SaleOrderViewModel.class);

        binding = FragmentSaleOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);
        setLayoutResource();

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.current_order)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.order_history)));

        SaleOrderTabAdapter adapter=new SaleOrderTabAdapter(getContext(),getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabPosition=tab.getPosition();
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
                Intent i=new Intent(getContext(), CategoryActivity.class);
                startActivity(i);
            }
        });
        return root;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if (childFragment instanceof SaleOrderFragment.onFragmentInteractionListener) {
            if (currentOrderListener == null)
                currentOrderListener = (SaleOrderFragment.onFragmentInteractionListener) childFragment;
            else orderHistoryListener = (SaleOrderFragment.onFragmentInteractionListener) childFragment;
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
        MenuItem itemSearch=menu.findItem(R.id.action_search);
        MenuItem itemRefresh=menu.findItem(R.id.action_refresh);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);

        itemRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (tabPosition == 0) {
                    if (currentOrderListener != null)
                        currentOrderListener.refresh();
                } else if (tabPosition == 1) {
                    if (orderHistoryListener != null)
                        orderHistoryListener.refresh();
                }
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (tabPosition == 0) {
                    if (currentOrderListener != null)
                        currentOrderListener.search(query);
                } else if (tabPosition == 1) {
                    if (orderHistoryListener != null)
                        orderHistoryListener.search(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        itemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
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

    private void setLayoutResource() {
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;
        fab = binding.fab;
    }

    public interface onFragmentInteractionListener{
        void refresh();
        void search(String value);
    }
}