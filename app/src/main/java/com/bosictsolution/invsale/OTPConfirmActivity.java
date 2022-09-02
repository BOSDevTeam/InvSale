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
import android.view.View;
import android.widget.Button;
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

public class OTPConfirmActivity extends AppCompatActivity {

    Button btnOK;
    TextView tvOTP1,tvOTP2,tvOTP3,tvOTP4,tvOTP5,tvOTP6,tvSubtitle;
    ClientData clientData;
    String verificationCode,otp;
    FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private Context context=this;
    AppSetting appSetting=new AppSetting();
    SharedPreferences sharedpreferences;

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

        fillData();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp=tvOTP1.getText().toString()+tvOTP2.getText().toString()+tvOTP3.getText().toString()+tvOTP4.getText().toString()+tvOTP5.getText().toString()+tvOTP6.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                signInWithPhone(credential);
            }
        });
    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        auth = FirebaseAuth.getInstance();
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            insertClient();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(OTPConfirmActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void insertClient() {
        Api.getClient().insertClient(clientData).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                progressDialog.dismiss();
                int clientId = response.body();
                if (clientId != 0) {
                    Toast.makeText(context, getResources().getString(R.string.register_success), Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt(AppConstant.ClientID, clientId);
                    editor.putString(AppConstant.ClientName, clientData.getClientName());
                    editor.putString(AppConstant.ClientShopName, clientData.getShopName());
                    editor.putString(AppConstant.ClientPhone, clientData.getPhone());
                    editor.putInt(AppConstant.ClientDivisionID, clientData.getDivisionID());
                    editor.putString(AppConstant.ClientDivisionName, clientData.getDivisionName());
                    editor.putInt(AppConstant.ClientTownshipID, clientData.getTownshipID());
                    editor.putString(AppConstant.ClientTownshipName, clientData.getTownshipName());
                    editor.putString(AppConstant.ClientAddress, clientData.getAddress());
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
        tvOTP1=findViewById(R.id.tvOTP1);
        tvOTP2=findViewById(R.id.tvOTP2);
        tvOTP3=findViewById(R.id.tvOTP3);
        tvOTP4=findViewById(R.id.tvOTP4);
        tvOTP5=findViewById(R.id.tvOTP5);
        tvOTP6=findViewById(R.id.tvOTP6);
        tvSubtitle=findViewById(R.id.tvSubtitle);
    }

    private void init(){
        sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(context);
        appSetting.setupProgress(progressDialog);
    }

    private void fillData(){
        tvSubtitle.setText(getResources().getString(R.string.sms_code_message) + clientData.getPhone());
    }
}