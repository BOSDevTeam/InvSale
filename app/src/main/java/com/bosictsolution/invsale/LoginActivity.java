package com.bosictsolution.invsale;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.common.DatabaseAccess;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.SubMenuData;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    Button btnClear,btnDel,btnZero,btnOne,btnTwo,btnThree,btnFour,btnFive,btnSix,btnSeven,btnEight,btnNine;
    TextView tvUser,tvEnterPassword,tvCode1,tvCode2,tvCode3,tvCode4;
    SharedPreferences sharedpreferences;
    int status,status_new=1,status_confirm=2,status_existing=3;
    String clientPassword,newPassword;
    private Context context=this;
    private ProgressDialog progressDialog;
    DatabaseAccess db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        setLayoutResource();
        init();
        fillData();

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearValue();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteValue();
            }
        });
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(0);
            }
        });
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(1);
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(2);
            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(3);
            }
        });
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(4);
            }
        });
        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(5);
            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(6);
            }
        });
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(7);
            }
        });
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(8);
            }
        });
        btnNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(9);
            }
        });
    }

    private void setValue(int val) {
        if (tvCode1.getText().toString().length() == 0) tvCode1.setText(String.valueOf(val));
        else if (tvCode2.getText().toString().length() == 0) tvCode2.setText(String.valueOf(val));
        else if (tvCode3.getText().toString().length() == 0) tvCode3.setText(String.valueOf(val));
        else if (tvCode4.getText().toString().length() == 0) {
            tvCode4.setText(String.valueOf(val));
            if (status == status_new) {
                newPassword = tvCode1.getText().toString() + tvCode2.getText().toString() + tvCode3.getText().toString() + tvCode4.getText().toString();
                status = status_confirm;
                tvEnterPassword.setText(getResources().getString(R.string.enter_confirm_password));
                clearValue();
            } else if (status == status_confirm) {
                String confirmPassword = tvCode1.getText().toString() + tvCode2.getText().toString() + tvCode3.getText().toString() + tvCode4.getText().toString();
                if (newPassword.equals(confirmPassword)) {
                    Toast.makeText(context, getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
                    int clientId = sharedpreferences.getInt(AppConstant.ClientID, 0);
                    updateClientPassword(clientId, newPassword);  // update password to database
                } else {
                    Toast.makeText(context, getResources().getString(R.string.incorrect_confirm_password), Toast.LENGTH_LONG).show();
                }
            } else if (status == status_existing) {
                String inputPassword = tvCode1.getText().toString() + tvCode2.getText().toString() + tvCode3.getText().toString() + tvCode4.getText().toString();
                if (clientPassword.equals(inputPassword)) loadData();
                else
                    Toast.makeText(context, getResources().getString(R.string.incorrect_password), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateClientPassword(int clientId, String clientPassword) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().updateClientPassword(clientId, clientPassword).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(AppConstant.ClientPassword, newPassword);
                editor.commit();
                loadData();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("LoginActivity", t.getMessage());
            }
        });
    }

    private void deleteValue(){
        if(tvCode4.getText().toString().length()!=0)tvCode4.setText("");
        else if(tvCode3.getText().toString().length()!=0)tvCode3.setText("");
        else if(tvCode2.getText().toString().length()!=0)tvCode2.setText("");
        else if(tvCode1.getText().toString().length()!=0)tvCode1.setText("");
    }

    private void clearValue(){
        tvCode1.setText("");
        tvCode2.setText("");
        tvCode3.setText("");
        tvCode4.setText("");
    }

    private void getPasswordStatus() {
        clientPassword = sharedpreferences.getString(AppConstant.ClientPassword, "");
        if (clientPassword.length() != 0) status = status_existing;
        else status = status_new;
    }

    private void getMainMenu(){
        Api.getClient().getMainMenu().enqueue(new Callback<List<MainMenuData>>() {
            @Override
            public void onResponse(Call<List<MainMenuData>> call, Response<List<MainMenuData>> response) {
                if(response.isSuccessful()) {
                    List<MainMenuData> list = response.body();
                    db.insertMainMenu(list);
                    getSubMenu();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(context, getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<MainMenuData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("LoginActivity", t.getMessage());
            }
        });
    }

    private void getSubMenu() {
        Api.getClient().getSubMenu().enqueue(new Callback<List<SubMenuData>>() {
            @Override
            public void onResponse(Call<List<SubMenuData>> call, Response<List<SubMenuData>> response) {
                if (response.isSuccessful()) {
                    List<SubMenuData> list = response.body();
                    db.insertSubMenu(list);
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubMenuData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("LoginActivity", t.getMessage());
            }
        });
    }

    private void loadData(){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        getMainMenu();
    }

    private void fillData() {
        String clientName = sharedpreferences.getString(AppConstant.ClientName, "");
        tvUser.setText(clientName);
        getPasswordStatus();
        if (status == status_existing)
            tvEnterPassword.setText(getResources().getString(R.string.enter_password));
        else tvEnterPassword.setText(getResources().getString(R.string.enter_new_password));
    }

    private void init(){
        sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
        db=new DatabaseAccess(context);
        progressDialog =new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void setLayoutResource(){
        btnClear=findViewById(R.id.btnClear);
        btnDel=findViewById(R.id.btnDel);
        btnZero=findViewById(R.id.btnZero);
        btnOne=findViewById(R.id.btnOne);
        btnTwo=findViewById(R.id.btnTwo);
        btnThree=findViewById(R.id.btnThree);
        btnFour=findViewById(R.id.btnFour);
        btnFive=findViewById(R.id.btnFive);
        btnSix=findViewById(R.id.btnSix);
        btnSeven=findViewById(R.id.btnSeven);
        btnEight=findViewById(R.id.btnEight);
        btnNine=findViewById(R.id.btnNine);
        tvUser=findViewById(R.id.tvUser);
        tvEnterPassword=findViewById(R.id.tvEnterPassword);
        tvCode1=findViewById(R.id.tvCode1);
        tvCode2=findViewById(R.id.tvCode2);
        tvCode3=findViewById(R.id.tvCode3);
        tvCode4=findViewById(R.id.tvCode4);
    }
}