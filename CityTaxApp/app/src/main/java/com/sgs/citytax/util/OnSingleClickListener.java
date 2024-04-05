package com.sgs.citytax.util;

import android.os.SystemClock;
import android.view.View;

/*
 * Created by Adil on 10/11/2020.
 */

public abstract class OnSingleClickListener implements View.OnClickListener {

    private static final long MIN_CLICK_INTERVAL = 3000;

    private long mLastClickTime;

    /**
     * click
     * @param v The view that was clicked.
     */

    public abstract void onSingleClick(View v);

    @Override
    public final void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        if (elapsedTime <= MIN_CLICK_INTERVAL)
            return;
        mLastClickTime = currentClickTime;
        onSingleClick(v);
    }

}
