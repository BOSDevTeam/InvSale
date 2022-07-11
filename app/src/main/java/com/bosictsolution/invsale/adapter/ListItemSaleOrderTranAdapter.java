package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.SaleOrderTranData;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemSaleOrderTranAdapter extends RecyclerView.Adapter<ListItemSaleOrderTranAdapter.ViewHolder> {
    private Context context;
    List<SaleOrderTranData> list;
    AppSetting appSetting=new AppSetting();

    public ListItemSaleOrderTranAdapter(List<SaleOrderTranData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_order_tran, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvProductName.setText(list.get(position).getProductName());
        holder.tvQuantity.setText(String.valueOf(list.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,tvQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName =  itemView.findViewById(R.id.tvProductName);
            tvQuantity =  itemView.findViewById(R.id.tvQuantity);
        }
    }
}
