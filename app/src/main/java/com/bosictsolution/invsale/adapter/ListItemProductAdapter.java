package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.ProductData;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemProductAdapter extends RecyclerView.Adapter<ListItemProductAdapter.ViewHolder> {
    private Context context;
    List<ProductData> list;
    AppSetting appSetting=new AppSetting();
    IListener iListener;
    DatabaseAccess db;

    public ListItemProductAdapter(List<ProductData> list, Context context) {
        this.list = list;
        this.context = context;
        this.db=new DatabaseAccess(context);
    }

    public void setListener(IListener iListener){
        this.iListener=iListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvProductName.setText(list.get(position).getProductName());
        holder.tvPrice.setText(db.getHomeCurrency()+context.getResources().getString(R.string.space) + appSetting.df.format(list.get(position).getSalePrice()));

        if (list.get(position).getQuantity() != 0) {
            holder.tvQuantity.setText(String.valueOf(list.get(position).getQuantity()));
            holder.tvQuantity.setVisibility(View.VISIBLE);
        }else
            holder.tvQuantity.setVisibility(View.GONE);

        if (list.get(position).getPhotoUrl() != null && list.get(position).getPhotoUrl().length() != 0)
            Picasso.with(context).load(list.get(position).getPhotoUrl()).into(holder.imgPhoto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iListener != null) iListener.onProductClicked(position);
            }
        });
    }

    public void setItem(List<ProductData> list){
        this.list=list;
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
        TextView tvProductName,tvPrice,tvQuantity;
        ImageView imgPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName =itemView.findViewById(R.id.tvProductName);
            tvPrice =  itemView.findViewById(R.id.tvPrice);
            tvQuantity =  itemView.findViewById(R.id.tvQuantity);
            imgPhoto =  itemView.findViewById(R.id.imgPhoto);
        }
    }

    public interface IListener{
        void onProductClicked(int position);
    }
}
