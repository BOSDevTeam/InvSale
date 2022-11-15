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
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.common.ConnectionLiveData;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.ClientData;
import com.bosictsolution.invsale.data.ConnectionData;
import com.bosictsolution.invsale.data.DivisionData;
import com.bosictsolution.invsale.data.TownshipData;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    Spinner spDivision,spTownship;
    TextInputLayout inputUserName,inputShopName,inputPhone,inputAddress;
    EditText etUserName,etShopName,etPhone,etAddress;
    TextView tvSignIn;
    AppSetting appSetting=new AppSetting();
    List<DivisionData> lstDivision=new ArrayList<>();
    List<TownshipData> lstTownship=new ArrayList<>();
    private Context context=this;
    private ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    ConnectionLiveData connectionLiveData;
    DatabaseAccess db;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth auth;
    private String verificationCode;
    ClientData clientData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        setLayoutResource();
        init();

        checkConnection();
        fillData();
        startFirebaseLogin();

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
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateControl()) {
                    clientData = new ClientData();
                    clientData.setClientName(etUserName.getText().toString());
                    clientData.setShopName(etShopName.getText().toString());
                    clientData.setPhone(etPhone.getText().toString());
                    clientData.setAddress(etAddress.getText().toString());
                    int position = spDivision.getSelectedItemPosition();
                    int divisionId = lstDivision.get(position).getDivisionID();
                    String divisionName = lstDivision.get(position).getDivisionName();
                    clientData.setDivisionID(divisionId);
                    clientData.setDivisionName(divisionName);
                    position = spTownship.getSelectedItemPosition();
                    int townshipId = lstTownship.get(position).getTownshipID();
                    String townshipName = lstTownship.get(position).getTownshipName();
                    clientData.setTownshipID(townshipId);
                    clientData.setTownshipName(townshipName);
                    clientData.setSalePerson(false);
                    if (db.getIsClientPhoneVerify() == 1) checkClientPhone();
                    else insertClient(clientData);  // insert client to database
                }
            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, SignInActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void init() {
        connectionLiveData = new ConnectionLiveData(context);
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        db=new DatabaseAccess(context);
        progressDialog = new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
    }

    private boolean validateControl(){
        if(etUserName.getText().toString().length()==0){
            inputUserName.setError(getResources().getString(R.string.enter_value));
            return false;
        }else if(etShopName.getText().toString().length()==0){
            inputShopName.setError(getResources().getString(R.string.enter_value));
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

    private void checkConnection(){
        connectionLiveData.observe(this, new Observer<ConnectionData>() {
            @Override
            public void onChanged(ConnectionData connectionData) {
                if (!connectionData.getIsConnected())
                    appSetting.showSnackBar(findViewById(R.id.layoutRoot));
            }
        });
    }

    private void fillData(){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        getDivision();
    }

    private void getDivision() {
        Api.getClient().getDivision().enqueue(new Callback<List<DivisionData>>() {
            @Override
            public void onResponse(Call<List<DivisionData>> call, Response<List<DivisionData>> response) {
                if (response.body() == null) return;
                lstDivision = response.body();
                setDivision();
            }

            @Override
            public void onFailure(Call<List<DivisionData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getTownshipByDivision(int divisionId) {
        if(!progressDialog.isShowing()){
            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
        }
        Api.getClient().getTownshipByDivision(divisionId).enqueue(new Callback<List<TownshipData>>() {
            @Override
            public void onResponse(Call<List<TownshipData>> call, Response<List<TownshipData>> response) {
                progressDialog.dismiss();
                if (response.body() == null) return;
                lstTownship = response.body();
                setTownship();
            }

            @Override
            public void onFailure(Call<List<TownshipData>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void insertClient(ClientData clientData) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().insertClient(clientData).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                progressDialog.dismiss();
                if (response.body() == null) return;
                int clientId = response.body();
                if (clientId != 0) {
                    Toast.makeText(context, getResources().getString(R.string.register_success), Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt(AppConstant.CLIENT_ID, clientId);
                    editor.putString(AppConstant.CLIENT_NAME, clientData.getClientName());
                    editor.putString(AppConstant.CLIENT_SHOP_NAME, clientData.getShopName());
                    editor.putString(AppConstant.CLIENT_PHONE, clientData.getPhone());
                    editor.putInt(AppConstant.CLIENT_DIVISION_ID, clientData.getDivisionID());
                    editor.putString(AppConstant.CLIENT_DIVISION_NAME, clientData.getDivisionName());
                    editor.putInt(AppConstant.CLIENT_TOWNSHIP_ID, clientData.getTownshipID());
                    editor.putString(AppConstant.CLIENT_TOWNSHIP_NAME, clientData.getTownshipName());
                    editor.putString(AppConstant.CLIENT_ADDRESS, clientData.getAddress());
                    editor.putBoolean(AppConstant.ACCESS_NOTI, true);
                    editor.commit();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else
                    Toast.makeText(context, getResources().getString(R.string.already_register_phone), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkClientPhone() {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().checkClient(clientData.getPhone()).enqueue(new Callback<ClientData>() {
            @Override
            public void onResponse(Call<ClientData> call, Response<ClientData> response) {
                if (response.body() == null) return;
                ClientData data = response.body();
                if (data.getClientID() == 0) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            clientData.getPhone(),           // Phone number to verify
                            60,                           // Timeout duration
                            TimeUnit.SECONDS,                // Unit of timeout
                            RegisterActivity.this,   // Activity (for callback binding)
                            mCallback);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, getResources().getString(R.string.already_register_phone), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ClientData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDivision(){
        if(lstDivision != null && lstDivision.size()!=0) {
            String[] divisions = new String[lstDivision.size()];
            for (int i = 0; i < lstDivision.size(); i++) {
                divisions[i] = lstDivision.get(i).getDivisionName();
            }
            ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, divisions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spDivision.setAdapter(adapter);
        }
    }

    private void setTownship(){
        if(lstTownship != null && lstTownship.size()!=0) {
            String[] townships = new String[lstTownship.size()];
            for (int i = 0; i < lstTownship.size(); i++) {
                townships[i] = lstTownship.get(i).getTownshipName();
            }
            ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, townships);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spTownship.setAdapter(adapter);
        }
    }

    private void setLayoutResource(){
        btnRegister=findViewById(R.id.btnRegister);
        spDivision=findViewById(R.id.spDivision);
        spTownship=findViewById(R.id.spTownship);
        etUserName=findViewById(R.id.etUserName);
        etShopName=findViewById(R.id.etShopName);
        etPhone=findViewById(R.id.etPhone);
        etAddress=findViewById(R.id.etAddress);
        inputUserName=findViewById(R.id.inputUserName);
        inputShopName=findViewById(R.id.inputShopName);
        inputPhone=findViewById(R.id.inputPhone);
        inputAddress=findViewById(R.id.inputAddress);
        tvSignIn=findViewById(R.id.tvSignIn);
    }

    private void startFirebaseLogin() {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
                Toast.makeText(context,getResources().getString(R.string.verification_completed), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(RegisterActivity.this, OTPConfirmActivity.class);
                i.putExtra("ClientData", (Parcelable) clientData);
                i.putExtra("VerificationCode",verificationCode);
                startActivity(i);
                finish();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(context,getResources().getString(R.string.invalid_verification),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(context,getResources().getString(R.string.code_sent),Toast.LENGTH_SHORT).show();
            }
        };
    }
}