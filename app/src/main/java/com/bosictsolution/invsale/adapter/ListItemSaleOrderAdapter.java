package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.SaleOrderDetailActivity;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.SaleOrderMasterData;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemSaleOrderAdapter extends RecyclerView.Adapter<ListItemSaleOrderAdapter.ViewHolder> {
    private Context context;
    List<SaleOrderMasterData> list;
    AppSetting appSetting=new AppSetting();

    public ListItemSaleOrderAdapter(List<SaleOrderMasterData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sale_order, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvYear.setText(list.get(position).getYear());
        holder.tvDay.setText(list.get(position).getDay());
        holder.tvMonth.setText(list.get(position).getMonth());
        holder.tvCustomer.setText(list.get(position).getCustomerName());
        holder.tvOrderNumber.setText(context.getResources().getString(R.string.space)+context.getResources().getString(R.string.hash)+list.get(position).getOrderNumber());
        holder.tvGrandTotal.setText("MMK"+context.getResources().getString(R.string.space)+appSetting.df.format(list.get(position).getGrandTotal()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, SaleOrderDetailActivity.class);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvYear,tvDay,tvMonth,tvOrderNumber,tvGrandTotal,tvCustomer;

        public ViewHolder(View itemView) {
            super(itemView);
            tvYear =itemView.findViewById(R.id.tvYear);
            tvDay =  itemView.findViewById(R.id.tvDay);
            tvMonth =  itemView.findViewById(R.id.tvMonth);
            tvOrderNumber =  itemView.findViewById(R.id.tvOrderNumber);
            tvGrandTotal =  itemView.findViewById(R.id.tvGrandTotal);
            tvCustomer =  itemView.findViewById(R.id.tvCustomer);
        }
    }
}
