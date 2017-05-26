package com.example.mylibrary.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

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
