package com.bosictsolution.invsale.ui.saleorder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SaleOrderViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SaleOrderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is sale order fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}