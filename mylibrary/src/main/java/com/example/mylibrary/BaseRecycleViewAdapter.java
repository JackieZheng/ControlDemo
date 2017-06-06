package com.example.mylibrary;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import java.util.Collection;
import java.util.List;

/**
 * 基础的{@link RecyclerView}数据适配器
 *
 * @param <T> {@link #mData}列表数据类型
 * @param <VH> {@link RecyclerView.ViewHolder}
 */
public abstract class BaseRecycleViewAdapter<T, VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  /** 底部布局 */
  protected final int ITEM_TYPE_FOOTER = 999;
  /** 数据 */
  protected final List<T> mData;

  /**
   * 构造函数
   *
   * @param data 数据
   */
  public BaseRecycleViewAdapter(@NonNull final List<T> data) {
    this.mData = data;
  }

  /**
   * 添加数据并更新列表显示
   *
   * @param data 数据
   */
  public void add(T data) {
    this.mData.add(data);
    this.notifyDataSetChanged();
  }

  /**
   * 在指定索引处添加数据
   *
   * @param index 索引
   * @param data 数据
   */
  public void add(int index, T data) {
    this.mData.add(index, data);
    this.notifyDataSetChanged();
  }

  /**
   * 添加一个集合到已有集合
   *
   * @param collection 集合
   */
  public void addAll(Collection<? extends T> collection) {
    if (collection != null) {
      this.mData.addAll(collection);
      this.notifyDataSetChanged();
    }
  }

  /**
   * 清空数据
   */
  public void clear() {
    this.mData.clear();
    this.notifyDataSetChanged();
  }

  /**
   * 移除某一项
   *
   * @param object 数据项
   */
  public void remove(T object) {
    this.mData.remove(object);
    this.notifyDataSetChanged();
  }

  /**
   * 移除某一项,注意是数据集合的某一项,有可能与{@link RecyclerView}不对应
   *
   * @param index 索引
   */
  public void remove(int index) {
    this.mData.remove(index);
    this.notifyDataSetChanged();
  }

  /**
   * 获取数据
   *
   * @return {@link #mData}
   */
  public List<T> getData() {
    return this.mData;
  }

  /**
   * 获取某一项数据,如果有类似Footer等非数据项, 那么必须重写次方法
   *
   * @param position 索引
   * @return 如果有类似Footer等非数据项, 那么必须重写次方法
   */
  public T getItem(int position) {
    return this.mData.get(position);
  }

  /**
   * 获取某一项的ID
   *
   * @param position 索引
   * @return 索引
   */
  public long getItemId(int position) {
    return (long) position;
  }

  /**
   * 获取数据条数,如果有类似Footer项那么必须重写次方法
   *
   * @return {@link RecyclerView}需要显示的数据数目
   */
  public int getItemCount() {
    return this.mData.size();
  }

  /**
   * 底部最后一项(加载更多,没有更多,加载中...)ViewHolder
   */
  public static class FootViewHolder extends RecyclerView.ViewHolder {
    /** 底部最后一项 */
    TextView mFootText;

    public FootViewHolder(View itemView) {
      super(itemView);
      this.mFootText = (TextView) itemView.findViewById(R.id.footer_text);
    }

    /**
     * 更新最后一项的状态
     *
     * @param mMore {@link EndlessScrollListener.IMore}
     */
    public void setFootView(final EndlessScrollListener.IMore mMore) {
      if (mMore.isLoading() || mMore.isRefreshing()) {//正在加载或刷新
        mFootText.setText("加载中...");
        mFootText.setOnClickListener(null);
      } else if (mMore.hasMore()) {//有更多,但是上一次加载更多出错
        mFootText.setText("点击加载更多");
        mFootText.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            mMore.loadMore();
          }
        });
      } else {//没有更多数据
        mFootText.setText("没有更多了");
        mFootText.setOnClickListener(null);
      }
    }
  }
}
