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

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.example.mylibrary.R;

import static com.example.mylibrary.base.ProgressFragment.ViewType.CONTENT;
import static com.example.mylibrary.base.ProgressFragment.ViewType.EMPTY_DATA;
import static com.example.mylibrary.base.ProgressFragment.ViewType.NETWORK_ERROR;
import static com.example.mylibrary.base.ProgressFragment.ViewType.PROGRESS;

/**
 * The implementation of the fragment to display content. Based on {@link
 * android.support.v4.app.ListFragment}.
 * If you are waiting for the initial data, you'll can displaying during this time an indeterminate
 * progress indicator.
 *
 * @author Evgeny Shishkin
 */
public abstract class ProgressFragment<T extends BaseActivity> extends BaseFragment<T> {

  /**
   * 视图类型,内容,加载中,没有数据,网络异常
   */
  enum ViewType {
    CONTENT, PROGRESS, EMPTY_DATA, NETWORK_ERROR
  }

  // 当前视图类型
  private ViewType mCurrentViewType = ViewType.CONTENT;

  private View mProgressContainer;//进度区域
  private View mContentContainer;//内容区域
  private View mContentView;//内容视图
  private View mEmptyView;//空区域
  private View mNetWorkErrorView;//网络异常视图

  private View mTempView;//临时保存创建的内容,在onViewCreated之后设置进去

  private ViewStub mProgressStub;
  private ViewStub mEmptyStub;
  private ViewStub mNetWorkErrorStub;

  private View.OnClickListener mEmptyViewClickListener;
  private View.OnClickListener mNetWorkErrorViewClickListener;

  /**
   * Provide default implementation to return a simple mTempView.  Subclasses
   * can override to replace with their own layout.  If doing so, the
   * returned mTempView hierarchy <em>must</em> have a progress container  whose id
   * is {@link  R.id#progress_container R.id.progress_container},
   * content container whose id
   * is {@link  R.id#content_container R.id.content_container} and can
   * optionally
   * have a sibling mTempView id {@link android.R.id#empty android.R.id.empty}
   * that is to be shown when the content is empty.
   * <p/>
   * <p>If you are overriding this method with your own custom content,
   * consider including the standard layout {@link  R.layout#fragment_progress}
   * in your layout file, so that you continue to retain all of the standard
   * behavior of ProgressFragment. In particular, this is currently the only
   * way to have the built-in indeterminant progress state be shown.
   */
  @Override public final View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mTempView = inflater.inflate(getFragmentContentLayoutResourceID(), null, false);
    return inflater.inflate(getFragmentLayoutResourceID(), container, false);
  }

  /**
   * {@link Fragment} 布局ID
   * 如果需要更改Fragment布局文件需要重写此方法,布局文件结构必须和R.layout.fragment_progress一致
   *
   * @return @LayoutRes(eg R.layout.fragment_progress)
   */
  public @LayoutRes int getFragmentLayoutResourceID() {
    return R.layout.fragment_progress;
  }

  /**
   * {@link Fragment} 内容区域布局ID
   *
   * @return @return @LayoutRes(eg R.layout.content_user_info)
   */
  abstract public @LayoutRes int getFragmentContentLayoutResourceID();

  /**
   * Attach to mTempView once the mTempView hierarchy has been created.
   */
  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ensureContent();
    setContentView(mTempView);
    if (mCurrentViewType != CONTENT) {
      switchView(getCurrentView(), mContentView, false);
    }
  }

  /**
   * Detach from mTempView.
   */
  @Override public void onDestroyView() {
    mProgressContainer = mContentContainer = mContentView = mEmptyView = mNetWorkErrorView = null;
    mProgressStub = mEmptyStub = mNetWorkErrorStub = null;
    super.onDestroyView();
  }

  /**
   * Return content mTempView or null if the content mTempView has not been initialized.
   *
   * @return content mTempView or null
   * @see #setContentView(View)
   * @see #setContentView(int)
   */
  public View getContentView() {
    return mContentView;
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

  /**
   * The default content for a ProgressFragment has a TextView that can be shown when
   * the content is empty {@link #setContentEmpty(boolean)}.
   * If you would like to have it shown, call this method to supply the text it should use.
   *
   * @param resId Identification of string from a resources
   * @see #setEmptyText(CharSequence)
   */
  public void setEmptyText(int resId) {
    setEmptyText(getString(resId));
  }

  /**
   * The default content for a ProgressFragment has a TextView that can be shown when
   * the content is empty {@link #setContentEmpty(boolean)}.
   * If you would like to have it shown, call this method to supply the text it should use.
   *
   * @param text Text for empty mTempView
   * @see #setEmptyText(int)
   */
  public void setEmptyText(CharSequence text) {
    ensureContent();
    if (mEmptyView != null && mEmptyView instanceof TextView) {
      ((TextView) mEmptyView).setText(text);
    } else {
      throw new IllegalStateException("Can't be used with a custom content mTempView");
    }
  }

  /**
   * 显示进度
   */
  public void showProgress() {
    if (mCurrentViewType == PROGRESS) return;
    if (isViewCreated) {
      View hideView = getCurrentView();
      View showView = getProgressContainer();
      switchView(showView, hideView, false);
    }
    mCurrentViewType = PROGRESS;
  }

  /**
   * 显示内容
   */
  public void showContent() {
    if (mCurrentViewType == CONTENT) return;
    if (isViewCreated) {
      View hideView = getCurrentView();
      View showView = mContentView;
      switchView(showView, hideView, false);
    }
    mCurrentViewType = CONTENT;
  }

  /**
   * 显示空视图
   */
  public void showEmpty() {
    if (mCurrentViewType == EMPTY_DATA) return;
    if (isViewCreated) {
      View hideView = getCurrentView();
      View showView = getEmptyView();
      switchView(showView, hideView, false);
    }
    mCurrentViewType = EMPTY_DATA;
  }

  /**
   * 显示网络错误
   */
  public void showNetWorkError() {
    if (mCurrentViewType == NETWORK_ERROR) return;
    if (isViewCreated) {
      View hideView = getCurrentView();
      View showView = getNetWorkErrorView();
      switchView(showView, hideView, false);
    }
    mCurrentViewType = NETWORK_ERROR;
  }

  /**
   * 设置空视图点击事件
   *
   * @param emptyViewClickListener
   */
  public void setEmptyViewClickListener(View.OnClickListener emptyViewClickListener) {
    if (mEmptyView != null) {
      mEmptyView.setOnClickListener(emptyViewClickListener);
    }
    mEmptyViewClickListener = emptyViewClickListener;
  }

  /**
   * 设置网络异常点击事件
   *
   * @param netWorkErrorViewClickListener
   */
  public void setNetWorkErrorViewClickListener(View.OnClickListener netWorkErrorViewClickListener) {
    if (mNetWorkErrorView != null) {
      mNetWorkErrorView.setOnClickListener(netWorkErrorViewClickListener);
    }
    mNetWorkErrorViewClickListener = netWorkErrorViewClickListener;
  }

  /**
   * Initialization views.
   */
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
      throw new RuntimeException(
          "Your content must have a ViewGroup whose id attribute is 'R.id.content_container'");
    }

    // 加载进度
    mProgressStub = (ViewStub) root.findViewById(R.id.progress_stub);
    if (mProgressStub == null) {
      throw new RuntimeException(
          "Your content must have a ViewStub whose id attribute is 'R.id.progress_stub'");
    }

    // 空视图
    mEmptyStub = (ViewStub) root.findViewById(R.id.empty_stub);
    if (mEmptyStub == null) {
      throw new RuntimeException(
          "Your content must have a ViewStub whose id attribute is 'R.id.empty_stub'");
    }

    // 网络异常
    mNetWorkErrorStub = (ViewStub) root.findViewById(R.id.network_error_stub);
    if (mNetWorkErrorStub == null) {
      throw new RuntimeException(
          "Your content must have a ViewStub whose id attribute is 'R.id.network_error_stub'");
    }
  }

  public View getEmptyView() {
    if (mEmptyView == null) {
      mEmptyView = mEmptyStub.inflate();
      mEmptyView.setOnClickListener(mEmptyViewClickListener);
    }
    return mEmptyView;
  }

  private View getProgressContainer() {
    if (mProgressContainer == null) mProgressContainer = mProgressStub.inflate();
    return mProgressContainer;
  }

  private View getNetWorkErrorView() {
    if (mNetWorkErrorView == null) {
      mNetWorkErrorView = mNetWorkErrorStub.inflate();
      mNetWorkErrorView.setOnClickListener(mNetWorkErrorViewClickListener);
    }
    return mNetWorkErrorView;
  }

  /**
   * 获取当前显示的View
   *
   * @return 当前显示的View
   */
  private View getCurrentView() {
    View view = null;
    switch (mCurrentViewType) {
      case PROGRESS: {
        view = getProgressContainer();
        break;
      }
      case CONTENT: {
        view = mContentView;
        break;
      }
      case EMPTY_DATA: {
        view = getEmptyView();
        break;
      }
      case NETWORK_ERROR: {
        view = getNetWorkErrorView();
        break;
      }
    }
    return view;
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
      shownView.startAnimation(
          AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_in));
      hiddenView.startAnimation(
          AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_out));
    } else {
      shownView.clearAnimation();
      hiddenView.clearAnimation();
    }

    shownView.setVisibility(View.VISIBLE);
    hiddenView.setVisibility(View.GONE);
  }
}
