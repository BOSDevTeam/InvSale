package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.bosictsolution.invsale.common.AppConstant;

public class PayDetailActivity extends AppCompatActivity {

    Spinner spCustomer,spLocation,spPayment,spPaymentMethod,spBankPayment;
    Button btnLimitDayAdvancedPayOk,btnDollar,btnPercent,btnPaidOk,btnPaymentPercentOk,btnContinue;
    RadioButton rdoNoDiscount;
    LinearLayout layoutPaymentDebt,layoutPaymentMethod,layoutPaidChange,layoutOnlinePayment,layoutAdvancedPay;
    EditText etAdvancedPay;

    String[] customers={"Walk in Client", "CustomerA", "CustomerB"};
    String[] locations={"Location1", "Location2"};
    String[] payments={"Cash Down", "Debt"};
    String[] paymentMethods={"Cash in Hand", "Online Payment"};
    String[] bankPayments={"KBZ Pay", "CB Pay"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle("#S0001");

        fillData();
        setLayoutPaymentDebt();
        setLayoutPaymentMethod();
        setLayoutPaidChange();
        setLayoutOnlinePayment();
        setLayoutAdvancedPay();

        spPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setLayoutPaymentDebt();
                setLayoutPaymentMethod();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setLayoutPaidChange();
                setLayoutOnlinePayment();
                setLayoutAdvancedPay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnLimitDayAdvancedPayOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutPaymentMethod();
            }
        });
//        etAdvancedPay.setOn  // don't allow to start with zero input

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(PayDetailActivity.this,CustomerActivity.class);
                i.putExtra(AppConstant.extra_module_type,AppConstant.sale_module_type);
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

    private void setLayoutAdvancedPay(){
        if(etAdvancedPay.getText().toString().length()==0)  // if not have advanced pay
            layoutAdvancedPay.setVisibility(View.GONE);
        else layoutAdvancedPay.setVisibility(View.VISIBLE);
    }

    private void setLayoutOnlinePayment(){
        if(spPaymentMethod.getSelectedItemPosition()==0)  // if payment method is cashInHand
            layoutOnlinePayment.setVisibility(View.GONE);
        else if(spPaymentMethod.getSelectedItemPosition()==1)  // if payment method is onlinePayment
            layoutOnlinePayment.setVisibility(View.VISIBLE);
    }

    private void setLayoutPaidChange(){
        if(spPaymentMethod.getSelectedItemPosition()==0)  // if payment method is cashInHand
            layoutPaidChange.setVisibility(View.VISIBLE);
        else if(spPaymentMethod.getSelectedItemPosition()==1)  // if payment method is onlinePayment
            layoutPaidChange.setVisibility(View.GONE);
    }

    private void setLayoutPaymentMethod(){
        if(spPayment.getSelectedItemPosition()==1 && etAdvancedPay.getText().toString().length()==0)
            layoutPaymentMethod.setVisibility(View.GONE);  // if payment is debt, if not have advanced pay
        else layoutPaymentMethod.setVisibility(View.VISIBLE);
    }

    private void setLayoutPaymentDebt(){
        if(spPayment.getSelectedItemPosition()==0)layoutPaymentDebt.setVisibility(View.GONE);
        else if(spPayment.getSelectedItemPosition()==1)layoutPaymentDebt.setVisibility(View.VISIBLE);
    }

    private void fillData(){
        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,customers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCustomer.setAdapter(adapter);

        adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocation.setAdapter(adapter);
        spLocation.setEnabled(false);

        adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,payments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPayment.setAdapter(adapter);

        adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentMethod.setAdapter(adapter);

        adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,bankPayments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBankPayment.setAdapter(adapter);
    }

    private void setLayoutResource(){
        spCustomer=findViewById(R.id.spCustomer);
        spLocation=findViewById(R.id.spLocation);
        spPayment=findViewById(R.id.spPayment);
        spPaymentMethod=findViewById(R.id.spPaymentMethod);
        spBankPayment=findViewById(R.id.spBankPayment);
        btnLimitDayAdvancedPayOk=findViewById(R.id.btnLimitDayAdvancedPayOk);
        btnDollar=findViewById(R.id.btnDollar);
        btnPercent=findViewById(R.id.btnPercent);
        btnPaidOk=findViewById(R.id.btnPaidOk);
        btnPaymentPercentOk=findViewById(R.id.btnPaymentPercentOk);
        btnContinue=findViewById(R.id.btnContinue);
        rdoNoDiscount=findViewById(R.id.rdoNoDiscount);
        layoutPaymentDebt=findViewById(R.id.layoutPaymentDebt);
        layoutPaymentMethod=findViewById(R.id.layoutPaymentMethod);
        layoutPaidChange=findViewById(R.id.layoutPaidChange);
        layoutOnlinePayment=findViewById(R.id.layoutOnlinePayment);
        layoutAdvancedPay=findViewById(R.id.layoutAdvancedPay);
        etAdvancedPay=findViewById(R.id.etAdvancedPay);
    }
}