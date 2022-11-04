package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.listener.ListItemSaleListener;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemSaleAdapter extends RecyclerView.Adapter<ListItemSaleAdapter.ViewHolder> {
    private Context context;
    List<SaleTranData> list;
    ListItemSaleListener listItemSaleListener;
    AppSetting appSetting=new AppSetting();
    boolean isAllowEdit;

    public ListItemSaleAdapter(Context context, List<SaleTranData> list, boolean isAllowEdit) {
        this.list = list;
        this.context = context;
        this.isAllowEdit=isAllowEdit;
    }

    public void setOnListener(ListItemSaleListener listItemSaleListener){
        this.listItemSaleListener=listItemSaleListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvProductName.setText(list.get(position).getProductName());
        holder.tvPrice.setText(appSetting.df.format(list.get(position).getSalePrice()));
        holder.tvQuantity.setText(String.valueOf(list.get(position).getQuantity()));
        holder.tvNumber.setText(String.valueOf(list.get(position).getNumber()));
        holder.tvAmount.setText(appSetting.df.format(list.get(position).getAmount()));

        if(isAllowEdit){
            holder.tvQuantity.setBackground(context.getResources().getDrawable(R.drawable.bd_quantity));
            holder.btnRemove.setVisibility(View.VISIBLE);
        }else{
            holder.tvQuantity.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            holder.btnRemove.setVisibility(View.GONE);
        }

        holder.tvQuantity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(listItemSaleListener !=null){
                    listItemSaleListener.onQuantityClickListener(position, holder.tvQuantity,holder.tvAmount);
                }
            }
        });
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(listItemSaleListener !=null){
                    listItemSaleListener.onRemoveClickListener(position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listItemSaleListener != null) {
                    listItemSaleListener.onItemLongClickListener(position, holder.tvPrice, holder.tvAmount);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber,tvProductName,tvPrice,tvQuantity,tvAmount;
        ImageButton btnRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNumber =itemView.findViewById(R.id.tvNumber);
            tvProductName =itemView.findViewById(R.id.tvProductName);
            tvPrice =  itemView.findViewById(R.id.tvPrice);
            tvQuantity =  itemView.findViewById(R.id.tvQuantity);
            tvAmount =  itemView.findViewById(R.id.tvAmount);
            btnRemove =  itemView.findViewById(R.id.btnRemove);
        }
    }
}
