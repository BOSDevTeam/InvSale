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
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
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
    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton fab;
    int tabPosition;
    private onFragmentInteractionListener saleSummaryListener,saleItemListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        saleViewModel =
                new ViewModelProvider(this).get(SaleViewModel.class);

        binding = FragmentSaleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);
        setLayoutResource();

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.sale_summary)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.sale_item)));

        SaleTabAdapter adapter = new SaleTabAdapter(getContext(), getChildFragmentManager(), tabLayout.getTabCount());
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
                Intent i = new Intent(getContext(), SaleActivity.class);
                startActivity(i);
            }
        });

        return root;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if (childFragment instanceof SaleFragment.onFragmentInteractionListener) {
            if (saleSummaryListener == null)
                saleSummaryListener = (SaleFragment.onFragmentInteractionListener) childFragment;
            else saleItemListener = (SaleFragment.onFragmentInteractionListener) childFragment;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_refresh, menu);
        MenuItem itemSearch=menu.findItem(R.id.action_search);
        MenuItem itemRefresh=menu.findItem(R.id.action_refresh);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);

        itemRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (tabPosition == 0) {
                    if (saleSummaryListener != null)
                        saleSummaryListener.refresh();
                } else if (tabPosition == 1) {
                    if (saleItemListener != null)
                        saleItemListener.refresh();
                }
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (tabPosition == 0) {
                    if (saleSummaryListener != null)
                        saleSummaryListener.search(query);
                } else if (tabPosition == 1) {
                    if (saleItemListener != null)
                        saleItemListener.search(query);
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