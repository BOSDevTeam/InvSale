package com.bosictsolution.invsale.ui.notification;

import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.MainActivity;
import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.SaleOrderDetailActivity;
import com.bosictsolution.invsale.adapter.ListItemNotificationAdapter;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.NotificationData;
import com.bosictsolution.invsale.data.ProductData;
import com.bosictsolution.invsale.databinding.FragmentNotificationBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements ListItemNotificationAdapter.INotification {

    RecyclerView rvNotification;
    Button btnMarkAllRead;
    private NotificationViewModel mViewModel;
    private FragmentNotificationBinding binding;
    SharedPreferences sharedpreferences;
    private ProgressDialog progressDialog;
    ListItemNotificationAdapter listItemNotificationAdapter;
    AppSetting appSetting=new AppSetting();
    DatabaseAccess db;
    int clientId;
    List<NotificationData> lstNotification=new ArrayList<>();

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(NotificationViewModel.class);
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);
        setLayoutResource();
        db=new DatabaseAccess(getContext());
        sharedpreferences = getContext().getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        clientId=sharedpreferences.getInt(AppConstant.CLIENT_ID, 0);
        getClientNotification(clientId);

        btnMarkAllRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllClientNotification(clientId);
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh, menu);
        MenuItem itemRefresh=menu.findItem(R.id.action_refresh);
        itemRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                getClientNotification(clientId);
                return false;
            }
        });
    }

    private void getClientNotification(int clientId){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getClientNotification(clientId,false).enqueue(new Callback<List<NotificationData>>() {
            @Override
            public void onResponse(Call<List<NotificationData>> call, Response<List<NotificationData>> response) {
                progressDialog.dismiss();
                if (response.body() == null) return;
                lstNotification=response.body();
                setAdapter(lstNotification);
                if(lstNotification != null) MainActivity.setNotificationBadge(lstNotification.size(),getContext());
            }

            @Override
            public void onFailure(Call<List<NotificationData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAdapter(List<NotificationData> list){
        listItemNotificationAdapter=new ListItemNotificationAdapter(getContext(),list);
        rvNotification.setAdapter(listItemNotificationAdapter);
        rvNotification.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listItemNotificationAdapter.setListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showNewProductDialog(ProductData data) {
        LayoutInflater reg = LayoutInflater.from(getContext());
        View v = reg.inflate(R.layout.dialog_product_detail, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final ImageView imgPhoto = v.findViewById(R.id.imgPhoto);
        final TextView tvProductName = v.findViewById(R.id.tvProductName);
        final TextView tvPrice = v.findViewById(R.id.tvPrice);
        final TextView tvDescription = v.findViewById(R.id.tvDescription);

        if (data.getPhotoUrl() != null && data.getPhotoUrl().length() != 0) {
            Picasso.with(getContext()).load(data.getPhotoUrl()).into(imgPhoto);
            imgPhoto.setVisibility(View.VISIBLE);
        }
        else imgPhoto.setVisibility(View.GONE);

        tvProductName.setText(data.getProductName());
        tvPrice.setText(db.getHomeCurrency() + getContext().getResources().getString(R.string.space) + appSetting.df.format(data.getSalePrice()));

        if (data.getDescription() != null && data.getDescription().length() != 0) {
            tvDescription.setText(data.getDescription());
            tvDescription.setVisibility(View.VISIBLE);
        } else tvDescription.setVisibility(View.GONE);

        dialog.setCancelable(true);
        android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void setLayoutResource(){
        rvNotification = binding.rvNotification;
        btnMarkAllRead = binding.btnMarkAllRead;

        progressDialog =new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    @Override
    public void setOnNewProductNotiClick(int productId) {
        getProduct(productId);
    }

    @Override
    public void setOnUpdateOrderNotiClick(int saleOrderId) {
        Intent i=new Intent(getContext(), SaleOrderDetailActivity.class);
        i.putExtra("SaleOrderID",saleOrderId);
        i.putExtra("IsFromNotification",true);
        getContext().startActivity(i);
        deleteClientNotification(clientId,saleOrderId,AppConstant.NOTI_UPDATE_ORDER);
    }

    private void deleteClientNotification(int clientId,int notiId,short notiType){
        Api.getClient().deleteClientNotification(clientId,notiId,notiType).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void deleteAllClientNotification(int clientId){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().deleteAllClientNotification(clientId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                lstNotification=new ArrayList<>();
                setAdapter(lstNotification);
                MainActivity.setNotificationBadge(0,getContext());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getProduct(int productId){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getProduct(productId).enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, Response<ProductData> response) {
                progressDialog.dismiss();
                if (response.body() == null) return;
                ProductData data=response.body();
                showNewProductDialog(data);
                deleteClientNotification(clientId,productId,AppConstant.NOTI_NEW_PRODUCT);
            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}