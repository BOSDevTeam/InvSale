package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.SaleMasterData;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemSaleSummaryAdapter extends RecyclerView.Adapter<ListItemSaleSummaryAdapter.ViewHolder> {
    private Context context;
    List<SaleMasterData> list;
    AppSetting appSetting=new AppSetting();
    DatabaseAccess db;

    public ListItemSaleSummaryAdapter(List<SaleMasterData> list, Context context) {
        this.list = list;
        this.context = context;
        this.db=new DatabaseAccess(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_summary, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvDate.setText(list.get(position).getDate());
        holder.tvCustomer.setText(list.get(position).getCustomerName());
        holder.tvSaleNo.setText(context.getResources().getString(R.string.hash)+list.get(position).getUserVoucherNo());
        holder.tvGrandTotal.setText(db.getHomeCurrency()+context.getResources().getString(R.string.space)+appSetting.df.format(list.get(position).getNetAmt()));
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate,tvSaleNo,tvGrandTotal,tvCustomer;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate =  itemView.findViewById(R.id.tvDate);
            tvSaleNo =  itemView.findViewById(R.id.tvSaleNo);
            tvGrandTotal =  itemView.findViewById(R.id.tvGrandTotal);
            tvCustomer =  itemView.findViewById(R.id.tvCustomer);
        }
    }
}
