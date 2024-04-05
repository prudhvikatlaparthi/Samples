package com.sgs.citytax.api;

import android.text.TextUtils;
import android.util.Log;

import com.sgs.citytax.BuildConfig;
import com.sgs.citytax.base.MyApplication;
import com.sgs.citytax.util.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.sgs.citytax.util.EncryptionHelperKt.doEncrypt;

class APIHelper {
    private static volatile Retrofit instance;
    private static volatile Retrofit instanceForStaticAPICall;
    private static volatile Retrofit instanceForOrangeWallet;
    private static volatile Retrofit instanceForShortURL;

    private static OkHttpClient getClient(boolean isStatic) {

        Interceptor interceptor = chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader(getHeaderKey(), getHeaderValue(isStatic))
                    .build();
            return chain.proceed(request);
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .callTimeout(5, TimeUnit.MINUTES);

        if (!BuildConfig.BUILD_VARIANT.equals(Constant.BuildVariant.PROD.getValue())) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.i("OkHttp", "log: " + message);
//                LogHelper.writeLog(null,message);
            });
            httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
            builder.addNetworkInterceptor(httpLoggingInterceptor);
        }

        builder.addInterceptor(interceptor);
        return builder.build();
    }

    public static String getHeaderKey() {
        return "justbilling";
    }

    public static String getHeaderValue(boolean isStatic) {
        String token = "";

        if (isStatic) {
            token = MyApplication.getPrefHelper().getStaticToken();
        } else if (!TextUtils.isEmpty(MyApplication.getPrefHelper().getDynamicToken())) {
            token = doEncrypt(MyApplication.getPrefHelper().getDynamicToken(), MyApplication.getPrefHelper().getSecretKey());
        }

        if (TextUtils.isEmpty(MyApplication.getPrefHelper().getSecretKey()))
            return token
                    + ":::";
        return token
                + ":" + MyApplication.getPrefHelper().getDomain()
                + ":" + doEncrypt(MyApplication.getPrefHelper().getSerialNumber(), MyApplication.getPrefHelper().getSecretKey())
                + ":" + doEncrypt(MyApplication.getPrefHelper().getUserOrgBranchID() + "", MyApplication.getPrefHelper().getSecretKey());

    }

    static Retrofit getInstance() {
        synchronized (APIHelper.class) {
            if (instance == null) {
                instance = new Retrofit.Builder()
                        .baseUrl(BuildConfig.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getClient(false))
                        .build();
            }
        }
        return instance;
    }

    static Retrofit getInstanceForStaticCall() {
        synchronized (APIHelper.class) {
            if (instanceForStaticAPICall == null) {
                instanceForStaticAPICall = new Retrofit.Builder()
                        .baseUrl(BuildConfig.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getClient(true))
                        .build();
            }
        }
        return instanceForStaticAPICall;
    }

    static Retrofit getInstanceForOrangeWallet() {
        synchronized (APIHelper.class) {
            if (instanceForOrangeWallet == null) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                        .readTimeout(5, TimeUnit.MINUTES)
                        .writeTimeout(5, TimeUnit.MINUTES)
                        .connectTimeout(5, TimeUnit.MINUTES)
                        .callTimeout(5, TimeUnit.MINUTES)
                        .build();

                instanceForOrangeWallet = new Retrofit.Builder()
                        .baseUrl(BuildConfig.ORANGE_WALLET_PAYMENT_BASE_URL)
                        .addConverterFactory(SimpleXmlConverterFactory.create())
                        .client(client)
                        .build();
            }
        }
        return instanceForOrangeWallet;
    }

    static Retrofit getInstanceForShortURL() {
        synchronized (APIHelper.class) {
            if (instanceForShortURL == null) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                        .readTimeout(5, TimeUnit.MINUTES)
                        .writeTimeout(5, TimeUnit.MINUTES)
                        .connectTimeout(5, TimeUnit.MINUTES)
                        .callTimeout(5, TimeUnit.MINUTES)
                        .build();

                instanceForShortURL = new Retrofit.Builder()
                        .baseUrl(BuildConfig.SHORT_URL_BASE_URL)
                        .addConverterFactory(SimpleXmlConverterFactory.create())
                        .client(client)
                        .build();
            }
        }
        return instanceForShortURL;
    }

}
