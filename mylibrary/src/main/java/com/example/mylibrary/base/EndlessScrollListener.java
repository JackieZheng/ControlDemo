package com.example.mylibrary.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

/**
 * {@link RecyclerView} 加载更多
 */
public class EndlessScrollListener extends OnScrollListener {
  /** 剩余多少项不可见时加载更多 */
  private int mUnVisibleItemsToLoadMore = 3;
  /** 是否可以加载更多 */
  private boolean canLoad = false;
  private EndlessScrollListener.IMore mMore;

  public EndlessScrollListener(EndlessScrollListener.IMore more) {
    this(3, more);
  }

  public EndlessScrollListener(int visibleThreshold, EndlessScrollListener.IMore more) {
    this.mUnVisibleItemsToLoadMore = visibleThreshold;
    if (this.mUnVisibleItemsToLoadMore <= 0) {
      this.mUnVisibleItemsToLoadMore = 3;
    }
    this.mMore = more;
  }

  @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
    super.onScrollStateChanged(recyclerView, newState);
    if (newState == RecyclerView.SCROLL_STATE_IDLE// 当前已经停止滚动
        && this.canLoad// 如果可以加载
        && this.mMore.hasMore()// 服务端还有更多数据
        && !this.mMore.isLoading() // 当前没有在请求服务
        && !this.mMore.isError()// 当前服务调用没有错误或者异常
        //&& !this.mMore.isRefreshing() // 能够加载更多(没有下拉刷新)
        && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()
        >= recyclerView.getAdapter().getItemCount()
        - this.mUnVisibleItemsToLoadMore)// 当前已经滚动到下面只要三项未显示
    {
      this.mMore.loadMore();
      //设置为false是为了防止加载下一页时出现了错误没有加载成功,这时候向上滑动列表又开始加载的情况,以及向下滑动列表也加载数据的情况
      canLoad = false;
    }
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);
    // 向下滑动不能加载更多
    canLoad = dy > 0;
  }

  public interface IMore {

    /**
     * 是否正在加载数据或{@link SwipeRefreshLayout}正在下拉刷新
     *
     * @return 请求过程中
     */
    boolean isLoading();

    /**
     * 是否有异常 {@link Throwable} 或加载出错(服务端异常)
     * <pre>
     *   如果有这样的需求,(服务端还有数据)每次滑动到只剩下三项未显示的时候,需要触发加载更多那么重载这个方法,返回true
     * </pre>
     *
     * @return {@link Boolean}
     */
    boolean isError();

    /**
     * 是否有更多数据
     *
     * @return 根据上一次请求返回的数据数目判断是否还有更多数据
     */
    boolean hasMore();

    /**
     * 加载更多
     */
    void loadMore();
  }
}
