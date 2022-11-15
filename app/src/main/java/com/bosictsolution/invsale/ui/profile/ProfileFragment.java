package com.bosictsolution.invsale.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.api.Api;
import com.bosictsolution.invsale.common.AppConstant;
import com.bosictsolution.invsale.data.ClientData;
import com.bosictsolution.invsale.data.DivisionData;
import com.bosictsolution.invsale.data.TownshipData;
import com.bosictsolution.invsale.databinding.FragmentProfileBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    Spinner spDivision,spTownship;
    TextView tvPhone,tvDivision,tvTownship;
    TextInputLayout inputUserName,inputShopName,inputAddress;
    EditText etUserName,etShopName,etAddress;
    ImageButton btnEdit;
    Button btnUpdateProfile;
    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;
    private ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    int clientId,clientDivisionId,clientTownshipId;
    List<DivisionData> lstDivision=new ArrayList<>();
    List<TownshipData> lstTownship=new ArrayList<>();

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setLayoutResource();
        sharedpreferences = getContext().getSharedPreferences(AppConstant.MYPREFERENCES, Context.MODE_PRIVATE);

        fillData();

        spDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(lstDivision.size()!=0){
                    int divisionId = lstDivision.get(i).getDivisionID();
                    getTownshipByDivision(divisionId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editData();
            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateControl()){
                    ClientData clientData = new ClientData();
                    clientData.setClientName(etUserName.getText().toString());
                    clientData.setShopName(etShopName.getText().toString());
                    clientData.setAddress(etAddress.getText().toString());
                    int position = spDivision.getSelectedItemPosition();
                    int divisionId = lstDivision.get(position).getDivisionID();
                    String divisionName=lstDivision.get(position).getDivisionName();
                    clientData.setDivisionID(divisionId);
                    clientData.setDivisionName(divisionName);
                    position = spTownship.getSelectedItemPosition();
                    int townshipId = lstTownship.get(position).getTownshipID();
                    String townshipName=lstTownship.get(position).getTownshipName();
                    clientData.setTownshipID(townshipId);
                    clientData.setTownshipName(townshipName);
                    updateClient(clientData);  // update client to database
                }
            }
        });

        return root;
    }

    private void updateClient(ClientData clientData){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().updateClient(clientId,clientData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),getResources().getString(R.string.success),Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(AppConstant.CLIENT_NAME, clientData.getClientName());
                editor.putString(AppConstant.CLIENT_SHOP_NAME, clientData.getShopName());
                editor.putInt(AppConstant.CLIENT_DIVISION_ID, clientData.getDivisionID());
                editor.putString(AppConstant.CLIENT_DIVISION_NAME, clientData.getDivisionName());
                editor.putInt(AppConstant.CLIENT_TOWNSHIP_ID, clientData.getTownshipID());
                editor.putString(AppConstant.CLIENT_TOWNSHIP_NAME, clientData.getTownshipName());
                editor.putString(AppConstant.CLIENT_ADDRESS, clientData.getAddress());
                editor.commit();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("ProfileFragment", t.getMessage());
            }
        });
    }

    private boolean validateControl(){
        if(etUserName.getText().toString().length()==0){
            inputUserName.setError(getResources().getString(R.string.enter_value));
            return false;
        }else if(etShopName.getText().toString().length()==0){
            inputShopName.setError(getResources().getString(R.string.enter_value));
            return false;
        }else if(lstDivision.size()==0){
            Toast.makeText(getContext(),getResources().getString(R.string.division_not_found),Toast.LENGTH_LONG).show();
            return false;
        }else if(lstTownship.size()==0){
            Toast.makeText(getContext(),getResources().getString(R.string.township_not_found),Toast.LENGTH_LONG).show();
            return false;
        }
        else if(etAddress.getText().toString().length()==0){
            inputAddress.setError(getResources().getString(R.string.enter_value));
            return false;
        }
        return true;
    }

    private void fillData() {
        clientId = sharedpreferences.getInt(AppConstant.CLIENT_ID, 0);
        String clientName = sharedpreferences.getString(AppConstant.CLIENT_NAME, "");
        String clientShopName = sharedpreferences.getString(AppConstant.CLIENT_SHOP_NAME, "");
        String clientPhone = sharedpreferences.getString(AppConstant.CLIENT_PHONE, "");
        clientDivisionId = sharedpreferences.getInt(AppConstant.CLIENT_DIVISION_ID, 0);
        String clientDivisionName = sharedpreferences.getString(AppConstant.CLIENT_DIVISION_NAME, "");
        clientTownshipId = sharedpreferences.getInt(AppConstant.CLIENT_TOWNSHIP_ID, 0);
        String clientTownshipName = sharedpreferences.getString(AppConstant.CLIENT_TOWNSHIP_NAME, "");
        String clientAddress = sharedpreferences.getString(AppConstant.CLIENT_ADDRESS, "");
        etUserName.setText(clientName);
        etShopName.setText(clientShopName);
        tvPhone.setText(clientPhone);
        tvDivision.setText(clientDivisionName);
        tvTownship.setText(clientTownshipName);
        etAddress.setText(clientAddress);
    }

    private void editData(){
        etUserName.setEnabled(true);
        etShopName.setEnabled(true);
        etAddress.setEnabled(true);
        tvDivision.setVisibility(View.GONE);
        tvTownship.setVisibility(View.GONE);
        spDivision.setVisibility(View.VISIBLE);
        spTownship.setVisibility(View.VISIBLE);
        btnUpdateProfile.setEnabled(true);
        getDivision();
    }

    private void getDivision() {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        Api.getClient().getDivision().enqueue(new Callback<List<DivisionData>>() {
            @Override
            public void onResponse(Call<List<DivisionData>> call, Response<List<DivisionData>> response) {
                if (response.body() == null) return;
                lstDivision = response.body();
                setDivision();
                for(int i=0;i<lstDivision.size();i++){
                    if(clientDivisionId == lstDivision.get(i).getDivisionID()){
                        spDivision.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DivisionData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("ProfileFragment", t.getMessage());
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
                for(int i=0;i<lstTownship.size();i++){
                    if(clientTownshipId == lstTownship.get(i).getTownshipID()){
                        spTownship.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<TownshipData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("ProfileFragment", t.getMessage());
            }
        });
    }

    private void setDivision(){
        String[] divisions = new String[lstDivision.size()];
        for (int i = 0; i < lstDivision.size(); i++) {
            divisions[i] = lstDivision.get(i).getDivisionName();
        }
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, divisions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(adapter);
    }

    private void setTownship(){
        String[] townships = new String[lstTownship.size()];
        for (int i = 0; i < lstTownship.size(); i++) {
            townships[i] = lstTownship.get(i).getTownshipName();
        }
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, townships);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTownship.setAdapter(adapter);
    }

    private void setLayoutResource(){
        spDivision = binding.spDivision;
        spTownship = binding.spTownship;
        tvPhone = binding.tvPhone;
        tvDivision = binding.tvDivision;
        tvTownship = binding.tvTownship;
        inputUserName = binding.inputUserName;
        inputShopName = binding.inputShopName;
        inputAddress = binding.inputAddress;
        etUserName = binding.etUserName;
        etShopName = binding.etShopName;
        etAddress = binding.etAddress;
        btnEdit = binding.btnEdit;
        btnUpdateProfile = binding.btnUpdateProfile;

        progressDialog =new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

}