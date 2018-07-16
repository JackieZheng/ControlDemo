package com.example.mylibrary.base.mvp.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.example.mylibrary.R;
import com.example.mylibrary.base.ProgressFragment;
import com.example.mylibrary.base.mvp.presenter.BasePresenter;
import com.example.mylibrary.base.mvp.view.IMVPProgressView;

/**
 * @param <P> 页面对应的 Presenter
 */
public abstract class MVPProgressFragment<P extends BasePresenter> extends ProgressFragment implements IMVPProgressView {
  protected P mPresenter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPresenter = initPresenter();
  }

  /**
   * 初始化Presenter
   *
   * @return {@link P}
   */
  protected abstract P initPresenter();

  /**
   * {@link Fragment} emptyView layout
   *
   * @return @LayoutRes(eg R.layout.default_empty_layout)
   */
  @LayoutRes
  public int getEmptyLayoutResourceId() {
    return R.layout.default_empty_layout;
  }

  /**
   * {@link Fragment} loading layout
   *
   * @return @LayoutRes(eg R.layout.default_progress_layout)
   */
  @LayoutRes
  public int getLoadingLayoutResourceId() {
    return R.layout.default_progress_layout;
  }

  /**
   * {@link Fragment} network error layout
   *
   * @return @LayoutRes(eg R.layout.default_network_error_layout)
   */
  @LayoutRes
  public int getNetWorkErrorResourceId() {
    return R.layout.default_network_error_layout;
  }
}
