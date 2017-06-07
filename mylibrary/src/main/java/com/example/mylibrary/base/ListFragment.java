package com.example.mylibrary.base;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.mylibrary.EndlessScrollListener;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class ListFragment<T extends BaseActivity, D> extends ProgressFragment<T>
    implements EndlessScrollListener.IMore {

  /** 是否正在加载 */
  protected boolean mIsLoading = false;
  /** 服务端是否还有更多数据 */
  protected boolean mHasMore = false;
  /** 是否调用出错 */
  protected boolean mIsError = false;
  protected final List<String> mData = new ArrayList<>();

  /**
   * 获取数据
   *
   * @param isRefresh 是否清空原来的数据
   */
  protected abstract void obtainData(boolean isRefresh);

  /**
   * 数据请求状态(开始加载,加载完成,加载失败)变更后回调此方法
   */
  protected abstract void onStatusUpdated();

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

  /**
   * 服务调用前执行,注意设置在主线程执行
   * 这里已经判断了是否需要拉去数据,以及将请求缓存起来等等销毁视图时取消请求
   *
   * @return {@link Consumer}
   */
  public Consumer<Disposable> getDoOnSubscribe() {
    return new Consumer<Disposable>() {//主线程
      @Override public void accept(Disposable disposable) throws Exception {
        if (mIsLoading) {// 如果当前正在加载数据,那么取消本次请求
          disposable.dispose();
          return;
        }
        mIsError = false;//加载正常
        mIsLoading = true;//加载中
        if (hasData()) {// 如果列表已经有数据,此时内容视图一定是可见的,不需要调用{@link #showContent}
          if (isViewCreated) onStatusUpdated();
        } else {// 加载的时候还没有数据,显示加载进度视图
          showProgress();
        }
        mDisposables.add(disposable);
      }
    };
  }

  /**
   * 服务调用异常后执行
   *
   * @return {@link Consumer}
   */
  public Consumer<Throwable> getOnError() {
    return new Consumer<Throwable>() {
      @Override public void accept(Throwable throwable) throws Exception {
        Log.e(TAG, throwable.getClass().getSimpleName(), throwable);
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
        mIsError = true;//加载出错
        mIsLoading = false;//加载完成
        if (isViewCreated) {//加载时候发生异常,更新界面显示
          onStatusUpdated();
        }
        if (hasData()) return;// 有数据只弹出异常信息

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

  @Override public boolean isRefreshing() {
    return false;
  }

  @Override public boolean hasMore() {
    return mHasMore;
  }

  @Override public boolean isLoading() {
    return mIsLoading;
  }

  @Override public void loadMore() {
    obtainData(false);
  }

  /**
   * 是否有数据
   *
   * @return {@link Boolean}
   */
  protected boolean hasData() {
    return mData.size() > 0;
  }

  /**
   * 是否有异常
   *
   * @return {@link Boolean}
   */
  public boolean isError() {
    return mIsError;
  }
}
