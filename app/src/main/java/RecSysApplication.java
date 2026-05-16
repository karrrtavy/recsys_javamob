package com.example.recsys;

import android.app.Application;
import utils.DataInitializer;

public class RecSysApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataInitializer.initData(this);
    }
}