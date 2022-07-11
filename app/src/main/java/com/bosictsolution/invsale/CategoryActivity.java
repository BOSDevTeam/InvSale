package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.bosictsolution.invsale.adapter.ExpandableListAdapter;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.SubMenuData;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    ExpandableListView expList;
    ExtendedFloatingActionButton fab;
    List<String> listDataHeader;
    HashMap<String,List<String>> listDataChild;
    ExpandableListAdapter expListAdapter;
    List<MainMenuData> lstMainMenu=new ArrayList<>();
    List<SubMenuData> lstSubMenu=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.categories));

        createMainMenu();
        createSubMenu();
        setDataToExpList();

        expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id){
                int subMenuId,mainMenuId;
                String subMenuName;
                List<Integer> lstSubMenuID = new ArrayList<>();

                mainMenuId = lstMainMenu.get(groupPosition).getMainMenuID();
                subMenuName = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                for (int i = 0; i < lstSubMenu.size(); i++) {
                    if (lstSubMenu.get(i).getMainMenuID() == mainMenuId) {
                        lstSubMenuID.add(lstSubMenu.get(i).getSubMenuID());
                    }
                }
                subMenuId = lstSubMenuID.get(childPosition);
                Intent i=new Intent(CategoryActivity.this, ProductActivity.class);
                startActivity(i);
                return false;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(CategoryActivity.this, SaleOrderSummaryActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createMainMenu(){
        MainMenuData data=new MainMenuData();
        data.setMainMenuID(1);
        data.setMainMenuName("Main Menu 1");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(2);
        data.setMainMenuName("Main Menu 2");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(3);
        data.setMainMenuName("Main Menu 3");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(4);
        data.setMainMenuName("Main Menu 4");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(5);
        data.setMainMenuName("Main Menu 5");
        lstMainMenu.add(data);

        data=new MainMenuData();
        data.setMainMenuID(6);
        data.setMainMenuName("Main Menu 6");
        lstMainMenu.add(data);
    }

    private void createSubMenu(){
        SubMenuData data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 1/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 2/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 3/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 4/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 5/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(1);
        data.setSubMenuName("Sub Menu 6/1");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(2);
        data.setSubMenuName("Sub Menu 1/2");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(3);
        data.setSubMenuName("Sub Menu 1/3");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(4);
        data.setSubMenuName("Sub Menu 1/4");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(5);
        data.setSubMenuName("Sub Menu 1/5");
        lstSubMenu.add(data);

        data=new SubMenuData();
        data.setSubMenuID(1);
        data.setMainMenuID(6);
        data.setSubMenuName("Sub Menu 1/6");
        lstSubMenu.add(data);
    }

    private void setDataToExpList(){
        listDataHeader=new ArrayList<>();
        listDataChild=new HashMap<>();
        for(int i=0;i<lstMainMenu.size();i++){
            int mainMenuID=lstMainMenu.get(i).getMainMenuID();
            String mainMenuName=lstMainMenu.get(i).getMainMenuName();

            List<String> lstSubMenuName=new ArrayList<>();
            for(int j=0;j<lstSubMenu.size();j++){
                if(lstSubMenu.get(j).getMainMenuID()==mainMenuID){
                    lstSubMenuName.add(lstSubMenu.get(j).getSubMenuName());
                }
            }
            if(lstSubMenuName.size()!=0){
                listDataChild.put(mainMenuName, lstSubMenuName);
                listDataHeader.add(mainMenuName);
            }
        }
        expListAdapter=new ExpandableListAdapter(this,listDataHeader,listDataChild);
        expList.setAdapter(expListAdapter);
    }

    private void setLayoutResource() {
        expList = findViewById(R.id.list);
        fab=findViewById(R.id.fab);
    }
}