package com.bosictsolution.invsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.LocationData;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListItemLocationAdapter extends RecyclerView.Adapter<ListItemLocationAdapter.ViewHolder> {
    private Context context;
    List<LocationData> list;
    AppSetting appSetting=new AppSetting();
    IListener iListener;
    DatabaseAccess db;

    public ListItemLocationAdapter(List<LocationData> list, Context context) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.rdoLocation.setText(list.get(position).getShortName());

        holder.rdoLocation.setChecked(list.get(position).isSelected());

        holder.rdoLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iListener != null) iListener.onClicked(position);
            }
        });
    }

    public void updateData(List<LocationData> lstLocation){
        this.list=lstLocation;
        notifyDataSetChanged();
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
        RadioButton rdoLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            rdoLocation =itemView.findViewById(R.id.rdoLocation);
        }
    }

    public interface IListener{
        void onClicked(int position);
    }
}
