package com.valtech.movenpick;

import android.app.Application;
import android.content.Context;


public class MyApplication extends Application {

    private static Context mContext;

    private static final String TAG = "MyApplication";


    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        mContext = getApplicationContext();

    }

    public static Context getContext() {
        return mContext;
    }


}
