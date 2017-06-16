package com.example.mylibrary.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.trello.rxlifecycle2.components.support.RxFragment;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @param <CONTAINER> 泛型参数类型为<b>{@link BaseActivity}</b>或其子类
 * Fragment 对应的容器(对Activity的根布局Fragment有效,嵌套的Fragment无效)
 *
 * 这个泛型参数将指定使用哪个Activity作为Fragment的容器</br>
 * eg: {@link BaseActivity} 表示使用没有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] Activity   </br>
 * eg: {@link TopBarActivity} 表示使用带有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] Activity </br>
 * 当泛型参数为{@link TopBarActivity}时,需要重写 {@link BaseFragment#initTopBar(TopBarActivity)}方法修改标题栏状态栏
 */
public abstract class BaseFragment<CONTAINER extends BaseActivity> extends RxFragment {

  protected final String TAG = this.getClass().getSimpleName();
  /** Activity **/
  protected BaseActivity mActivity;
  /** 视图是否已经创建完成 */
  protected boolean mIsViewCreated = false;
  /** 缓存所有请求 */
  protected final CompositeDisposable mDisposables = new CompositeDisposable();

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = (BaseActivity) getActivity();
    // 如果泛型参数 CONTAINER 不是BaseActivity的子类,跑出异常
    if (mActivity == null) {
      throw new IllegalArgumentException("泛型参数类型必须为BaseActivity或其子类");
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // 如果是在带顶部栏的Activity中显示Fragment,那么调用初始化顶部栏方法
    if (mActivity instanceof TopBarActivity) {
      initTopBar((TopBarActivity) mActivity);
    }
    mIsViewCreated = true;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mIsViewCreated = false;
    mDisposables.clear();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mDisposables.dispose();
  }

  /**
   * 初始化状态栏,只要当Fragment的泛型参数为{@link TopBarActivity}时才会调用此方法
   *
   * @param activity {@link TopBarActivity}
   */
  public void initTopBar(TopBarActivity activity) {

  }
}
