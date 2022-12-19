package com.bosictsolution.invsale.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.listener.IConfirmation;

public class Confirmation {

    IConfirmation iConfirmation;

    public Confirmation(IConfirmation iConfirmation) {
        this.iConfirmation = iConfirmation;
    }

    public void showConfirmDialog(Context context, String message,String... title) {
        LayoutInflater reg = LayoutInflater.from(context);
        View v = reg.inflate(R.layout.dialog_confirm, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(v);

        final ImageButton btnClose = v.findViewById(R.id.btnClose);
        final Button btnCancel = v.findViewById(R.id.btnCancel);
        final Button btnOK = v.findViewById(R.id.btnOK);
        final TextView tvMessage = v.findViewById(R.id.tvMessage);
        final TextView tvTitle = v.findViewById(R.id.tvTitle);

        if(title.length!=0)tvTitle.setText(title[0]);
        tvMessage.setText(message);

        dialog.setCancelable(true);
        android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (iConfirmation != null) iConfirmation.setOnConfirmOKClick();
            }
        });
    }
}
