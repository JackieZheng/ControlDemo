package com.example.mylibrary.base;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import timber.log.Timber;

import static com.trello.rxlifecycle2.android.FragmentEvent.DESTROY;

/**
 * 列表类型的{@link Fragment}封装
 * <pre>
 *   1. 提供数据请求前,请求中,请求成功,失败,对视图的基本切换,和页面状态的基本处理.
 *   2. 配合{@link EndlessScrollListener.IMore} 和 {@link EndlessScrollListener} 加载更多的处理逻辑
 *   使用场景
 *      需要有 加载中,数据为空,等状态显示的列表.
 *      如: 用户帖子列表 进入列表时 显示加载中,加载成功后显示列表数据,网络异常显示网络异常界面
 * </pre>
 *
 * @param <CONTAINER> 指定此Fragment在哪个Activity中打开,对直接嵌套在Activity中的Fragment有效
 * @param <ITEM> 列表数据类型
 * @param <DATA> {@link ApiResponse} 的泛型参数
 */
public abstract class ListFragment<CONTAINER extends BaseActivity, ITEM, DATA> extends ProgressFragment<CONTAINER>
  implements EndlessScrollListener.IMore {

  /** 是否正在加载 */
  protected boolean mIsLoading = false;
  /** 服务端是否还有更多数据 */
  protected boolean mHasMore = false;
  /** 是否调用出错 */
  protected boolean mIsError = false;
  /** 是否需要刷新(清空)列表数据 */
  private boolean mIsRefresh;
  /** 列表数据 */
  protected final List<ITEM> mData = new ArrayList<>();

  private PublishSubject<Boolean> mSubject = PublishSubject.create();

  @CallSuper
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    subscribe();
  }

  /**
   * 获取数据
   *
   * @param isRefresh 是否清空原来的数据
   */
  @CallSuper
  protected void obtainData(boolean isRefresh) {
    if (isLoading()) {// 如果正在加载,取消本次请求
      return;
    }
    mIsError = false;//加载正常
    mIsLoading = true;//加载中
    mIsRefresh = isRefresh; // 是否需要刷新(清空)数据

    if (hasData()) {// showContent(); // 如果列表已经有数据,此时内容视图一定是可见的,不需要调用{@link #showConten}
      if (mIsViewCreated) onStatusUpdated();
    } else {// 加载的时候还没有数据,显示加载进度视图
      showLoading();
    }
    mSubject.onNext(isRefresh); // 请求数据,每次都会调用{@link #getRequest}
  }

  /**
   * 数据请求 [状态变更后] 回调此方法,仅在列表有数据时候{@link #hasData()} == true时调用
   * <pre>
   * 主要用于更新列表数据状态
   * 1. 开始加载{@link #mIsLoading} == true
   * 2. 加载完成{@link #mIsLoading} == false
   * 3. 加载失败{@link #mIsLoading} == false && {@link #mIsError}  == true
   * </pre>
   */
  protected abstract void onStatusUpdated();

  /**
   * 服务调用成功
   *
   * @param data 服务端返回的数据
   * @param isRefresh 是否需要清空原来的数据
   */
  protected abstract void onSuccess(ApiResponse<DATA> data, boolean isRefresh);

  /**
   * 服务调用出错处理
   * <pre>
   *   错误提示,日志打印等可以在此方法处理
   * </pre>
   *
   * @param throwable {@link Throwable}
   */
  protected void onError(Throwable throwable) {
    Timber.e(throwable);
    Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
  }

  /**
   * 根据服务返回数据,检查服务端是否还有更多数据
   * <pre>
   * // 标准实现
   * public boolean checkHasMore(ApiResponse<List<String>> data) {
   *    // 服务调用失败 || 数据满一页 表示还有更多数据
   *    return !data.isSuccess() || !(data.getData() == null || data.getData().size() != PAGE_SIZE);
   * }
   * </pre>
   *
   * @param data {@link ApiResponse}
   * @return {@link Boolean} true还有更多数据
   */
  protected abstract boolean checkHasMore(ApiResponse<DATA> data);

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // 没有数据视图点击事件
    setEmptyViewClickListener(v -> obtainData(false));
    // 网络异常视图点击事件
    setNetWorkErrorViewClickListener(v -> obtainData(false));
  }

  /** 超时重试 */
  public static BiPredicate<Integer, Throwable> timeoutRetry() {
    return (integer, throwable) -> {
      Timber.w(throwable);
      return throwable instanceof SocketTimeoutException && integer < 3;
    };
  }

  @Override
  public boolean isLoading() {
    return mIsLoading;
  }

  @Override
  public boolean isError() {
    return mIsError;
  }

  @Override
  public boolean hasMore() {
    return mHasMore;
  }

  @Override
  public void loadMore() {
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

  @NonNull
  protected abstract Observable<ApiResponse<DATA>> getRequest(boolean isRefresh);

  private void subscribe() {
    mSubject.hide().compose(bindUntilEvent(DESTROY))//
      .switchMap(isRefresh -> { // 获取请求,并设置超时,重试策略
        // 这里会将 mSubject.onNext的参数传进getRequest
        return getRequest(isRefresh).retry(timeoutRetry());
      })//
      .subscribe(new Observer<ApiResponse<DATA>>() {
        @Override
        public void onSubscribe(Disposable disposable) {
          // 注意这个方法只在 mSubject.subscribe 的时候执行一次
        }

        @Override
        public void onNext(ApiResponse<DATA> data) {

          mIsError = !data.isSuccess();
          mIsLoading = false;
          boolean hasDataBefore = hasData();// 请求之前是否有数据

          mHasMore = checkHasMore(data);// 检查是否还有更多数据

          onSuccess(data, mIsRefresh);// 服务调用成功的回调

          if (hasData()) {// 有数据显示内容
            showContent();
          } else {//没数据显示数据为空视图
            showEmpty();// 没有数据
          }

          // 如果服务调用之前有数据,或者调用之后有数据(也就是数据有无或者数据数量有变化),都需要更新
          if (hasDataBefore || hasData()) {
            if (mIsViewCreated) {
              onStatusUpdated();
            }
          }
        }

        @Override
        public void onError(Throwable throwable) {
          subscribe(); // 报错会结束订阅所以重新订阅
          mIsError = true;//加载出错
          mIsLoading = false;//加载完成
          ListFragment.this.onError(throwable);//错误处理

          if (hasData()) {
            if (mIsViewCreated) onStatusUpdated();//加载时候发生异常,更新界面显示
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

        @Override
        public void onComplete() {

        }
      });
  }
}
