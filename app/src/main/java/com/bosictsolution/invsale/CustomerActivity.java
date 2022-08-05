package com.bosictsolution.invsale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.CustomerData;
import com.bosictsolution.invsale.data.DivisionData;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.TownshipData;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerActivity extends AppCompatActivity {

    Button btnConfirm;
    Spinner spDivision,spTownship;
    TextInputLayout inputName,inputPhone;
    EditText etName,etPhone,etEmail,etContactPerson,etAddress;
    short moduleType;
    private Context context=this;
    private ProgressDialog progressDialog;
    List<DivisionData> lstDivision=new ArrayList<>();
    List<TownshipData> lstTownship=new ArrayList<>();
    int locationId,clientId;
    DatabaseAccess db;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        setLayoutResource();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.customer_detail));
        sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
        db=new DatabaseAccess(context);

        Intent i=getIntent();
        moduleType=i.getShortExtra(AppConstant.extra_module_type,Short.MIN_VALUE);
        locationId=i.getIntExtra("LocationID",0);

        clientId=sharedpreferences.getInt(AppConstant.ClientID,0);

        fillData();

        spDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(lstDivision.size()!=0) {
                    int divisionId = lstDivision.get(i).getDivisionID();
                    getTownshipByDivision(divisionId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateControl()){
                    CustomerData customerData=new CustomerData();
                    customerData.setCustomerName(etName.getText().toString());
                    customerData.setPhone(etPhone.getText().toString());
                    customerData.setEmail(etEmail.getText().toString());
                    customerData.setContact(etContactPerson.getText().toString());
                    customerData.setAddress(etAddress.getText().toString());
                    int position=spDivision.getSelectedItemPosition();
                    int divisionId=lstDivision.get(position).getDivisionID();
                    customerData.setDivisionID(divisionId);
                    position=spTownship.getSelectedItemPosition();
                    int townshipId=lstTownship.get(position).getTownshipID();
                    customerData.setTownshipID(townshipId);
                    insertCustomer(customerData);
                }
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

    private void insertCustomer(CustomerData customerData) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().insertCustomer(customerData).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int customerId = response.body();
                if (customerId != 0) {
                    insertSale(customerId);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, getResources().getString(R.string.already_exist_customer_phone), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("CustomerActivity", t.getMessage());
            }
        });
    }

    private void insertSale(int customerId) {
        SaleMasterData data = db.getMasterSale();
        data.setLstSaleTran(db.getTranSale());
        data.setCustomerID(customerId);
        data.setClient(true);
        data.setClientID(clientId);
        Api.getClient().insertSale(data).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    int slipId=response.body();
                    Intent intent = new Intent();
                    if (moduleType == AppConstant.sale_module_type) {
                        intent = new Intent(CustomerActivity.this, SaleBillActivity.class);
                        intent.putExtra("LocationID", locationId);
                        intent.putExtra("CustomerName", etName.getText().toString());
                        intent.putExtra("SlipID",slipId );
                    } else if (moduleType == AppConstant.sale_order_module_type) {
                        intent = new Intent(CustomerActivity.this, SaleOrderSuccessActivity.class);
                    }
                    if (intent != null) {
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("CustomerActivity", t.getMessage());
            }
        });
    }

    private boolean validateControl(){
        if(etName.getText().toString().length()==0){
            inputName.setError(getResources().getString(R.string.enter_value));
            return false;
        }else if(etPhone.getText().toString().length()==0){
            inputPhone.setError(getResources().getString(R.string.enter_value));
            return false;
        }else if(lstDivision.size()==0){
            Toast.makeText(context,getResources().getString(R.string.division_not_found),Toast.LENGTH_LONG).show();
            return false;
        }else if(lstTownship.size()==0){
            Toast.makeText(context,getResources().getString(R.string.township_not_found),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void fillData(){
        if(moduleType==AppConstant.sale_module_type)btnConfirm.setText(getResources().getString(R.string.pay_confirm));
        else if(moduleType==AppConstant.sale_order_module_type)btnConfirm.setText(getResources().getString(R.string.order_confirm));
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        getDivision();
    }

    private void getDivision() {
        Api.getClient().getDivision().enqueue(new Callback<List<DivisionData>>() {
            @Override
            public void onResponse(Call<List<DivisionData>> call, Response<List<DivisionData>> response) {
                lstDivision = response.body();
                setDivision();
            }

            @Override
            public void onFailure(Call<List<DivisionData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("RegisterActivity", t.getMessage());
            }
        });
    }

    private void getTownshipByDivision(int divisionId) {
        Api.getClient().getTownshipByDivision(divisionId).enqueue(new Callback<List<TownshipData>>() {
            @Override
            public void onResponse(Call<List<TownshipData>> call, Response<List<TownshipData>> response) {
                progressDialog.dismiss();
                lstTownship = response.body();
                setTownship();
            }

            @Override
            public void onFailure(Call<List<TownshipData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("RegisterActivity", t.getMessage());
            }
        });
    }

    private void setDivision(){
        String[] divisions = new String[lstDivision.size()];
        for (int i = 0; i < lstDivision.size(); i++) {
            divisions[i] = lstDivision.get(i).getDivisionName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, divisions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(adapter);
    }

    private void setTownship(){
        String[] townships = new String[lstTownship.size()];
        for (int i = 0; i < lstTownship.size(); i++) {
            townships[i] = lstTownship.get(i).getTownshipName();
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, townships);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTownship.setAdapter(adapter);
    }

    private void setLayoutResource(){
        spDivision=findViewById(R.id.spDivision);
        spTownship=findViewById(R.id.spTownship);
        btnConfirm=findViewById(R.id.btnConfirm);
        inputName=findViewById(R.id.inputName);
        inputPhone=findViewById(R.id.inputPhone);
        etName=findViewById(R.id.etName);
        etPhone=findViewById(R.id.etPhone);
        etEmail=findViewById(R.id.etEmail);
        etContactPerson=findViewById(R.id.etContactPerson);
        etAddress=findViewById(R.id.etAddress);

        progressDialog =new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }
}