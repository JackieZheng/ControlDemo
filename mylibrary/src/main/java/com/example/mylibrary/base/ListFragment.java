package com.example.mylibrary.base;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * 列表类型的Fragment继承此类
 *
 * @param <T> 指定此Fragment在哪个Activity中打开,对直接嵌套在Activity中的Fragment有效
 * @param <D> 列表数据类型
 */
public abstract class ListFragment<T extends BaseActivity, D, PD extends IModel>
    extends ProgressFragment<T> implements EndlessScrollListener.IMore {

  /** 是否正在加载 */
  protected boolean mIsLoading = false;
  /** 服务端是否还有更多数据 */
  protected boolean mHasMore = false;
  /** 是否调用出错 */
  protected boolean mIsError = false;
  /** 列表数据 */
  protected final List<D> mData = new ArrayList<>();

  /**
   * 获取数据
   *
   * @param isRefresh 是否清空原来的数据
   */
  protected abstract void obtainData(boolean isRefresh);

  /**
   * 数据请求 [状态变更后] 回调此方法,仅在列表有数据时候{@link #hasData()} == true时调用
   *
   * 1. 开始加载{@link #mIsLoading} == true
   * 2. 加载完成{@link #mIsLoading} == false
   * 3. 加载失败{@link #mIsLoading} == false && {@link #isError()}  == false
   */
  protected abstract void onStatusUpdated();

  /**
   * 服务调用成功
   *
   * @param data 服务端返回的数据
   * @param isRefresh 是否需要清空原来的数据
   */
  protected abstract void onSuccess(PD data, boolean isRefresh);

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // 没有数据视图点击事件
    setEmptyViewClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        obtainData(false);
      }
    });
    // 网络异常视图点击事件
    setNetWorkErrorViewClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        obtainData(false);
      }
    });
  }

  //@Override public boolean isRefreshing() {
  //  return false;
  //}

  @Override public boolean isLoading() {
    return mIsLoading;
  }

  @Override public boolean hasMore() {
    return mHasMore;
  }

  @Override public void loadMore() {
    obtainData(false);
  }

  /**
   * 是否有数据,有数据才会显示内容视图
   *
   * @return {@link Boolean} 是否有数据
   */
  protected boolean hasData() {
    return mData.size() > 0;
  }

  /**
   * 是否有异常 {@link Throwable}
   *
   * @return {@link Boolean}
   */
  public boolean isError() {
    return mIsError;
  }

  /**
   * 调用后台服务之前执行的一些准备工作.
   * <pre>
   * 1. 如果正在加载,取消请求
   * 2. 更新当前状态 {@link #mIsLoading } = true {@link #mIsError} = false
   * 3. 将请求与Fragment的生命周期绑定 mDisposables.add(disposable)
   *
   * 使用方法:
   *  在服务调用处理中加入如下处理逻辑
   *  .doOnSubscribe(getDoOnSubscribe())//开始执行之前的准备工作
   *  .subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
   * </pre>
   *
   * @return {@link Consumer}
   */
  public final Consumer<Disposable> getDoOnSubscribe() {
    return new Consumer<Disposable>() {//主线程
      @Override public void accept(Disposable disposable) throws Exception {
        if (isLoading()) {// 如果正在加载,取消本次请求
          disposable.dispose();
          return;
        }
        mIsError = false;//加载正常
        mIsLoading = true;//加载中
        if (hasData()) {// 如果列表已经有数据,此时内容视图一定是可见的,不需要调用{@link #showConten}
          if (isViewCreated) onStatusUpdated();
        } else {// 加载的时候还没有数据,显示加载进度视图
          showProgress();
        }
        mDisposables.add(disposable);
      }
    };
  }

  /**
   * 服务端调用正常
   *
   * @param isRefresh 是否需要清空原来的数据
   * <pre>
   * 1. 更新状态 {@link #mIsLoading} = false ,{@link #mIsError} = false
   * 2. 服务调用成功后的回调 {@link #onSuccess(IModel, boolean)}
   * 3. 状态更新后的回调方法 {@link #onStatusUpdated()}
   * </pre>
   * @return {@link Consumer}
   */
  public final Consumer<PD> getOnNext(final boolean isRefresh) {
    return new Consumer<PD>() {
      @Override public void accept(PD data) throws Exception {
        mIsError = false;
        mIsLoading = false;
        boolean hasDataBefore = hasData();

        // 服务调用成功的回调
        onSuccess(data, isRefresh);
        if (hasData()) {// 有数据显示内容
          showContent();
        } else {//没数据显示数据为空视图
          showEmpty();// 没有数据
        }

        // 如果服务调用之前有数据,或者调用之后有数据(也就是数据有无或者数据数量有变化),都需要更新
        if (hasDataBefore || hasData()) {
          if (isViewCreated) {
            onStatusUpdated();
          }
        }
      }
    };
  }

  /**
   * 服务调用异常处理
   *
   * @return {@link Consumer}
   */
  public final Consumer<Throwable> getOnError() {
    return new Consumer<Throwable>() {
      @Override public void accept(Throwable throwable) throws Exception {
        Log.e(TAG, throwable.getClass().getSimpleName(), throwable);
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
        mIsError = true;//加载出错
        mIsLoading = false;//加载完成

        if (hasData()) {
          if (isViewCreated) onStatusUpdated();//加载时候发生异常,更新界面显示
          return;// 有数据只弹出异常信息
        }

        // 网络异常显示,网络异常视图
        if (throwable instanceof NetworkErrorException
            || throwable instanceof TimeoutException
            || throwable instanceof IOException) {
          showNetWorkError();
        } else {// 没有数据,但是存编码问题抛出异常
          showEmpty(); // 普通异常显示空数据视图
        }
      }
    };
  }
}
