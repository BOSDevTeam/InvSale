package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.SaleTranData;

import java.util.ArrayList;
import java.util.List;

public class SaleBillActivity extends AppCompatActivity {

    LinearLayout layoutList;
    List<SaleTranData> lstSaleTran=new ArrayList<>();
    AppSetting appSetting=new AppSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_bill);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.sale_completed));

        createBillItem();
    }

    private void createBillItem(){
        SaleTranData data=new SaleTranData();
        data.setNumber(1);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setTotalAmount(2000);
        lstSaleTran.add(data);

        data=new SaleTranData();
        data.setNumber(2);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setTotalAmount(2000);
        lstSaleTran.add(data);

        data=new SaleTranData();
        data.setNumber(3);
        data.setProductName("Product ABC");
        data.setQuantity(1);
        data.setSalePrice(2000);
        data.setTotalAmount(2000);
        lstSaleTran.add(data);

        for (int i = 0; i< lstSaleTran.size(); i++) {
            LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.item_sale, null);
            TextView tvProductName=row.findViewById(R.id.tvProductName);
            TextView tvPrice=row.findViewById(R.id.tvPrice);
            TextView tvQuantity=row.findViewById(R.id.tvQuantity);
            TextView tvNumber=row.findViewById(R.id.tvNumber);
            TextView tvAmount=row.findViewById(R.id.tvAmount);
            ImageButton btnRemove=row.findViewById(R.id.btnRemove);

            tvQuantity.setBackgroundColor(getResources().getColor(R.color.transparent));
            btnRemove.setVisibility(View.GONE);

            tvProductName.setText(lstSaleTran.get(i).getProductName());
            tvPrice.setText(appSetting.df.format(lstSaleTran.get(i).getSalePrice()));
            tvQuantity.setText(String.valueOf(lstSaleTran.get(i).getQuantity()));
            tvNumber.setText(String.valueOf(lstSaleTran.get(i).getNumber()));
            tvAmount.setText(appSetting.df.format(lstSaleTran.get(i).getTotalAmount()));

            layoutList.addView(row);
        }
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

    private void setLayoutResource(){
        layoutList=findViewById(R.id.layoutList);
    }
}