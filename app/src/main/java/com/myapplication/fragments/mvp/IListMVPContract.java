package com.myapplication.fragments.mvp;

import com.example.mylibrary.base.ApiResponse;
import com.example.mylibrary.base.mvp.presenter.BaseListPresenter;
import com.example.mylibrary.base.mvp.view.IBaseMVPListView;
import com.myapplication.bean.Result;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import java.util.List;

public interface IListMVPContract {
  /**
   * @param <T> 泛型参数为 IBaseMVPListView 的子类
   */
  abstract class IListMVPPresenter<DATA, T extends IBaseMVPListView> extends BaseListPresenter<DATA, T> {

    public IListMVPPresenter(T view, LifecycleProvider<FragmentEvent> provider) {
      super(view, provider);
    }
  }

  /**
   * @param <T> 泛型参数,后台服务对应的响应数据结构 eg:<b> ApiResponse&lt;List&lt;User&gt;&gt;</b>
   */
  interface IListMVPView<T> extends IBaseMVPListView<T> {

  }
}
