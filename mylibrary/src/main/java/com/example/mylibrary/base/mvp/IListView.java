package com.example.mylibrary.base.mvp;

import com.example.mylibrary.base.ApiResponse;
import io.reactivex.Observable;

/**
 * 列表模式的MVP模式View继承此类
 *
 * @param <T> 后台服务对应的响应数据结构 ,eg:<b> ApiResponse&lt;List&lt;User&gt;&gt;</b>
 */
public interface IListView<T> extends IView {
  Observable<T> getRequest(boolean isRefresh);

  void obtainData(boolean isRefresh);
}
