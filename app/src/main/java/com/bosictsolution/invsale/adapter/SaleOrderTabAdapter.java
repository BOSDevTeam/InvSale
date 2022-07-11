package com.bosictsolution.invsale.adapter;

import android.content.Context;

import com.bosictsolution.invsale.fragment.CurrentSaleOrderFragment;
import com.bosictsolution.invsale.fragment.SaleOrderHistoryFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SaleOrderTabAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public SaleOrderTabAdapter(Context context, @NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        this.context=context;
        this.totalTabs=totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CurrentSaleOrderFragment();
            case 1:
                return new SaleOrderHistoryFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
