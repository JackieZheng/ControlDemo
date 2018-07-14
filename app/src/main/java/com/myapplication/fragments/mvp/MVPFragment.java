package com.myapplication.fragments.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.mylibrary.base.ApiResponse;
import com.example.mylibrary.base.EndlessScrollListener;
import com.example.mylibrary.base.ListFragment;
import com.example.mylibrary.base.TopBarActivity;
import com.example.mylibrary.base.mvp.BaseListMVPFragment;
import com.myapplication.R;
import com.myapplication.api.MockApi;
import com.myapplication.bean.Result;
import com.myapplication.fragments.BasicAdapter;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class MVPFragment extends BaseListMVPFragment<List<Result>, IMVPContract.IMVPPresenter>
  implements IMVPContract.IMVPView<ApiResponse<List<Result>>> {
  private static final int PAGE_SIZE = 20;
  Unbinder mUnBinder;

  private final List<Result> mData = new ArrayList<>();

  @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
  @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    obtainData(false);// 请求数据
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // 注意ProgressFragment的子类,只能在onViewCreated里面才能bind
    mUnBinder = ButterKnife.bind(this, view);

    // 初始化下拉刷新
    mSwipeRefreshLayout.setColorSchemeResources(//
      android.R.color.holo_blue_bright,//
      android.R.color.holo_green_light,//
      android.R.color.holo_orange_light,//
      android.R.color.holo_red_light);
    mSwipeRefreshLayout.setOnRefreshListener(() -> obtainData(true));

    // 初始化列表
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

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnBinder.unbind();
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
    toolBar.setTitle("MVP基本使用");
    toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolBar.setNavigationOnClickListener(v -> mActivity.finish());
  }

  @Override
  public int getContentLayoutResourceId() {
    return R.layout.content_mvp;
  }

  @Override
  protected IMVPContract.IMVPPresenter initPresenter() {
    return new MVPPresenter(this, this);
  }

  @Override
  public void obtainData(boolean isRefresh) {
    super.obtainData(isRefresh);
    mPresenter.loadData(PAGE_SIZE, isRefresh);
  }

  @NonNull
  @Override
  public Observable<ApiResponse<List<Result>>> getRequest(boolean isRefresh) {
    return MockApi.queryData(PAGE_SIZE)//
      //.retry(timeoutRetry())//
      .compose(bindUntilEvent(FragmentEvent.DESTROY))//生命周期绑定
      //.doOnNext(mView.getDoOnNext(pageSize))//
      .subscribeOn(Schedulers.io())// 指定在这行代码之前的subscribe在io线程执行
      //.doOnSubscribe(getDoOnSubscribe())//开始执行之前的准备工作
      //.subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
      .observeOn(AndroidSchedulers.mainThread());//指定这行代码之后的subscribe在io线程执行
    //.subscribe(getOnNext(isRefresh), getOnError());
  }

  @Override
  public boolean checkHasMore(ApiResponse<List<Result>> data) {
    // 服务调用失败 || 数据满一页 表示还有更多数据
    return !data.isSuccess() || !(data.getData() == null || data.getData().size() != PAGE_SIZE);
  }

  @Override
  public void onStatusUpdated(ListFragment.LoadingStatus status) {
    mSwipeRefreshLayout.setRefreshing(isLoading());
    mRecyclerView.getAdapter().notifyDataSetChanged();
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
  protected void onError(Throwable throwable) {
    super.onError(throwable);
  }

  @Override
  protected boolean hasData() {
    return mData.size() > 0;
  }
}
