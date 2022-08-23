package com.bosictsolution.invsale.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.adapter.GeneralExpandableListAdapter;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.SubMenuData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryFilter {

    ICategoryFilter iCategoryFilter;

    public CategoryFilter(ICategoryFilter iCategoryFilter){
        this.iCategoryFilter=iCategoryFilter;
    }

    public void showCategoryFilterDialog(Context context, List<MainMenuData> lstMainMenu,List<SubMenuData> lstSubMenu) {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_category_filter, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final ExpandableListView expList = v.findViewById(R.id.list);
        final TextView tvAll = v.findViewById(R.id.tvAll);

        setDataToExpList(expList, lstMainMenu, lstSubMenu, context);

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (iCategoryFilter != null) iCategoryFilter.setOnAllClick();
            }
        });
        expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                alertDialog.dismiss();
                if (iCategoryFilter != null) iCategoryFilter.setOnChildMenuClick(groupPosition,childPosition);
                return false;
            }
        });
    }

    private void setDataToExpList(ExpandableListView expList, List<MainMenuData> lstMainMenu, List<SubMenuData> lstSubMenu,Context context) {
        GeneralExpandableListAdapter generalExpandableListAdapter;
        List<String> listDataHeader = new ArrayList<>();
        HashMap<String, List<String>> listDataChild = new HashMap<>();

        for (int i = 0; i < lstMainMenu.size(); i++) {
            int mainMenuID = lstMainMenu.get(i).getMainMenuID();
            String mainMenuName = lstMainMenu.get(i).getMainMenuName();

            List<String> lstSubMenuName = new ArrayList<>();
            for (int j = 0; j < lstSubMenu.size(); j++) {
                if (lstSubMenu.get(j).getMainMenuID() == mainMenuID) {
                    lstSubMenuName.add(lstSubMenu.get(j).getSubMenuName());
                }
            }
            listDataChild.put(mainMenuName, lstSubMenuName);
            listDataHeader.add(mainMenuName);
        }
        generalExpandableListAdapter = new GeneralExpandableListAdapter(context, listDataHeader, listDataChild);
        expList.setAdapter(generalExpandableListAdapter);
    }

    public interface ICategoryFilter{
        void setOnAllClick();
        void setOnChildMenuClick(int groupPosition,int childPosition);
    }
}
