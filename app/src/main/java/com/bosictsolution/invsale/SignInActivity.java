package com.bosictsolution.invsale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.data.ClientData;
import com.bosictsolution.invsale.data.ConnectionData;
import com.google.android.material.textfield.TextInputLayout;

public class SignInActivity extends AppCompatActivity {

    TextInputLayout inputPhone;
    EditText etPhone;
    Button btnContinue;
    private Context context=this;
    private ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    ConnectionLiveData connectionLiveData;
    AppSetting appSetting=new AppSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();
        setLayoutResource();
        init();

        checkConnection();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateControl()){
                    checkClient(etPhone.getText().toString(),false);
                }
            }
        });
    }

    private void checkConnection(){
        connectionLiveData.observe(this, new Observer<ConnectionData>() {
            @Override
            public void onChanged(ConnectionData connectionData) {
                if (!connectionData.getIsConnected())
                    appSetting.showSnackBar(findViewById(R.id.layoutRoot));
            }
        });
    }

    private void init(){
        connectionLiveData = new ConnectionLiveData(context);
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        progressDialog =new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
    }

    private void checkClient(String phone,boolean isSalePerson) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().checkClient(phone,isSalePerson).enqueue(new Callback<ClientData>() {
            @Override
            public void onResponse(Call<ClientData> call, Response<ClientData> response) {
                progressDialog.dismiss();
                if (response.body() == null){
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    return;
                }
                ClientData data = response.body();
                if (data.getClientID() != 0) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt(AppConstant.CLIENT_ID, data.getClientID());
                    editor.putString(AppConstant.CLIENT_NAME, data.getClientName());
                    editor.putString(AppConstant.CLIENT_PASSWORD, data.getClientPassword());
                    editor.putString(AppConstant.CLIENT_SHOP_NAME, data.getShopName());
                    editor.putString(AppConstant.CLIENT_PHONE, phone);
                    editor.putInt(AppConstant.CLIENT_DIVISION_ID, data.getDivisionID());
                    editor.putString(AppConstant.CLIENT_DIVISION_NAME, data.getDivisionName());
                    editor.putInt(AppConstant.CLIENT_TOWNSHIP_ID, data.getTownshipID());
                    editor.putString(AppConstant.CLIENT_TOWNSHIP_NAME, data.getTownshipName());
                    editor.putString(AppConstant.CLIENT_ADDRESS, data.getAddress());
                    editor.putBoolean(AppConstant.ACCESS_NOTI, true);
                    editor.commit();
                    Intent i = new Intent(SignInActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else
                    Toast.makeText(context, getResources().getString(R.string.registration_not_found), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ClientData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateControl(){
        if(etPhone.getText().toString().length()==0){
            inputPhone.setError(getResources().getString(R.string.enter_value));
            return false;
        }
        return true;
    }

    private void setLayoutResource(){
        btnContinue=findViewById(R.id.btnContinue);
        etPhone=findViewById(R.id.etPhone);
        inputPhone=findViewById(R.id.inputPhone);
    }
}