package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.data.ProductData;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemProductInfoAdapter extends RecyclerView.Adapter<ListItemProductInfoAdapter.ViewHolder> {
    private Context context;
    List<ProductData> list;

    public ListItemProductInfoAdapter(List<ProductData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product_info, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvProductName.setText(list.get(position).getProductName());
        holder.tvCode.setText(list.get(position).getProductCode());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,tvCode;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName =itemView.findViewById(R.id.tvProductName);
            tvCode =  itemView.findViewById(R.id.tvCode);
        }
    }
}
