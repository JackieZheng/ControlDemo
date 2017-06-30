package com.myapplication.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.request.RequestOptions;
import com.example.mylibrary.base.BaseActivity;
import com.example.mylibrary.base.BaseFragment;
import com.myapplication.R;
import com.myapplication.glide.GlideApp;
import com.r0adkll.slidr.Slidr;

/**
 * <pre>
 * {@link CoordinatorLayout}
 * {@link CollapsingToolbarLayout}
 * {@link AppBarLayout}
 * 嵌套使用沉浸式状态栏实现
 * </pre>
 */
public class CollapsingToolbarLayoutFragment extends BaseFragment<BaseActivity> {
  public static final String EXTRA_NAME = "cheese_name";
  public static final String EXTRA_ICON = "cheese_icon";

  @BindView(R.id.backdrop) ImageView mImageView;
  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
  @BindView(R.id.appbar) AppBarLayout mAppbar;
  @BindView(R.id.textView) TextView mTextView;
  @BindView(R.id.main_content) CoordinatorLayout mMainContent;
  private Unbinder mUnBinder;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail, container, false);
    mUnBinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Slidr.attach(mActivity);
    mActivity.setTransparentForWindow();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      mMainContent.setFitsSystemWindows(false);
      mAppbar.setFitsSystemWindows(false);
      mCollapsingToolbar.setFitsSystemWindows(false);
      mImageView.setFitsSystemWindows(false);
      mToolbar.setFitsSystemWindows(false);
      ViewGroup.MarginLayoutParams layoutParams =
          (ViewGroup.MarginLayoutParams) mToolbar.getLayoutParams();
      layoutParams.setMargins(layoutParams.leftMargin,
          layoutParams.topMargin + mActivity.getSystemBarConfig().getStatusBarHeight(),
          layoutParams.rightMargin, layoutParams.bottomMargin);
    }

    mToolbar.setTitle("基本测试");
    mToolbar.inflateMenu(R.menu.sample_actions);
    mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mActivity.finish();
      }
    });

    //setSupportActionBar(mToolbar);
    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    //设置标题文字
    mCollapsingToolbar.setTitle(getArguments().getString(EXTRA_NAME));
    //状态栏颜色
    mCollapsingToolbar.setStatusBarScrimColor(getResources().getColor(R.color.primary));
    //折叠后标题文字颜色
    mCollapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
    //扩张时候的title颜色
    mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.primary));

    GlideApp.with(this)
        .load(getArguments().getInt(EXTRA_ICON, 0))
        .apply(RequestOptions.centerCropTransform())
        .into(mImageView);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mUnBinder.unbind();
  }
}
