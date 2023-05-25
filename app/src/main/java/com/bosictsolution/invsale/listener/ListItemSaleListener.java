package com.bosictsolution.invsale.listener;

import android.widget.TextView;

public interface ListItemSaleListener {
    void onQuantityClickListener(int position, TextView tvQuantity, TextView tvAmount);
    void onRemoveClickListener(int position);
    void onMoreClickListener(int position, TextView tvPrice, TextView tvAmount, TextView tvDiscount);
    void onItemLongClickListener(int position, TextView tvPrice, TextView tvAmount, TextView tvDiscount);
}
