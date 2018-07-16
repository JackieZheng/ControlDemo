package com.example.mylibrary.base.mvp.fragment;

import android.os.Bundle;
import android.view.View;
import com.example.mylibrary.base.BaseFragment;
import com.example.mylibrary.base.mvp.presenter.BaseListPresenter;

/**
 * @param <P> 页面对应的Presenter
 */
public abstract class BaseMVPListFragment<P extends BaseListPresenter> extends MVPProgressFragment<P> {

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setEmptyViewClickListener(v -> mPresenter.obtainData(true));
    setNetWorkErrorViewClickListener(v -> mPresenter.obtainData(true));
  }

  @Override
  public BaseFragment getFragment() {
    return this;
  }
}
