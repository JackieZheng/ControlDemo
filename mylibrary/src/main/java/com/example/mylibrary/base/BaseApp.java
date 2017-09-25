package com.example.mylibrary.base;

import android.app.Application;
import com.blankj.utilcode.util.Utils;
import com.example.mylibrary.BuildConfig;
import timber.log.Timber;

public class BaseApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    Utils.init(this);

    //在这里先使用Timber.plant注册一个Tree，然后调用静态的.d .v 去使用
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      Timber.plant(new Timber.DebugTree());
      //Timber.plant(new CrashReportingTree());
    }
  }
}
