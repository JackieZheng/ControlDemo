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
import com.example.mylibrary.base.BaseFragment;
import com.example.mylibrary.base.EndlessScrollListener;
import com.example.mylibrary.base.ListFragment;
import com.example.mylibrary.base.TopBarActivity;
import com.example.mylibrary.base.mvp.fragment.BaseMVPListFragment;
import com.myapplication.R;
import com.myapplication.bean.Result;
import com.myapplication.fragments.BasicAdapter;
import com.myapplication.fragments.mvp.IListMVPContract.IListMVPPresenter;
import com.myapplication.fragments.mvp.IListMVPContract.IListMVPView;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

import static com.myapplication.fragments.mvp.ListMVPPresenter.PAGE_SIZE;

public class ListMVPFragment extends BaseMVPListFragment<IListMVPPresenter> implements IListMVPView<List<Result>> {
  Unbinder mUnBinder;
  @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
  @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

  private final List<Result> mData = new ArrayList<>();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPresenter.obtainData(false);// 请求数据
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
    mSwipeRefreshLayout.setOnRefreshListener(() -> mPresenter.obtainData(true));

    // 初始化列表
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecyclerView.setAdapter(new BasicAdapter(this, mData, mPresenter));
    mRecyclerView.addOnScrollListener(new EndlessScrollListener(mPresenter));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnBinder.unbind();
  }

  @Override
  public int getContentLayoutResourceId() {
    return R.layout.content_mvp;
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
    toolBar.setTitle("MVP基本使用");
    toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolBar.setNavigationOnClickListener(v -> mActivity.finish());
  }

  @Override
  protected IListMVPPresenter initPresenter() {
    return new ListMVPPresenter(this, this);
  }

  @Override
  public void onStatusUpdated(ListFragment.LoadingStatus status) {
    // mSwipeRefreshLayout.setRefreshing(isLoading());
    mSwipeRefreshLayout.setRefreshing(status == ListFragment.LoadingStatus.LOADING);
    //更新foot
    mRecyclerView.getAdapter().notifyItemChanged(mRecyclerView.getAdapter().getItemCount() - 1);
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
    mRecyclerView.getAdapter().notifyDataSetChanged();
  }

  @Override
  public void onError(Throwable throwable) {
    Timber.e(throwable);
    Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
  }

  @Override
  public boolean hasData() {
    return mData.size() > 0;
  }

  public boolean checkHasMore(ApiResponse<List<Result>> data) {
    // 服务调用失败 || 数据满一页 表示还有更多数据
    return !data.isSuccess() || !(data.getData() == null || data.getData().size() != PAGE_SIZE);
  }
}
