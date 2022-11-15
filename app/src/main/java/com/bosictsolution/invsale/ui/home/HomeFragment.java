package com.bosictsolution.invsale.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bosictsolution.invsale.CategoryActivity;
import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.SummaryData;
import com.bosictsolution.invsale.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    ProgressBar progressBar;
    LinearLayout layoutCurrentOrder,layoutTotalOrder;
    TextView tvBadge,tvTotalProduct,tvCurrentSaleOrder,tvTotalOrder;
    Button btnCategory;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    SharedPreferences sharedpreferences;
    int clientId;
    AppSetting appSetting=new AppSetting();
    DatabaseAccess db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);
        setLayoutResource();
        init();
        fillData();

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), CategoryActivity.class);
                startActivity(i);
            }
        });
        layoutCurrentOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_sale_order);
            }
        });
        layoutTotalOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_sale_order);
            }
        });

        return root;
    }

    private void init() {
        db=new DatabaseAccess(getContext());
        sharedpreferences = getContext().getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
    }

    private void fillData(){
        clientId=sharedpreferences.getInt(AppConstant.CLIENT_ID,0);
    }

    private void getSummaryData(int clientId,String date){
        Api.getClient().getSummaryData(clientId,date).enqueue(new Callback<SummaryData>() {
            @Override
            public void onResponse(Call<SummaryData> call, Response<SummaryData> response) {
                progressBar.setVisibility(View.GONE);
                if(response.body()==null)return;
                SummaryData data= response.body();
                if(data != null){
                    if(data.getNotiCount()!=0){
                        tvBadge.setVisibility(View.VISIBLE);
                        tvBadge.setText(String.valueOf(data.getNotiCount()));
                    }
                    tvTotalProduct.setText(String.valueOf(data.getTotalProduct()));
                    tvCurrentSaleOrder.setText(String.valueOf(data.getCurrentOrder()));
                    tvTotalOrder.setText(String.valueOf(data.getTotalOrder()));
                }
            }

            @Override
            public void onFailure(Call<SummaryData> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        MenuItem itemProfile=menu.findItem(R.id.action_profile);
        MenuItem itemNotification=menu.findItem(R.id.action_notification);
        View notificationView = itemNotification.getActionView();
        tvBadge = notificationView.findViewById(R.id.tvBadge);

        getSummaryData(clientId,appSetting.getTodayDate());

        itemProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_profile);
                return false;
            }
        });

        notificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_notification);
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setLayoutResource(){
        layoutCurrentOrder = binding.layoutCurrentOrder;
        layoutTotalOrder = binding.layoutTotalOrder;
        tvTotalProduct = binding.tvTotalProduct;
        tvCurrentSaleOrder = binding.tvCurrentSaleOrder;
        tvTotalOrder = binding.tvTotalOrder;
        btnCategory = binding.btnCategory;
        progressBar = binding.progressBar;
    }
}