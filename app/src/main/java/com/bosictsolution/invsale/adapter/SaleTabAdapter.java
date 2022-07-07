package com.bosictsolution.invsale.adapter;

import android.content.Context;

import com.bosictsolution.invsale.SaleItemFragment;
import com.bosictsolution.invsale.SaleSummaryFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SaleTabAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public SaleTabAdapter(Context context,@NonNull FragmentManager fm,int totalTabs) {
        super(fm);
        this.context=context;
        this.totalTabs=totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SaleSummaryFragment();
            case 1:
                return new SaleItemFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
