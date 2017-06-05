package com.example.mylibrary.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @param <T> 泛型参数类型为<b>{@link BaseActivity}</b>或其子类
 * 这个泛型参数将指定使用哪个Activity作为Fragment的容器</br>
 * eg: {@link BaseActivity} 表示使用没有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] Activity</br>
 * eg: {@link TopBarActivity} 表示使用带有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] Activity</br>
 */
public abstract class BaseFragment<T extends BaseActivity> extends Fragment {

  /** Activity **/
  protected BaseActivity mActivity;
  /** 视图是否已经创建完成 */
  protected boolean isViewCreated = false;
  /** 缓存所有请求 */
  protected CompositeDisposable mDisposables = new CompositeDisposable();

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = (BaseActivity) getActivity();
    if (mActivity == null) {
      throw new IllegalArgumentException("泛型参数类型不正确");
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    isViewCreated = true;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    isViewCreated = false;
    mDisposables.clear();
  }
}
