package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.ProductData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemProductAdapter extends RecyclerView.Adapter<ListItemProductAdapter.ViewHolder> {
    private Context context;
    List<ProductData> list;
    AppSetting appSetting=new AppSetting();
    BottomSheetBehavior sheetBehavior;

    public ListItemProductAdapter(List<ProductData> list, Context context, LinearLayout bottom_sheet) {
        this.list = list;
        this.context = context;
        this.sheetBehavior=BottomSheetBehavior.from(bottom_sheet);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvProductName.setText(list.get(position).getProductName());
        holder.tvPrice.setText("MMK "+appSetting.df.format(list.get(position).getSalePrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,tvPrice;
        ImageView imgPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName =itemView.findViewById(R.id.tvProductName);
            tvPrice =  itemView.findViewById(R.id.tvPrice);
            imgPhoto =  itemView.findViewById(R.id.imgPhoto);
        }
    }
}
