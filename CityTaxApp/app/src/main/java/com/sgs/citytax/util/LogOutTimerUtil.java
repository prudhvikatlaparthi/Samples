package com.sgs.citytax.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.util.List;

public class LogOutTimerUtil {

    private static Handler handler = new Handler();
    private static Runnable runnable;

    public static synchronized void startLogoutTimer(final Context context, final LogOutListener logOutListener) {
        if (MyApplication.getPrefHelper().getAppSessionTimeOut() != 0) {
            handler.removeCallbacks(runnable);
            runnable = () -> {
                logOutListener.doLogout();
                handler.removeCallbacks(runnable);
            };
            handler.postDelayed(runnable, MyApplication.getPrefHelper().getAppSessionTimeOut() * 60 * 1000);
        }
    }

    public static synchronized void stopLogoutTimer() {
        handler.removeCallbacks(runnable);
    }

    public interface LogOutListener {
        void doLogout();
    }

    static class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

}
