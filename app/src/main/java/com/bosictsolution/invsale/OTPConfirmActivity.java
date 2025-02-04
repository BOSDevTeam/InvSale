package com.bosictsolution.invsale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.AppSetting;
import com.bosictsolution.invsale.data.ClientData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class OTPConfirmActivity extends AppCompatActivity {

    Button btnOK;
    EditText etOTP1,etOTP2,etOTP3,etOTP4,etOTP5,etOTP6;
    TextView tvSubtitle;
    ClientData clientData;
    String verificationCode,otp,signInPhone;
    FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private Context context=this;
    AppSetting appSetting=new AppSetting();
    SharedPreferences sharedpreferences;
    boolean isFromSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpconfirm);
        getSupportActionBar().hide();
        setLayoutResource();
        init();

        Intent i=getIntent();
        clientData=i.getParcelableExtra("ClientData");
        verificationCode=i.getStringExtra("VerificationCode");
        isFromSignIn=i.getBooleanExtra("IsFromSignIn",false);
        signInPhone=i.getStringExtra("Phone");

        fillData(signInPhone);

        etOTP1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(etOTP1.length()==0)etOTP1.requestFocus();
                else etOTP2.requestFocus();
                return false;
            }
        });
        etOTP2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(etOTP2.length()==0)etOTP2.requestFocus();
                else etOTP3.requestFocus();
                return false;
            }
        });
        etOTP3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(etOTP3.length()==0)etOTP3.requestFocus();
                else etOTP4.requestFocus();
                return false;
            }
        });
        etOTP4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(etOTP4.length()==0)etOTP4.requestFocus();
                else etOTP5.requestFocus();
                return false;
            }
        });
        etOTP5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(etOTP5.length()==0)etOTP5.requestFocus();
                else etOTP6.requestFocus();
                return false;
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp=etOTP1.getText().toString()+etOTP2.getText().toString()+etOTP3.getText().toString()+etOTP4.getText().toString()+etOTP5.getText().toString()+etOTP6.getText().toString();
                if(otp==null || otp.length()==0)return;
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                signInWithPhone(credential);
            }
        });
    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.sign_in_phone));
        auth = FirebaseAuth.getInstance();
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //getToken();
                            if(!isFromSignIn) insertClient("");
                            else {
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putInt(AppConstant.CLIENT_ID, clientData.getClientID());
                                editor.putString(AppConstant.CLIENT_NAME, clientData.getClientName());
                                editor.putString(AppConstant.CLIENT_PASSWORD, clientData.getClientPassword());
                                editor.putString(AppConstant.CLIENT_SHOP_NAME, clientData.getShopName());
                                editor.putString(AppConstant.CLIENT_PHONE, clientData.getPhone());
                                editor.putInt(AppConstant.CLIENT_DIVISION_ID, clientData.getDivisionID());
                                editor.putString(AppConstant.CLIENT_DIVISION_NAME, clientData.getDivisionName());
                                editor.putInt(AppConstant.CLIENT_TOWNSHIP_ID, clientData.getTownshipID());
                                editor.putString(AppConstant.CLIENT_TOWNSHIP_NAME, clientData.getTownshipName());
                                editor.putString(AppConstant.CLIENT_ADDRESS, clientData.getAddress());
                                editor.putBoolean(AppConstant.ACCESS_NOTI, true);
                                editor.commit();
                                Intent i = new Intent(OTPConfirmActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(OTPConfirmActivity.this, getResources().getString(R.string.incorrect_otp), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void insertClient(String token) {
        progressDialog.setMessage(getResources().getString(R.string.insert_client));
        clientData.setToken(token);
        Api.getClient().insertClient(clientData).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                progressDialog.dismiss();
                if (response.body() == null){
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    return;
                }
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
                    Intent i = new Intent(OTPConfirmActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else
                    Toast.makeText(context, getResources().getString(R.string.already_register_phone), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLayoutResource(){
        btnOK=findViewById(R.id.btnOK);
        etOTP1=findViewById(R.id.etOTP1);
        etOTP2=findViewById(R.id.etOTP2);
        etOTP3=findViewById(R.id.etOTP3);
        etOTP4=findViewById(R.id.etOTP4);
        etOTP5=findViewById(R.id.etOTP5);
        etOTP6=findViewById(R.id.etOTP6);
        tvSubtitle=findViewById(R.id.tvSubtitle);
    }

    private void init(){
        sharedpreferences = getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
    }

    private void fillData(String phone){
        tvSubtitle.setText(getResources().getString(R.string.sms_code_message) + phone);
    }

    private void getToken() {
        progressDialog.setMessage(getResources().getString(R.string.get_token));
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            if(!isFromSignIn) saveTokenInFirebase(token);
                            else updateTokenInFirebase(token);
                        }
                    }
                });
    }

    private void saveTokenInFirebase(String token) {
        progressDialog.setMessage(getResources().getString(R.string.save_token));
        String phone = auth.getCurrentUser().getPhoneNumber();
        ClientData user = new ClientData(phone, token);
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference(AppConstant.FB_NOTE_USER);
        dbUsers.child(auth.getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(OTPConfirmActivity.this, "Token Saved", Toast.LENGTH_LONG).show();
                    insertClient(token);
                }
            }
        });
    }

    private void updateTokenInFirebase(String token){
        progressDialog.setMessage(getResources().getString(R.string.update_token));
        String phone = auth.getCurrentUser().getPhoneNumber();
        ClientData user = new ClientData(phone, token);
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference(AppConstant.FB_NOTE_USER);
        dbUsers.child(auth.getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(OTPConfirmActivity.this, "Token Updated", Toast.LENGTH_LONG).show();
                    updateClientToken(token);
                }
            }
        });
    }

    private void updateClientToken(String token){
        progressDialog.setMessage(getResources().getString(R.string.update_client_token));
        Api.getClient().updateClientToken(signInPhone,token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(AppConstant.CLIENT_ID, clientData.getClientID());
                editor.putString(AppConstant.CLIENT_NAME, clientData.getClientName());
                editor.putString(AppConstant.CLIENT_PASSWORD, clientData.getClientPassword());
                editor.putString(AppConstant.CLIENT_SHOP_NAME, clientData.getShopName());
                editor.putString(AppConstant.CLIENT_PHONE, clientData.getPhone());
                editor.putInt(AppConstant.CLIENT_DIVISION_ID, clientData.getDivisionID());
                editor.putString(AppConstant.CLIENT_DIVISION_NAME, clientData.getDivisionName());
                editor.putInt(AppConstant.CLIENT_TOWNSHIP_ID, clientData.getTownshipID());
                editor.putString(AppConstant.CLIENT_TOWNSHIP_NAME, clientData.getTownshipName());
                editor.putString(AppConstant.CLIENT_ADDRESS, clientData.getAddress());
                editor.putBoolean(AppConstant.ACCESS_NOTI, true);
                editor.commit();
                Intent i = new Intent(OTPConfirmActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}