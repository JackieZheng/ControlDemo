package com.example.mylibrary.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.example.mylibrary.R;

/**
 * 基础Activity,此Activity不包含  [自定义的顶部栏(状态栏+标题栏+标题栏阴影)]
 */
public class BaseActivity extends AppCompatActivity {
  /** Fragment名字 */
  public static String FRAGMENT_CLASS_NAME = "fragment_class_name";
  /** 系统UI的相关属性 */
  protected SystemBarConfig mSystemBarConfig;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getContentResourceId());
    if (mSystemBarConfig == null) {
      mSystemBarConfig = new SystemBarConfig(this);
    }
    // 使用Fragment替换R.id.content
    if (getIntent() != null && getIntent().hasExtra(FRAGMENT_CLASS_NAME)) {
      String stringExtra = getIntent().getStringExtra(FRAGMENT_CLASS_NAME);
      if (TextUtils.isEmpty(stringExtra)) return;

      //Fragment fragment = this.getSupportFragmentManager().findFragmentByTag(stringExtra);
      if (savedInstanceState == null) {
        //if (fragment == null) {
        Fragment fragment = Fragment.instantiate(this, stringExtra, getIntent().getExtras());
        //}
        if (fragment != null) {
          getSupportFragmentManager().beginTransaction()
              .replace(R.id.content, fragment, stringExtra)
              .commit();
        }
      }
      //else {
      // 如果是 show() hide()模式,那么需要重新设置,以免重叠
      //Fragment fragment = this.getSupportFragmentManager().findFragmentByTag(stringExtra);
      //}
    }
  }

  /**
   * 返回Activity布局文件{@link R.layout#activity_base;}
   *
   * @return {@link LayoutRes}
   */
  public @LayoutRes int getContentResourceId() {
    return R.layout.activity_base;
  }

  /**
   * 获取系统UI配置信息
   *
   * @return {@link SystemBarConfig}
   */
  public SystemBarConfig getSystemBarConfig() {
    if (mSystemBarConfig == null) {
      synchronized (this) {
        if (mSystemBarConfig == null) {
          mSystemBarConfig = new SystemBarConfig(this);
        }
      }
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
   * {@link Build.VERSION_CODES#KITKAT}以上系统调用此方法,可以是状态栏透明,
   * 并使得Activity内容显示在状态栏下层,内容被状态栏覆盖
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

  public void clearTransparentForWindow() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(Color.TRANSPARENT);
      getWindow().getDecorView()
          .setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
              & ~(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN));
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }
}
