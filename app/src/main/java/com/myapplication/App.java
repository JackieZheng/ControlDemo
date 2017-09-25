package com.myapplication;

import com.blankj.utilcode.util.Utils;
import com.example.mylibrary.base.BaseApp;
import com.facebook.soloader.SoLoader;
import com.myapplication.glide.ConcealUtil;
import timber.log.Timber;

public class App extends BaseApp {

  @Override public void onCreate() {
    super.onCreate();
    SoLoader.init(this, false);
    ConcealUtil.init(this, "123456");
  }
}