package com.bosictsolution.invsale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.SubMenuData;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<MainMenuData> _listDataHeader;
    private HashMap<MainMenuData,List<SubMenuData>> _listDataChild;

    public ExpandableListAdapter(Context context, List<MainMenuData> listDataHeader, HashMap<MainMenuData,List<SubMenuData>> listDataChild){
        this._context=context;
        this._listDataHeader=listDataHeader;
        this._listDataChild=listDataChild;
    }

    @Override
    public Object getChild(int groupPosition,int childPosition){
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition,int childPosition){
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition,final int childPosition,boolean isLastChild,View convertView,ViewGroup parent){
        final SubMenuData data=(SubMenuData)getChild(groupPosition,childPosition);
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.exp_list_child, null);
        }
        TextView tvChild=(TextView) convertView.findViewById(R.id.tvChild);
        ImageView imgPhoto=(ImageView)convertView.findViewById(R.id.imgPhoto);
        tvChild.setText(data.getSubMenuName());

        if (data.getPhotoUrl()!=null && data.getPhotoUrl().length()!=0)
            Picasso.with(convertView.getContext()).load(data.getPhotoUrl()).into(imgPhoto);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition){
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition){
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition){
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition,boolean isExpanded,View convertView,ViewGroup parent){
        MainMenuData data=(MainMenuData)getGroup(groupPosition);
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.exp_list_group, null);
        }
        TextView tvGroup=(TextView)convertView.findViewById(R.id.tvGroup);
        ImageView imgPhoto=(ImageView)convertView.findViewById(R.id.imgPhoto);
        tvGroup.setText(data.getMainMenuName());

        if (data.getPhotoUrl()!=null && data.getPhotoUrl().length()!=0)
            Picasso.with(convertView.getContext()).load(data.getPhotoUrl()).into(imgPhoto);

        return convertView;
    }

    @Override
    public int getGroupCount(){
        return this._listDataHeader.size();
    }

    @Override
    public boolean hasStableIds(){
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition,int childPosition){
        return true;
    }
}
