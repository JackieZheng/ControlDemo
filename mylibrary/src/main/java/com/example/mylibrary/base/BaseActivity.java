package com.example.mylibrary.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.example.mylibrary.SystemBarConfig;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
  protected SystemBarConfig mSystemBarConfig;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (mSystemBarConfig == null) {
      mSystemBarConfig = new SystemBarConfig(this);
    }
  }

  /**
   * 获取系统UI配置信息
   *
   * @return {@link SystemBarConfig}
   */
  public SystemBarConfig getSystemBarConfig() {
    if (mSystemBarConfig == null) {
      mSystemBarConfig = new SystemBarConfig(this);
    }
    return mSystemBarConfig;
  }

  /**
   * 内容是不是显示到状态栏下层,状态栏是否透明的
   *
   * @return {@link Boolean}
   */
  public boolean isWindowTransparent() {
    Window window = getWindow();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      View decorView = window.getDecorView();
      return (decorView.getSystemUiVisibility() & (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)) == (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      WindowManager.LayoutParams params = window.getAttributes();
      return ((params.flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
          == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
    return false;
  }

  /**
   * 设置内容显示到状态栏下层,并使状态栏透明
   */
  public void setTransparentForWindow() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(Color.TRANSPARENT);
      // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
      getWindow().getDecorView()
          .setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }
}
