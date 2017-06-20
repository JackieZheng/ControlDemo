package com.example.mylibrary.base.mvp;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 列表模式的MVP模式View继承此类
 *
 * @param <T> 后台服务对应的响应数据结构 ,如<b> ApiResponse&lt;List&lt;User&gt;&gt;</b>
 */
public interface IListMVPView<T> extends IMVPView {

  /**
   * 服务调用前的一些准备工作
   *
   * @return {@link Consumer}
   */
  Consumer<? super Disposable> getDoOnSubscribe();

  /**
   * 服务端调用正常
   *
   * @param isRefresh 是否清空原来的数据
   * @return {@link Consumer}
   */
  Consumer<T> getOnNext(boolean isRefresh);

  /**
   * 服务调用异常处理
   *
   * @return {@link Consumer}
   */
  Consumer<? super Throwable> getOnError();
}
