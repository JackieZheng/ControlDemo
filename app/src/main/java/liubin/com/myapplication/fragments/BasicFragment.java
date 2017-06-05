package liubin.com.myapplication.fragments;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.mylibrary.base.ProgressFragment;
import com.example.mylibrary.base.TopBarActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import liubin.com.myapplication.CheeseListFragment;
import liubin.com.myapplication.R;
import liubin.com.myapplication.api.CustomerApi;
import liubin.com.myapplication.api.TestApi;
import liubin.com.myapplication.bean.BaseModel;

/**
 * <pre>有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] 的Activity基本使用方式
 * 1. 继承{@link ProgressFragment}并指定泛型参数为{@link TopBarActivity}
 * 2. 重写 {@link #getFragmentContentLayoutResourceID} 方法,返回内容区域的布局文件,
 * 这个布局文件将嵌入到{@link ProgressFragment} 的内容区域
 * 3. 注意请不要重写{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)},
 * 如需要修改Fragment布局内容,请重写{@link #getFragmentLayoutResourceID()}方法.
 * </pre>
 */
public class BasicFragment extends ProgressFragment<TopBarActivity>
    implements EndlessScrollListener.IMore {

  Unbinder unbinder;
  @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
  @BindView(R.id.swip) SwipeRefreshLayout mSwipeRefreshLayout;

  private boolean isLoading = false;
  private final List<String> mData = new ArrayList<>();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    obtainData(false);
  }

  @Override public int getFragmentContentLayoutResourceID() {
    return R.layout.content_basic;
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);

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

    // 修改Activity中的标题栏
    if (mActivity instanceof TopBarActivity) {
      Toolbar toolBar = ((TopBarActivity) mActivity).getToolBar();
      toolBar.setTitle("基本测试");
      toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
      toolBar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mActivity.finish();
        }
      });
    }

    mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
        android.R.color.holo_green_light, android.R.color.holo_orange_light,
        android.R.color.holo_red_light);
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        obtainData(true);
      }
    });
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecyclerView.setAdapter(
        new CheeseListFragment.SimpleStringRecyclerViewAdapter(getActivity(), mData));
    mRecyclerView.addOnScrollListener(new EndlessScrollListener(this));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  /**
   * refresh
   *
   * @param refresh 是否刷新
   */
  private void obtainData(final boolean refresh) {
    CustomerApi.queryData(20)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe(new Consumer<Disposable>() {
          @Override public void accept(Disposable disposable) throws Exception {
            if (isLoading) {// 如果当前正在加载数据,那么取消本次请求
              disposable.dispose();
              return;
            }
            isLoading = true;
            if (mData.size() == 0) {// 没有数据,加载数据时,切换到列表加载视图
              showProgress();
            } else {// 有数据,显示SwipeRefreshLayout的加载圈圈
              mSwipeRefreshLayout.setRefreshing(true);
            }
            mDisposables.add(disposable);
          }
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<String>>() {
          @Override public void accept(List<String> datas) throws Exception {
            isLoading = false;
            mSwipeRefreshLayout.setRefreshing(false);
            if (datas != null) {
              if (refresh) {
                mData.clear();
              }
              mData.addAll(datas);
            }
            if (mData.size() == 0) {
              showEmpty();// 没有数据
              setEmptyMessage("这里没有数据", R.drawable.ic_conn_no_network);
              return;
            }
            showContent();
            mRecyclerView.getAdapter().notifyDataSetChanged();
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {
            isLoading = false;
            mSwipeRefreshLayout.setRefreshing(false);
            Log.e("BasicFragment", "Throwable", throwable);
            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            if (mData.size() == 0) {// 有数据只弹出异常信息
              if (throwable instanceof NetworkErrorException) {
                showNetWorkError();
                return;
              }
              showEmpty();// 没有数据
              setEmptyMessage("这里没有数据", R.drawable.ic_conn_no_network);
            }
          }
        });
  }

  @Override public boolean isRefreshing() {
    return mSwipeRefreshLayout.isRefreshing();
  }

  @Override public boolean hasMore() {
    return mData.size() % 20 == 0;
  }

  @Override public boolean isLoading() {
    return isLoading;
  }

  @Override public void loadMore() {
    obtainData(false);
  }

  private void testApi() {
    Disposable subscribe = ApiClient.create(TestApi.class)
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
