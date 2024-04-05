package com.sgs.citytax.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.sgs.citytax.R;
import com.sgs.citytax.model.Payment;
import com.sgs.citytax.util.Constant;
import com.sgs.citytax.util.GlobalKt;
import com.sgs.citytax.util.LogHelper;
import com.sgs.citytax.util.PrefHelper;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.util.Locale;

public class MyApplication extends Application {
    public static SunmiPrinterService sunmiPrinterService;
    private static MyApplication application;
    private static Payment payment;
    private static PrefHelper prefHelper;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static Context getContext() {
        return application.getApplicationContext();
    }

    public static Payment getPayment() {
        if (payment == null)
            return new Payment();
        return payment;
    }

    public static Payment resetPayment() {
        payment = new Payment();
        return payment;
    }
// removed the custome funtion
    public static PrefHelper getPrefHelper() {
        synchronized (MyApplication.class) {
            if (prefHelper == null) {
                preferences = getContext().getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_PRIVATE);
                editor = preferences.edit();
                prefHelper = new PrefHelper();
            }
            return prefHelper;
        }
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static SharedPreferences.Editor getEditor() {
        return editor;
    }

    // Handler to catch the crashes and write to logger.
    private Thread.UncaughtExceptionHandler androidDefaultUEH;
    private final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
            LogHelper.writeLog(new Exception(ex), null);
            androidDefaultUEH.uncaughtException(thread, ex);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        application = this;
        bindPrintService();
        payment = new Payment();
    }

    private void bindPrintService() {
        try {
            InnerPrinterManager.getInstance().bindService(this, new InnerPrinterCallback() {
                @Override
                protected void onConnected(SunmiPrinterService service) {
                    MyApplication.sunmiPrinterService = service;
                }

                @Override
                protected void onDisconnected() {
                    MyApplication.sunmiPrinterService = null;
                }
            });
        } catch (InnerPrinterException e) {
            LogHelper.writeLog(e,null);
        }
    }

    public static void updateLanguages(Context context, String language) {
        getPrefHelper().setLanguage(language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }

    public static Context updateLanguage(Context context) {
        if (MyApplication.getPrefHelper().getLanguage().equals(GlobalKt.getString(R.string.english)) || MyApplication.getPrefHelper().getLanguage().equals("EN")) {
            MyApplication.updateLanguages(context, "EN");
        } else {
            MyApplication.updateLanguages(context, "FR");
        }
        return context;
    }
}


