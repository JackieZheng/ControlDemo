package com.example.mylibrary.base.mvp.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import com.example.mylibrary.base.BaseFragment;
import com.example.mylibrary.base.mvp.presenter.BasePresenter;

/**
 * @param <P> 页面对应的 Presenter
 */
public abstract class BaseMVPFragment<P extends BasePresenter> extends BaseFragment {
  protected P mPresenter;

  @CallSuper
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

}
