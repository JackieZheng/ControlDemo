package com.example.mylibrary.base.mvp.view;

import com.example.mylibrary.base.ApiResponse;
import com.example.mylibrary.base.ListFragment;

/**
 * 列表模式的MVP模式View继承此类
 *
 * @param  <DATA> <pre>List<Result></pre>
 */
public interface IBaseMVPListView<DATA> extends IMVPProgressView {

  boolean hasData();

  boolean checkHasMore(ApiResponse<DATA> data);
  /**
   * 数据加载状态监听
   *
   * @param status {@link ListFragment.LoadingStatus}
   */
  void onStatusUpdated(ListFragment.LoadingStatus status);

  /**
   * 服务调用成功
   *
   * @param data 服务端返回的数据
   * @param isRefresh 是否需要清空原来的数据
   */
  void onSuccess(ApiResponse<DATA> data, boolean isRefresh);

  /**
   * 服务调用出错处理
   * <pre>
   *   错误提示,日志打印等可以在此方法处理
   * </pre>
   *
   * @param throwable {@link Throwable}
   */
  void onError(Throwable throwable);
}
