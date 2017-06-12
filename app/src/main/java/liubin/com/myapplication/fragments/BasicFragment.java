package liubin.com.myapplication.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.mylibrary.ApiClient;
import com.example.mylibrary.EndlessScrollListener;
import com.example.mylibrary.base.ListFragment;
import com.example.mylibrary.base.ProgressFragment;
import com.example.mylibrary.base.TopBarActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import liubin.com.myapplication.R;
import liubin.com.myapplication.api.CustomerApi;
import liubin.com.myapplication.api.Api;
import liubin.com.myapplication.bean.BaseModel;
import liubin.com.myapplication.bean.StringData;

/**
 * <pre>有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] 的Activity基本使用方式
 * 1. 继承{@link ProgressFragment}并指定泛型参数为{@link TopBarActivity}
 * 2. 重写 {@link #getFragmentContentLayoutResourceID} 方法,返回内容区域的布局文件,
 * 这个布局文件将嵌入到{@link ProgressFragment} 的内容区域
 * 3. 注意请不要重写{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)},
 * 如需要修改Fragment布局内容,请重写{@link #getFragmentLayoutResourceID()}方法.
 * </pre>
 */
public class BasicFragment extends ListFragment<TopBarActivity, String, StringData> {

  private static final int PAGE_SIZE = 20;
  Unbinder mUnBinder;
  @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
  @BindView(R.id.swip) SwipeRefreshLayout mSwipeRefreshLayout;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    obtainData(false);//请求数据,不清空原来数据
  }

  @Override public int getFragmentContentLayoutResourceID() {
    return R.layout.content_basic;
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mUnBinder = ButterKnife.bind(this, view);//注意ProgressFragment的子类,只能在onViewCreated里面才能bind

    mSwipeRefreshLayout.setColorSchemeResources(//
        android.R.color.holo_blue_bright,//
        android.R.color.holo_green_light,//
        android.R.color.holo_orange_light,//
        android.R.color.holo_red_light);
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        obtainData(true);
      }
    });

    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecyclerView.setAdapter(new BasicAdapter(getActivity(), mData, this));
    mRecyclerView.addOnScrollListener(new EndlessScrollListener(this));

    // 这一句可以在任何时候调用
    setEmptyMessage("这里没有数据", R.drawable.ic_conn_no_network);
  }

  /**
   * 初始化状态栏,标题栏
   *
   * @param activity {@link TopBarActivity}
   */
  @Override public void initTopBar(TopBarActivity activity) {
    super.initTopBar(activity);
    Toolbar toolBar = activity.getToolBar();
    toolBar.setTitle("基本使用");
    toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mActivity.finish();
      }
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mUnBinder.unbind();
  }

  /**
   * 请求数据
   *
   * @param isRefresh 是否清空原来的数据
   */
  public void obtainData(final boolean isRefresh) {
    CustomerApi.queryData(PAGE_SIZE)//
        .doOnNext(new Consumer<StringData>() {
          @Override public void accept(StringData data) throws Exception {
            //判断是否还有更多数据
            if (data != null && data.isSuccess()) {
              mHasMore = data.getData() != null && data.getData().size() == PAGE_SIZE;
            }
          }
        })// 服务端返回数据解析之后处理数据
        .subscribeOn(Schedulers.io())// 指定在这行代码之前的subscribe在io线程执行
        .doOnSubscribe(getDoOnSubscribe())//开始执行之前的准备工作
        .subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
        .observeOn(AndroidSchedulers.mainThread())//指定这行代码之后的subscribe在io线程执行
        .subscribe(getOnNext(isRefresh), getOnError());
  }

  @Override public void onSuccess(StringData data, boolean isRefresh) {
    if (!data.isSuccess()) {// 服务端返回异常代码
      Toast.makeText(getContext(), data.getMessage(), Toast.LENGTH_LONG).show();
      return;
    }

    if (isRefresh) mData.clear();
    if (data.getData() != null && data.getData().size() > 0) {
      mData.addAll(data.getData());
    }
  }

  @Override protected void onStatusUpdated() {
    mSwipeRefreshLayout.setRefreshing(isLoading());
    mRecyclerView.getAdapter().notifyDataSetChanged();
  }

  //@Override public boolean isRefreshing() {
  //  return mSwipeRefreshLayout.isRefreshing();
  //}

  private void testApi() {
    Disposable subscribe = ApiClient.create(Api.class)
        .getUser(1, 22)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe(new Consumer<Disposable>() {
          @Override public void accept(Disposable disposable) throws Exception {
            showProgress();
          }
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<BaseModel>() {
          @Override public void accept(BaseModel user) throws Exception {//onNext
            Toast.makeText(getContext(), "下一步", Toast.LENGTH_LONG).show();
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {//onError
            showEmpty();
            setEmptyMessage("这里没有数据", R.drawable.ic_conn_no_network);
            Toast.makeText(getContext(), "出错了", Toast.LENGTH_LONG).show();
          }
        }, new Action() {
          @Override public void run() throws Exception {//onComplete
            Toast.makeText(getContext(), "完成", Toast.LENGTH_LONG).show();
            showContent();
          }
        });
    mDisposables.add(subscribe);
  }
}
