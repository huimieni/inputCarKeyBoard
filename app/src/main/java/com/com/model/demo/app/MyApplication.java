package com.com.model.demo.app;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    public static MyApplication MyApp;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MyApp = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
