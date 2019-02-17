package com.oude.dndclub.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.oude.dndclub.utils.PublicStaticData;

public class MyApplication extends Application {
    private static MyApplication mContext;
    public static Context context;

    public synchronized static MyApplication getInstance() {
        if (mContext == null) {
            mContext = new MyApplication();
        }
        return mContext;
    }

    @Override
    public void onCreate() {
        getInstance();
        //初始化PreferencesUtils
        PublicStaticData.mySharedPreferences = getSharedPreferences("", Activity.MODE_PRIVATE);
        context = getApplicationContext();
        super.onCreate();
    }
}
