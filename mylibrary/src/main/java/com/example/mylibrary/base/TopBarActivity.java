package com.example.mylibrary.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import com.example.mylibrary.R;

/**
 * 包含 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] 的Activity, 一般页面使用的Activity
 */
public class TopBarActivity extends BaseActivity {
  View mStatusBar;
  Toolbar mToolBar;
  View mToolBarShadow;
  ViewGroup mToolBarContainer;
  ViewGroup mTopBar;
  ViewStub mStubTopBar;

  private ViewGroup mContentContainer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContentContainer = (ViewGroup) findViewById(R.id.content);
    setTransparentForWindow();
    setTopBarOverlay(false);
    //UiUtils.requestStatusBarLight(this, true);
  }

  /**
   * Activity布局文件布局文件
   * 请参考{@link R.layout#activity_common}
   *
   * @return 布局文件ID
   */
  public @LayoutRes int getContentResourceId() {
    return R.layout.activity_common;
  }

  /**
   * 获取自定义状态栏,可以对状态栏,设置背景色,隐藏等操作
   *
   * @return {@link View}
   */
  public View getStatusBar() {
    ensureTopBar();
    return mStatusBar;
  }

  /**
   * 获取状态栏
   *
   * @return {@link Toolbar}
   */
  public Toolbar getToolBar() {
    ensureTopBar();
    return mToolBar;
  }

  /**
   * 获取 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)]
   *
   * @return {@link ViewGroup}
   */
  public ViewGroup getTopBar() {
    ensureTopBar();
    return mTopBar;
  }

  /**
   * 获取标题栏容器
   *
   * @return {@link ViewGroup}
   */
  public ViewGroup getToolBarContainer() {
    ensureTopBar();
    return mToolBarContainer;
  }

  /**
   * 设置[自定义的顶部栏(状态栏+标题栏+标题栏阴影)]是否覆盖内容区域
   *
   * @param overlay true:覆盖
   */
  public void setTopBarOverlay(boolean overlay) {
    ensureTopBar();
    if (mContentContainer == null) {
      return;
    }
    if (overlay) {
      mContentContainer.setPadding(0, 0, 0, 0);
    } else {
      mContentContainer.setPadding(0,
          mSystemBarConfig.getStatusBarHeight() + mSystemBarConfig.getToolBarHeight(), 0, 0);
    }

    ViewGroup.LayoutParams params = mStatusBar.getLayoutParams();
    params.height = mSystemBarConfig.getStatusBarHeight();
    mStatusBar.requestLayout();
  }

  /**
   * 初始化 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)]
   */
  private void ensureTopBar() {
    if (mTopBar != null) {
      return;
    }

    mStubTopBar = (ViewStub) findViewById(R.id.top_bar_stub);

    mTopBar = (ViewGroup) mStubTopBar.inflate();
    mStatusBar = mTopBar.findViewById(R.id.status_bar);

    mToolBarContainer = (ViewGroup) mTopBar.findViewById(R.id.tool_bar_container);
    mToolBar = (Toolbar) mToolBarContainer.findViewById(R.id.tool_bar);
    mToolBarShadow = mToolBarContainer.findViewById(R.id.tool_bar_shadow);

    // 使自定义状态栏,标题栏不覆盖内容区域
    setTopBarOverlay(false);
  }
}
