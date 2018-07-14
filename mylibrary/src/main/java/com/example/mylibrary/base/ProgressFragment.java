/*
 * Copyright (C) 2013 Evgeny Shishkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mylibrary.base;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import com.example.mylibrary.R;

import static com.example.mylibrary.base.ProgressFragment.ViewType.CONTENT;
import static com.example.mylibrary.base.ProgressFragment.ViewType.EMPTY;
import static com.example.mylibrary.base.ProgressFragment.ViewType.ERROR;
import static com.example.mylibrary.base.ProgressFragment.ViewType.LOADING;

/**
 * 有状态切换的{@link Fragment}封装,继承自{@link BaseFragment}
 * <pre>
 *   1. 提供 加载中,数据为空,网络错误,内容,等状态切换显示.
 *   2. 提供内容区域自定义,加载中,数据为空,网络错误自定义接口.
 *   使用场景:
 *   需要通过网络请求返回数据后才能决定界面显示的情况.
 *   如:帖子数据列表,评论列表等.
 * </pre>
 *
 * @author toutoumu
 */

public abstract class ProgressFragment extends BaseFragment {
  /**
   * 视图类型: 内容,加载中,没有数据,网络异常
   */
  enum ViewType {
    CONTENT, LOADING, EMPTY, ERROR
  }

  // 视图切换之前的视图类型
  private ViewType mPreViewType = ViewType.CONTENT;
  // 当前视图类型(使用LiveData,避免 onViewCreated之前,onDestroy之后操作View)
  private final MutableLiveData<ViewType> mCurrentViewType = new MutableLiveData<>();

  private View mContentContainer;//内容区域容器

  private View mContentView;//内容视图
  private View mLoadingView;//加载中区域
  private View mEmptyView;//空区域
  private View mNetWorkErrorView;//网络异常视图

  private View mTempView;//临时保存创建的内容视图,在onViewCreated之后设置进去

  private ViewStub mLoadingStub;
  private ViewStub mEmptyStub;
  private ViewStub mNetWorkErrorStub;

  private View.OnClickListener mEmptyViewClickListener;
  private View.OnClickListener mNetWorkErrorViewClickListener;

  /**
   * {@link Fragment} content layout
   *
   * @return @LayoutRes(eg R.layout.content_user_info)
   */
  @LayoutRes
  abstract public int getContentLayoutResourceId();

  /**
   * {@link Fragment} emptyView layout
   *
   * @return @LayoutRes(eg R.layout.default_empty_layout)
   */
  @LayoutRes
  protected int getEmptyLayoutResourceId() {
    return R.layout.default_empty_layout;
  }

  /**
   * {@link Fragment} loading layout
   *
   * @return @LayoutRes(eg R.layout.default_progress_layout)
   */
  @LayoutRes
  protected int getLoadingLayoutResourceId() {
    return R.layout.default_progress_layout;
  }

  /**
   * {@link Fragment} network error layout
   *
   * @return @LayoutRes(eg R.layout.default_network_error_layout)
   */
  @LayoutRes
  protected int getNetWorkErrorResourceId() {
    return R.layout.default_network_error_layout;
  }

  /**
   * 当空视图被加载
   *
   * @param emptyView 默认的 emptyView 包含ID 为 data_empty_text 的TextView
   */
  protected void onEmptyViewInflated(@NonNull View emptyView) {
  }

  /**
   * 当加载中被加载
   *
   * @param loadingView loadingView
   */
  protected void onLoadingViewInflated(@NonNull View loadingView) {
  }

  /**
   * 当网络错误视图被加载
   *
   * @param errorView errorView
   */
  protected void onNewWorkErrorViewInflated(@NonNull View errorView) {
  }

  @CallSuper
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mCurrentViewType.setValue(ViewType.CONTENT);
    mCurrentViewType.observe(this, viewType -> {
      if (viewType == mPreViewType || viewType == null) {
        return;
      }
      View hideView = null; // 将要隐藏的View
      View showView = null; // 将要显示的View
      switch (mPreViewType) {
        case LOADING:
          hideView = getLoadingView();
          break;
        case CONTENT:
          hideView = mContentView;
          break;
        case EMPTY:
          hideView = getEmptyView();
          break;
        case ERROR:
          hideView = getNetWorkErrorView();
          break;
      }
      switch (viewType) {
        case CONTENT:
          showView = mContentView;
          break;
        case ERROR:
          showView = getNetWorkErrorView();
          break;
        case LOADING:
          showView = getLoadingView();
          break;
        case EMPTY:
          showView = getEmptyView();
          break;
      }
      switchView(showView, hideView, false);
    });
  }

  @CallSuper
  @Override
  public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mTempView = inflater.inflate(getContentLayoutResourceId(), null, false);
    return inflater.inflate(R.layout.fragment_progress, container, false);
  }

  @CallSuper
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ensureContent();
    setContentView(mTempView);
  }

  @CallSuper
  @Override
  public void onDestroyView() {
    mContentContainer = mLoadingView = mContentView = mEmptyView = mNetWorkErrorView = mTempView = null;
    mLoadingStub = mEmptyStub = mNetWorkErrorStub = null;
    super.onDestroyView();
  }

  public <T extends View> T findViewById(@IdRes int id) {
    return this.mContentView.findViewById(id);
  }

  /**
   * Set the content content from a layout resource.
   *
   * @param layoutResId Resource ID to be inflated.
   * @see #setContentView(View)
   * @see #getContentView()
   */
  public void setContentView(@LayoutRes int layoutResId) {
    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
    View contentView = layoutInflater.inflate(layoutResId, null);
    setContentView(contentView);
  }

  /**
   * Set the content mTempView to an explicit mTempView. If the content mTempView was installed
   * earlier,
   * the content will be replaced with a new mTempView.
   *
   * @param view The desired content to display. Value can't be null.
   * @see #setContentView(int)
   * @see #getContentView()
   */
  public void setContentView(View view) {
    ensureContent();
    if (view == null) {
      throw new IllegalArgumentException("Content mTempView can't be null");
    }
    if (mContentContainer instanceof ViewGroup) {
      ViewGroup contentContainer = (ViewGroup) mContentContainer;
      if (mContentView == null) {
        contentContainer.addView(view);
      } else {
        int index = contentContainer.indexOfChild(mContentView);
        // replace content mTempView
        contentContainer.removeView(mContentView);
        contentContainer.addView(view, index);
      }
      mContentView = view;
    } else {
      throw new IllegalStateException("Can't be used with a custom content mTempView");
    }
  }

  /** 显示内容 */
  public void showContent() {
    if (mCurrentViewType.getValue() != CONTENT) {
      mPreViewType = mCurrentViewType.getValue();
      mCurrentViewType.postValue(CONTENT);
    }
  }

  /** 显示进度 */
  public void showLoading() {
    if (mCurrentViewType.getValue() != LOADING) {
      mPreViewType = mCurrentViewType.getValue();
      mCurrentViewType.postValue(LOADING);
    }
  }

  /** 显示空视图 */
  public void showEmpty() {
    if (mCurrentViewType.getValue() != EMPTY) {
      mPreViewType = mCurrentViewType.getValue();
      mCurrentViewType.postValue(EMPTY);
    }
  }

  /** 显示网络错误 */
  public void showNetWorkError() {
    if (mCurrentViewType.getValue() != ERROR) {
      mPreViewType = mCurrentViewType.getValue();
      mCurrentViewType.postValue(ERROR);
    }
  }

  /**
   * 设置空视图点击事件
   *
   * @param listener {@link View.OnClickListener}
   */
  public void setEmptyViewClickListener(View.OnClickListener listener) {
    if (mEmptyView != null) {
      mEmptyView.setOnClickListener(listener);
    }
    mEmptyViewClickListener = listener;
  }

  /**
   * 设置网络异常点击事件
   *
   * @param listener {@link View.OnClickListener}
   */
  public void setNetWorkErrorViewClickListener(View.OnClickListener listener) {
    if (mNetWorkErrorView != null) {
      mNetWorkErrorView.setOnClickListener(listener);
    }
    mNetWorkErrorViewClickListener = listener;
  }

  /**
   * Return content mTempView or null if the content mTempView has not been initialized.
   *
   * @return content mTempView or null
   * @see #setContentView(View)
   * @see #setContentView(int)
   */
  private View getContentView() {
    return mContentView;
  }

  private View getEmptyView() {
    if (mEmptyView == null) {
      mEmptyStub.setLayoutResource(getEmptyLayoutResourceId());
      mEmptyView = mEmptyStub.inflate();
      mEmptyView.setOnClickListener(mEmptyViewClickListener);
      onEmptyViewInflated(mEmptyView);
    }
    return mEmptyView;
  }

  private View getLoadingView() {
    if (mLoadingView == null) {
      mLoadingStub.setLayoutResource(getLoadingLayoutResourceId());
      mLoadingView = mLoadingStub.inflate();
      onLoadingViewInflated(mLoadingView);
    }
    return mLoadingView;
  }

  private View getNetWorkErrorView() {
    if (mNetWorkErrorView == null) {
      mNetWorkErrorStub.setLayoutResource(getNetWorkErrorResourceId());
      mNetWorkErrorView = mNetWorkErrorStub.inflate();
      mNetWorkErrorView.setOnClickListener(mNetWorkErrorViewClickListener);
      onNewWorkErrorViewInflated(mNetWorkErrorView);
    }
    return mNetWorkErrorView;
  }

  /** 初始化view. */
  private void ensureContent() {
    if (mContentContainer != null) {// 已经初始化
      return;
    }
    View root = getView();
    if (root == null) {
      throw new IllegalStateException("Content mTempView not yet created");
    }

    // 内容
    mContentContainer = root.findViewById(R.id.content_container);
    if (mContentContainer == null) {
      throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.content_container'");
    }

    // 加载中
    mLoadingStub = root.findViewById(R.id.progress_stub);
    if (mLoadingStub == null) {
      throw new RuntimeException("Your content must have a ViewStub whose id attribute is 'R.id.progress_stub'");
    }

    // 空视图
    mEmptyStub = root.findViewById(R.id.empty_stub);
    if (mEmptyStub == null) {
      throw new RuntimeException("Your content must have a ViewStub whose id attribute is 'R.id.empty_stub'");
    }

    // 网络异常
    mNetWorkErrorStub = root.findViewById(R.id.network_error_stub);
    if (mNetWorkErrorStub == null) {
      throw new RuntimeException("Your content must have a ViewStub whose id attribute is 'R.id.network_error_stub'");
    }
  }

  /**
   * 切换当前显示的视图
   *
   * @param shownView 需要显示的View
   * @param hiddenView 需要隐藏的View
   * @param animate 动画?
   */
  private void switchView(View shownView, View hiddenView, boolean animate) {
    if (animate) {
      shownView.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_in));
      hiddenView.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_out));
    } else {
      shownView.clearAnimation();
      hiddenView.clearAnimation();
    }

    hiddenView.setVisibility(View.GONE);
    shownView.setVisibility(View.VISIBLE);
  }
}
