package com.bosictsolution.invsale.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.MenuItemCompat;

public class NavDrawerBadge extends AppCompatTextView {

    private float strokeWidth;
    int strokeColor = Color.parseColor(AppConstant.COLOR_BLACK); // black
    int solidColor = Color.parseColor(AppConstant.COLOR_RED); // red

    // **** THIS IS THE FULL CONSTRUCTOR YOU HAVE TO CALL ****
    public NavDrawerBadge(Context context, NavigationView navigationView, int itemId, String value, String letterColor, String strokeColor, String solidColor) {
        super(context);
        MenuItemCompat.setActionView(navigationView.getMenu().findItem(itemId), this);
        NavDrawerBadge badge = (NavDrawerBadge) MenuItemCompat.getActionView(navigationView.getMenu().findItem(itemId));
        badge.setGravity(Gravity.CENTER);
        badge.setTextColor(Color.parseColor(letterColor));
        badge.setText(value);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        badge.setLayoutParams(params);
        badge.setPadding(3, 3, 3, 3);
        badge.setStrokeWidth(1);
        badge.setStrokeColor(strokeColor);
        badge.setSolidColor(solidColor);
        badge.requestLayout();
    }

    public NavDrawerBadge(Context context) {
        super(context);
    }

    public NavDrawerBadge(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavDrawerBadge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint circlePaint = new Paint();
        circlePaint.setColor(solidColor);
        circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        Paint strokePaint = new Paint();
        strokePaint.setColor(strokeColor);
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        int h = this.getHeight();
        int w = this.getWidth();

        int diameter = ((h > w) ? h : w);
        int radius = diameter / 2;

        this.setHeight(diameter);
        this.setWidth(diameter);

        canvas.drawCircle(diameter / 2, diameter / 2, radius, strokePaint);

        canvas.drawCircle(diameter / 2, diameter / 2, radius - strokeWidth, circlePaint);

        super.draw(canvas);
    }

    public void setStrokeWidth(int dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        strokeWidth = dp * scale;
    }

    public void setStrokeColor(String color) {
        strokeColor = Color.parseColor(color);
    }

    public void setSolidColor(String color) {
        solidColor = Color.parseColor(color);
    }
}

