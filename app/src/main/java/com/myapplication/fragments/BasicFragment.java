package com.myapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.mylibrary.base.ApiResponse;
import com.example.mylibrary.base.EndlessScrollListener;
import com.example.mylibrary.base.ListFragment;
import com.example.mylibrary.base.ProgressFragment;
import com.example.mylibrary.base.TopBarActivity;
import com.myapplication.R;
import com.myapplication.api.MockApi;
import com.myapplication.bean.Result;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.schedulers.Schedulers;
import java.net.SocketTimeoutException;
import java.util.List;
import timber.log.Timber;

/**
 * <pre>有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] 的Activity基本使用方式
 * 1. 继承{@link ListFragment}并指定泛型参数为{@link TopBarActivity}
 * 2. 重写 {@link #getContentLayoutResourceId} 方法,返回内容区域的布局文件,
 * 这个布局文件将嵌入到{@link ProgressFragment} 的内容区域
 * 3. 注意请不要重写{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)},
 * 如需要修改Fragment布局内容,请重写{@link #getEmptyLayoutResourceId()}方法.
 * </pre>
 */
public class BasicFragment extends ListFragment<TopBarActivity, Result, List<Result>> {

  private static final int PAGE_SIZE = 20;
  Unbinder mUnBinder;
  @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
  @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    obtainData(false);//请求数据,不清空原来数据
  }

  @Override
  public int getContentLayoutResourceId() {
    return R.layout.content_basic;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    //注意ProgressFragment的子类,只能在onViewCreated里面才能bind
    mUnBinder = ButterKnife.bind(this, view);

    mSwipeRefreshLayout.setOnRefreshListener(() -> obtainData(true));
    mSwipeRefreshLayout.setColorSchemeResources(//
      android.R.color.holo_blue_bright,//
      android.R.color.holo_green_light,//
      android.R.color.holo_orange_light,//
      android.R.color.holo_red_light);

    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecyclerView.setAdapter(new BasicAdapter(this, mData, this));
    mRecyclerView.addOnScrollListener(new EndlessScrollListener(this));
  }

  @Override
  protected void onEmptyViewInflated(@NonNull View emptyView) {
    super.onEmptyViewInflated(emptyView);
    TextView textView = emptyView.findViewById(R.id.data_empty_text);
    textView.setText("这里没有数据");
    textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_conn_no_network, 0, 0);
  }

  /**
   * 初始化状态栏,标题栏
   *
   * @param activity {@link TopBarActivity}
   */
  @Override
  public void initTopBar(TopBarActivity activity) {
    super.initTopBar(activity);
    Toolbar toolBar = activity.getToolBar();
    toolBar.setTitle("基本使用");
    toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolBar.setNavigationOnClickListener(v -> mActivity.finish());
    activity.getStatusBar().setBackgroundResource(R.color.primary);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnBinder.unbind();
  }

  @NonNull
  @Override
  protected Observable<ApiResponse<List<Result>>> getRequest(boolean isRefresh) {
    return MockApi.queryData(PAGE_SIZE)//
      //.retry(timeoutRetry())//
      .compose(bindUntilEvent(FragmentEvent.DESTROY))//
      .subscribeOn(Schedulers.io())// 指定在这行代码之前的subscribe在io线程执行
      //.doOnSubscribe(getDoOnSubscribe())//开始执行之前的准备工作
      //.subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
      .observeOn(AndroidSchedulers.mainThread());//指定这行代码之后的subscribe 在主线程执行
    //.subscribe(getOnNext(isRefresh), getOnError());
  }

  @Override
  public void onSuccess(ApiResponse<List<Result>> data, boolean isRefresh) {
    if (!data.isSuccess()) {// 服务端返回异常代码
      Toast.makeText(getContext(), data.getMessage(), Toast.LENGTH_LONG).show();
      return;
    }

    if (isRefresh) mData.clear();
    if (data.getData() != null && data.getData().size() > 0) {
      mData.addAll(data.getData());
    }
  }

  @Override
  public boolean checkHasMore(ApiResponse<List<Result>> data) {
    // 服务调用失败 || 数据满一页 表示还有更多数据
    return !data.isSuccess() || !(data.getData() == null || data.getData().size() != PAGE_SIZE);
  }

  @Override
  protected void onStatusUpdated() {
    mSwipeRefreshLayout.setRefreshing(isLoading());
    mRecyclerView.getAdapter().notifyDataSetChanged();
  }
}
