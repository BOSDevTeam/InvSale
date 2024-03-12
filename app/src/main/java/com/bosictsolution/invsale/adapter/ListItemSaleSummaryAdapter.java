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
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.listener.ListSaleListener;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemSaleSummaryAdapter extends RecyclerView.Adapter<ListItemSaleSummaryAdapter.ViewHolder> {
    private Context context;
    List<SaleMasterData> list;
    AppSetting appSetting=new AppSetting();
    DatabaseAccess db;
    ListSaleListener listSaleListener;

    public ListItemSaleSummaryAdapter(List<SaleMasterData> list, Context context) {
        this.list = list;
        this.context = context;
        this.db=new DatabaseAccess(context);
    }

    public void setOnListener(ListSaleListener listSaleListener){
        this.listSaleListener=listSaleListener;
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
        holder.tvDate.setText(list.get(position).getSaleDateTime());
        holder.tvCustomer.setText(context.getResources().getString(R.string.slip_no)+context.getResources().getString(R.string.space)+list.get(position).getSlipID());
        holder.tvSaleNo.setText(context.getResources().getString(R.string.hash)+list.get(position).getUserVoucherNo());
        holder.tvGrandTotal.setText(db.getHomeCurrency()+context.getResources().getString(R.string.space)+appSetting.df.format(list.get(position).getGrandtotal()));

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(listSaleListener !=null){
                    listSaleListener.onMoreClickListener(position);
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
        TextView tvDate,tvSaleNo,tvGrandTotal,tvCustomer;
        ImageButton btnMore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate =  itemView.findViewById(R.id.tvDate);
            tvSaleNo =  itemView.findViewById(R.id.tvSaleNo);
            tvGrandTotal =  itemView.findViewById(R.id.tvGrandTotal);
            tvCustomer =  itemView.findViewById(R.id.tvCustomer);
            btnMore =  itemView.findViewById(R.id.btnMore);
        }
    }

    public void updateItem(List<SaleMasterData> list){
        this.list=list;
        notifyDataSetChanged();
    }
}
