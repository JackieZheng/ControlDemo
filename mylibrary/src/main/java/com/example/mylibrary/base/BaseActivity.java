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
import android.view.WindowManager;
import com.example.mylibrary.SystemBarConfig;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
  protected SystemBarConfig mSystemBarConfig;

  @Override public void onCreate(@Nullable Bundle savedInstanceState,
      @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
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
   * 隐藏系统UI
   * 可以在View.OnSystemUiVisibilityChangeListener监听这种改变
   *
   * View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY 是否在显示之后一段时间再次隐藏
   */
  @TargetApi(16) private void hideSystemUI() {
    int flag = 0;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_FULLSCREEN;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      flag = flag | View.SYSTEM_UI_FLAG_IMMERSIVE;
    }
    getWindow().getDecorView().setSystemUiVisibility(flag);
  }

  /**
   * 显示系统UI
   * 可以在View.OnSystemUiVisibilityChangeListener监听这种改变
   */
  @TargetApi(16) private void showSystemUI() {
    getWindow().getDecorView()
        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }
}
