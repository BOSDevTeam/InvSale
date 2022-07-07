package com.bosictsolution.invsale.ui.sale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bosictsolution.invsale.DatePickerFragment;
import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.SaleActivity;
import com.bosictsolution.invsale.adapter.SaleTabAdapter;
import com.bosictsolution.invsale.databinding.FragmentSaleBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class SaleFragment extends Fragment {

    TextView tvDate;
    private SaleViewModel saleViewModel;
    private FragmentSaleBinding binding;
    public static final int REQUEST_CODE = 11; // Used to identify the result
    String selectedDate;
    FragmentManager fm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        saleViewModel =
                new ViewModelProvider(this).get(SaleViewModel.class);

        binding = FragmentSaleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TabLayout tabLayout = binding.tabLayout;
        final ViewPager viewPager=binding.viewPager;
        final FloatingActionButton fab=binding.fab;
        tvDate=binding.tvDate;
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

        return root;
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
}