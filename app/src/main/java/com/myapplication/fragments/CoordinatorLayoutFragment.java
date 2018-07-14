package com.myapplication.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.mylibrary.base.BaseActivity;
import com.example.mylibrary.base.BaseFragment;
import com.myapplication.MainActivity;
import com.myapplication.R;
import timber.log.Timber;

/**
 * {@link CoordinatorLayout} {@link AppBarLayout}嵌套使用沉浸式状态栏实现
 */
public class CoordinatorLayoutFragment extends BaseFragment {

  Unbinder unbinder;
  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.tabs) TabLayout mTabLayout;
  @BindView(R.id.appbar) AppBarLayout mAppbar;
  @BindView(R.id.viewpager) ViewPager mViewpager;
  @BindView(R.id.fab) FloatingActionButton mFab;
  @BindView(R.id.main_content) CoordinatorLayout mCoordinatorLayout;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.content_coordinator_layout, container, false);
    unbinder = ButterKnife.bind(this, view);

    mActivity.setTransparentForWindow();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      // 设置状态栏颜色
      // 这句没有效果,因为会被 getWindow().setStatusBarColor(Color.RED);覆盖,如果设置为透明则可以显示
      // mCoordinatorLayout.setStatusBarBackgroundColor(Color.RED);
      mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // 通过设置背景色来修改状态栏颜色
      mCoordinatorLayout.setBackgroundColor(getResources().getColor(R.color.primary_dark));
    }
    return view;
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // 初始化标题栏(标题,返回按钮,菜单)
    mToolbar.setTitle("CoordinatorLayout");
    mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mActivity.finish();
      }
    });
    mToolbar.inflateMenu(R.menu.sample_actions);
    mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        return false;
      }
    });

    // 初始化ViewPager
    setupViewPager(mViewpager, mTabLayout);

    // 初始化TabLayout
    setupTabLayout(mTabLayout, mViewpager);

    mFab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show();
      }
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  /**
   * 初始化ViewPager
   *
   * @param viewPager {@link ViewPager}
   * @param tabLayout
   */
  private void setupViewPager(ViewPager viewPager, TabLayout tabLayout) {
    // 设置Adapter
    MainActivity.Adapter adapter = new MainActivity.Adapter(getChildFragmentManager());
    adapter.addFragment(new CheeseListFragment(), "Category 1");
    adapter.addFragment(new CheeseListFragment(), "Category 2");
    adapter.addFragment(new CheeseListFragment(), "Category 3");
    viewPager.setAdapter(adapter);
    // 设置监听
    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
      @Override public void onPageSelected(int position) {
        super.onPageSelected(position);
        Timber.d("ViewPager.TabLayoutOnPageChangeListener#onPageSelected");
      }
    });
  }

  /**
   * 初始化{@link TabLayout}
   *
   * @param tableLayout {@link TabLayout}
   * @param viewpager {@link ViewPager}
   */
  private void setupTabLayout(TabLayout tableLayout, ViewPager viewpager) {
    tableLayout.setupWithViewPager(viewpager);
    tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override public void onTabSelected(TabLayout.Tab tab) {
        Timber.d("TabLayout.OnTabSelectedListener#onTabSelected");
      }

      @Override public void onTabUnselected(TabLayout.Tab tab) {
        Timber.d("TabLayout.OnTabSelectedListener#onTabUnselected");
      }

      @Override public void onTabReselected(TabLayout.Tab tab) {
        Timber.d("TabLayout.OnTabSelectedListener#onTabReselected");
      }
    });
  }
}
