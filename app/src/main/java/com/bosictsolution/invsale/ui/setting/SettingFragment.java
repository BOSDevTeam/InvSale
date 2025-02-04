package com.bosictsolution.invsale.ui.setting;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.invsale.BTPrinterSettingActivity;
import com.bosictsolution.invsale.LocationSettingActivity;
import com.bosictsolution.invsale.NotificationSettingActivity;
import com.bosictsolution.invsale.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    private SettingViewModel mViewModel;
    private FragmentSettingBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView tvBTPrinterSetting = binding.tvBTPrinterSetting;
        final TextView tvNotificationSetting = binding.tvNotificationSetting;
        final TextView tvLocationSetting = binding.tvLocationSetting;

        tvBTPrinterSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), BTPrinterSettingActivity.class);
                startActivity(i);
            }
        });
        tvNotificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), NotificationSettingActivity.class);
                startActivity(i);
            }
        });
        tvLocationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), LocationSettingActivity.class);
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

}