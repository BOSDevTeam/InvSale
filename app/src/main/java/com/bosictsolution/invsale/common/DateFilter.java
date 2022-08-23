package com.bosictsolution.invsale.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.bosictsolution.invsale.R;

public class DateFilter {

    IDateFilter iDateFilter;

    public DateFilter(IDateFilter iDateFilter){
        this.iDateFilter=iDateFilter;
    }

    public void showDateFilterDialog(Context context) {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_date_filter, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final RadioButton rdoDate = v.findViewById(R.id.rdoDate);
        final RadioButton rdoFromToDate = v.findViewById(R.id.rdoFromToDate);

        dialog.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        rdoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (iDateFilter != null) iDateFilter.setOnDateClick();
            }
        });
        rdoFromToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rdoFromToDate.isChecked()) {
                    alertDialog.dismiss();
                    if (iDateFilter != null) iDateFilter.setOnFromToDateClick();
                }
            }
        });
    }

    public interface IDateFilter{
        void setOnDateClick();
        void setOnFromToDateClick();
    }
}
