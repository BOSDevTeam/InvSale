package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.SaleTranData;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemSaleTranAdapter extends RecyclerView.Adapter<ListItemSaleTranAdapter.ViewHolder> {
    private Context context;
    List<SaleTranData> list;
    AppSetting appSetting=new AppSetting();

    public ListItemSaleTranAdapter(List<SaleTranData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_tran, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvProductName.setText(list.get(position).getProductName());
        holder.tvQuantity.setText(String.valueOf(list.get(position).getQuantity()));
        holder.tvAmount.setText(appSetting.df.format(list.get(position).getTotalAmount()));
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,tvQuantity,tvAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName =  itemView.findViewById(R.id.tvProductName);
            tvQuantity =  itemView.findViewById(R.id.tvQuantity);
            tvAmount =  itemView.findViewById(R.id.tvAmount);
        }
    }
}
