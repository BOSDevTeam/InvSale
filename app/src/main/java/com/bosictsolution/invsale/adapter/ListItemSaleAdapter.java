package com.bosictsolution.invsale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.listener.ListItemSaleListener;

import java.util.List;

public class ListItemSaleAdapter extends BaseAdapter {
    private Context context;
    ListItemSaleListener listItemSaleListener;
    List<SaleTranData> list;
    AppSetting appSetting=new AppSetting();
    boolean isAllowEdit;

    public ListItemSaleAdapter(Context context, List<SaleTranData> list,boolean isAllowEdit){
        this.context=context;
        this.list=list;
        this.isAllowEdit=isAllowEdit;
    }

    public void setOnListener(ListItemSaleListener listItemSaleListener){
        this.listItemSaleListener=listItemSaleListener;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public String getItem(int position){
        return list.get(position).getProductName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvNumber,tvProductName,tvPrice,tvQuantity,tvAmount;
        ImageButton btnRemove;
    }

    @Override
    public View getView(final int position,View convertView,ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.item_sale, parent,false);
            holder=new ViewHolder();
            holder.tvProductName=row.findViewById(R.id.tvProductName);
            holder.tvPrice=row.findViewById(R.id.tvPrice);
            holder.tvQuantity=row.findViewById(R.id.tvQuantity);
            holder.tvNumber=row.findViewById(R.id.tvNumber);
            holder.tvAmount=row.findViewById(R.id.tvAmount);
            holder.btnRemove=row.findViewById(R.id.btnRemove);

            if(isAllowEdit){
                holder.tvQuantity.setBackground(context.getResources().getDrawable(R.drawable.bd_quantity));
                holder.btnRemove.setVisibility(View.VISIBLE);
            }else{
                holder.tvQuantity.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                holder.btnRemove.setVisibility(View.GONE);
            }

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvProductName.setText(list.get(position).getProductName());
        holder.tvPrice.setText(appSetting.df.format(list.get(position).getSalePrice()));
        holder.tvQuantity.setText(String.valueOf(list.get(position).getQuantity()));
        holder.tvNumber.setText(String.valueOf(list.get(position).getNumber()));
        holder.tvAmount.setText(appSetting.df.format(list.get(position).getTotalAmount()));

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
        return row;
    }
}
