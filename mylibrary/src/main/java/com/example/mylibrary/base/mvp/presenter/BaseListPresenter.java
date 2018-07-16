package com.example.mylibrary.base.mvp.presenter;

import android.accounts.NetworkErrorException;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.example.mylibrary.base.ApiResponse;
import com.example.mylibrary.base.EndlessScrollListener;
import com.example.mylibrary.base.ListFragment;
import com.example.mylibrary.base.mvp.view.IBaseMVPListView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.subjects.PublishSubject;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;
import timber.log.Timber;

import static com.trello.rxlifecycle2.android.FragmentEvent.DESTROY;

/**
 * list类型的MVP模式Presenter继承此类
 *
 * @param <T> 泛型参数,指定{@link #mView}的类型
 * eg: IMVPContract.IMVPView&lt;ApiResponse&lt;List&lt;Result&gt;&gt;&gt;
 */
public abstract class BaseListPresenter<DATA, T extends IBaseMVPListView> extends BasePresenter<T>
  implements EndlessScrollListener.IMore {

  /** 是否正在加载 */
  protected boolean mIsLoading = false;
  /** 服务端是否还有更多数据 */
  protected boolean mHasMore = false;
  /** 是否调用出错 */
  protected boolean mIsError = false;
  /** 是否需要刷新(清空)列表数据 */
  private boolean mIsRefresh;

  private final MutableLiveData<ListFragment.LoadingStatus> mLoadingStatus = new MutableLiveData<>();
  private final PublishSubject<Boolean> mSubject = PublishSubject.create();

  public BaseListPresenter(T view, LifecycleProvider<FragmentEvent> provider) {
    super(view, provider);
    subscribe();
    mLoadingStatus.observe(view.getFragment(), status -> {
      if (status != null) {
        mView.onStatusUpdated(status);
      }
    });
  }

  /**
   * 获取数据
   *
   * @param isRefresh 是否清空原来的数据
   */
  @CallSuper
  public void obtainData(boolean isRefresh) {
    if (isLoading()) return; // 如果正在加载,取消本次请求

    mIsError = false; // 加载正常
    mIsLoading = true; // 加载中
    mIsRefresh = isRefresh; // 是否需要刷新(清空)数据

    if (!mView.hasData()) mView.showLoading(); // 加载的时候还没有数据,显示加载进度视图
    mSubject.onNext(isRefresh); // 请求数据,每次都会调用{@link #getRequest}
    mLoadingStatus.postValue(ListFragment.LoadingStatus.LOADING); // 状态更新
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

  private void subscribe() {
    mSubject.hide().compose(mProvider.bindUntilEvent(DESTROY))//
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
          mIsLoading = false; // 是否加载中
          mIsError = !data.isSuccess(); // 是否错误
          mHasMore = mView.checkHasMore(data);// 检查是否还有更多数据

          mView.onSuccess(data, mIsRefresh);// 服务调用成功的回调

          if (mView.hasData()) {// 有数据显示内容
            mView.showContent();
          } else {//没数据显示数据为空视图
            mView.showEmpty();// 没有数据
          }
          mLoadingStatus.postValue(ListFragment.LoadingStatus.SUCCESS);
        }

        @Override
        public void onError(Throwable throwable) {
          subscribe(); // 报错会结束订阅所以重新订阅
          mIsError = true; // 加载出错
          mIsLoading = false; // 加载完成
          mHasMore = true; // 加载出错认为还有跟多数据

          mView.onError(throwable); // 错误处理
          mLoadingStatus.postValue(ListFragment.LoadingStatus.ERROR);

          if (mView.hasData()) return; // 有数据只弹出异常信息

          // 网络异常显示,网络异常视图
          if (throwable instanceof NetworkErrorException
            || throwable instanceof SocketTimeoutException
            || throwable instanceof TimeoutException
            || throwable instanceof IOException) {
            mView.showNetWorkError();
          } else {// 没有数据,但是存编码问题抛出异常
            mView.showEmpty(); // 普通异常显示空数据视图
          }
        }

        @Override
        public void onComplete() {
        }
      });
  }

  /**
   * 构造请求
   *
   * @param isRefresh 是否需要刷新(清空)数据
   * @return ..
   */
  @NonNull
  protected abstract Observable<ApiResponse<DATA>> getRequest(boolean isRefresh);
}
