package com.example.mylibrary.base.mvp.presenter;

import com.example.mylibrary.base.mvp.view.IBaseMVPView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * 普通类型的MVP模式Presenter继承此类
 *
 * @param <T> {@link  IBaseMVPView} 的子类
 */
public abstract class BasePresenter<T extends IBaseMVPView> {

  /** Presenter 中持有对View的引用, MVP中的View */
  protected T mView;

  /** 提供RxJava生命周期绑定方法 */
  protected LifecycleProvider<FragmentEvent> mProvider;

  public BasePresenter(T view, LifecycleProvider<FragmentEvent> provider) {
    this.mView = view;
    this.mProvider = provider;
  }
}
