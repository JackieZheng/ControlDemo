package com.example.mylibrary.base.mvp;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * list类型的MVP模式Presenter继承此类
 *
 * @param <T> 泛型参数,指定{@link #mView}的类型
 * eg: IMVPContract.IMVPView&lt;ApiResponse&lt;List&lt;Result&gt;&gt;&gt;
 */
public abstract class BaseListPresenter<T extends IListView> extends BasePresenter<T> {
  public BaseListPresenter(T view, LifecycleProvider<FragmentEvent> provider) {
    super(view, provider);
  }
}
