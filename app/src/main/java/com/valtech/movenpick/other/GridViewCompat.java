package com.valtech.movenpick.other;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.GridView;

public class GridViewCompat extends GridView {
    public GridViewCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewCompat(Context context) {
        super(context);
    }

    public GridViewCompat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getNumColumnsCompat() {
        if (VERSION.SDK_INT >= 11) {
            return getNumColumnsCompat11();
        }
        int columns = 0;
        if (getChildCount() > 0) {
            int width = getChildAt(0).getMeasuredWidth();
            if (width > 0) {
                columns = getWidth() / width;
            }
        }
        return columns <= 0 ? -1 : columns;
    }

    @TargetApi(11)
    private int getNumColumnsCompat11() {
        return getNumColumns();
    }
}
