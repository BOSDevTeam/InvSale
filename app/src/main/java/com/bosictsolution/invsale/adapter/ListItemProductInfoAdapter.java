package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.ProductData;
import com.bosictsolution.invsale.listener.ListItemProductInfoListener;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemProductInfoAdapter extends RecyclerView.Adapter<ListItemProductInfoAdapter.ViewHolder> {
    private Context context;
    List<ProductData> list;
    ListItemProductInfoListener listItemProductInfoListener;
    AppSetting appSetting=new AppSetting();

    public ListItemProductInfoAdapter(List<ProductData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setOnListener(ListItemProductInfoListener listItemProductInfoListener){
        this.listItemProductInfoListener=listItemProductInfoListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_info, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvProductName.setText(list.get(position).getProductName());
        holder.tvPrice.setText(appSetting.df.format(list.get(position).getSalePrice()));
        holder.tvCode.setText(list.get(position).getCode());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listItemProductInfoListener!=null){
                    listItemProductInfoListener.onItemClickListener(position,list);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,tvPrice,tvCode;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName =itemView.findViewById(R.id.tvProductName);
            tvPrice =  itemView.findViewById(R.id.tvPrice);
            tvCode =  itemView.findViewById(R.id.tvCode);
        }
    }
}
