package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.data.NotificationData;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemNotificationAdapter extends RecyclerView.Adapter<ListItemNotificationAdapter.ViewHolder> {
    private Context context;
    List<NotificationData> list;
    INotification iNotification;

    public ListItemNotificationAdapter(Context context, List<NotificationData> list) {
        this.list = list;
        this.context = context;
    }

    public void setListener(INotification iNotification){
        this.iNotification=iNotification;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvNotiMessage.setText(list.get(position).getNotiMessage());
        holder.tvNotiDateTime.setText(list.get(position).getNotiDateTime());
        holder.tvNotiType.setText(String.valueOf(list.get(position).getNotiType()));
        holder.tvNotiID.setText(String.valueOf(list.get(position).getNotiID()));

        if (list.get(position).getNotiType() == AppConstant.NOTI_UPDATE_ORDER)
            holder.tvNotiMessage.getCompoundDrawables()[0].setTint(context.getResources().getColor(R.color.accent_700));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iNotification!=null) {
                    if (list.get(position).getNotiType() == AppConstant.NOTI_NEW_PRODUCT)
                        iNotification.setOnNewProductNotiClick(list.get(position).getNotiID());
                    else if (list.get(position).getNotiType() == AppConstant.NOTI_UPDATE_ORDER)
                        iNotification.setOnUpdateOrderNotiClick(list.get(position).getNotiID());
                }
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
        TextView tvNotiMessage,tvNotiDateTime,tvNotiType,tvNotiID;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNotiMessage =itemView.findViewById(R.id.tvNotiMessage);
            tvNotiDateTime =itemView.findViewById(R.id.tvNotiDateTime);
            tvNotiType =  itemView.findViewById(R.id.tvNotiType);
            tvNotiID =  itemView.findViewById(R.id.tvNotiID);
        }
    }

    public interface INotification{
        void setOnNewProductNotiClick(int productId);
        void setOnUpdateOrderNotiClick(int saleOrderId);
    }
}
