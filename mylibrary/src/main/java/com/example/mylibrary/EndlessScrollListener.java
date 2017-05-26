package com.example.mylibrary;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

/**
 * {@link RecyclerView} 加载更多
 */
public class EndlessScrollListener extends OnScrollListener {
  private int visibleThreshold;
  private EndlessScrollListener.IMore mMore;

  public EndlessScrollListener(EndlessScrollListener.IMore more) {
    this(3, more);
  }

  public EndlessScrollListener(int visibleThreshold, EndlessScrollListener.IMore more) {
    this.visibleThreshold = 3;
    this.visibleThreshold = visibleThreshold;
    this.mMore = more;
  }

  public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
    super.onScrollStateChanged(recyclerView, newState);
    if (newState == RecyclerView.SCROLL_STATE_IDLE// 当前已经停止滚动
        && this.mMore.hasMore()// 服务端还有更多数据
        && !this.mMore.isLoading() // 当前没有在请求服务
        && this.mMore.canLoadMore() // 能够加载更多(没有下拉刷新)
        && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()
        >= recyclerView.getAdapter().getItemCount() - this.visibleThreshold)// 当前已经滚动到下面只要三项未显示
    {
      this.mMore.loadMore();
    }
  }

  public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);
  }

  public interface IMore {
    /**
     * 是否能够加载更多,如{@link SwipeRefreshLayout}正在下拉刷新的时候不能加载更多
     */
    boolean canLoadMore();

    boolean canShow();

    /**
     * 是否有更多数据
     *
     * @return 根据上一次请求返回的数据数目判断是否还有更多数据
     */
    boolean hasMore();

    /**
     * 是否正在加载数据
     *
     * @return 请求过程中
     */
    boolean isLoading();

    /**
     * 加载更多
     */
    void loadMore();
  }
}
