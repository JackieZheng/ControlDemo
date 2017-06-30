package com.myapplication.fragments.mvp;

import com.example.mylibrary.base.mvp.BaseListPresenter;
import com.example.mylibrary.base.mvp.IListView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

public interface IMVPContract {
  abstract class IMVPPresenter<T extends IListView> extends BaseListPresenter<T> {

    public IMVPPresenter(T view, LifecycleProvider<FragmentEvent> provider) {
      super(view, provider);
    }

    /**
     * 请求数据
     *
     * @param pageSize 每页数据数目
     * @param isRefresh 是否清空原有数据
     */
    abstract void loadData(int pageSize, final boolean isRefresh);
  }

  /**
   * @param <T> 泛型参数,后台服务对应的响应数据结构 eg:<b> ApiResponse&lt;List&lt;User&gt;&gt;</b>
   */
  interface IMVPView<T> extends IListView<T> {
  }
}
