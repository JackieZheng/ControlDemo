package com.example.mylibrary.base.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.example.mylibrary.base.ApiResponse;
import com.example.mylibrary.base.BaseActivity;
import com.example.mylibrary.base.ListFragment;

/**
 * @param <CONTAINER> Activity容器类型
 * @param <ITEM> 列表对应的数据类型
 * @param <DATA> {@link ApiResponse} 的泛型参数 如<b> ApiResponse&lt;List&lt;User&gt;&gt;</b>
 * 则&lt;DATA&gt;
 * 为List&lt;User&gt;
 * @param <P> 页面对应的Presenter
 */
public abstract class BaseListMVPFragment<CONTAINER extends BaseActivity, ITEM, DATA, P extends BaseListPresenter>
    extends ListFragment<CONTAINER, ITEM, DATA> {

  protected P mPresenter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
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
