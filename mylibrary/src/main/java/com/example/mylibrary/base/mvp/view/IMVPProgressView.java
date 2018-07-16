package com.example.mylibrary.base.mvp.view;

import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Progress 模式View继承此类
 */
public interface IMVPProgressView extends IBaseMVPView {

  /**
   * {@link Fragment} content layout
   *
   * @return @LayoutRes(eg R.layout.content_user_info)
   */
  @LayoutRes
  int getContentLayoutResourceId();

  /**
   * {@link Fragment} emptyView layout
   *
   * @return @LayoutRes(eg R.layout.default_empty_layout)
   */
  @LayoutRes
  int getEmptyLayoutResourceId();

  /**
   * {@link Fragment} loading layout
   *
   * @return @LayoutRes(eg R.layout.default_progress_layout)
   */
  @LayoutRes
  int getLoadingLayoutResourceId();

  /**
   * {@link Fragment} network error layout
   *
   * @return @LayoutRes(eg R.layout.default_network_error_layout)
   */
  @LayoutRes
  int getNetWorkErrorResourceId();

  void setContentView(@LayoutRes int layoutResId);

  void setContentView(View view);

  /** 显示内容 */
  void showContent();

  /** 显示进度 */
  void showLoading();

  /** 显示空视图 */
  void showEmpty();

  /** 显示网络错误 */
  void showNetWorkError();

  /**
   * 设置空视图点击事件
   *
   * @param listener {@link View.OnClickListener}
   */
  void setEmptyViewClickListener(View.OnClickListener listener);

  /**
   * 设置网络异常点击事件
   *
   * @param listener {@link View.OnClickListener}
   */
  void setNetWorkErrorViewClickListener(View.OnClickListener listener);
}
