package com.bosictsolution.invsale.listener;

import com.bosictsolution.invsale.data.ProductData;

import java.util.List;

public interface ListItemProductInfoListener {
    void onItemClickListener(int position, List<ProductData> list);
}
