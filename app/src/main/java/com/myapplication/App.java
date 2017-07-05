package com.myapplication;

import android.app.Application;
import com.facebook.soloader.SoLoader;
import com.myapplication.glide.ConcealUtil;
import timber.log.Timber;

public class App extends Application {

  @Override public void onCreate() {
    super.onCreate();
    SoLoader.init(this, false);
    ConcealUtil.init(this, "123456");

    //在这里先使用Timber.plant注册一个Tree，然后调用静态的.d .v 去使用
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      //Timber.plant(new CrashReportingTree());
    }
  }
}