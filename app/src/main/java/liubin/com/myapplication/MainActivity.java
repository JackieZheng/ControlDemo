/*
 * Copyright (C) 2015 The Android Open Source Project
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

package liubin.com.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.mylibrary.base.ActivityUtils;
import com.example.mylibrary.base.ApiClient;
import com.example.mylibrary.base.BaseActivity;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import liubin.com.myapplication.api.CustomerApi;
import liubin.com.myapplication.bean.User;
import liubin.com.myapplication.fragments.BasicFragment;
import liubin.com.myapplication.fragments.CheeseListFragment;
import liubin.com.myapplication.fragments.CollapsingToolbarLayoutFragment;
import liubin.com.myapplication.fragments.CoordinatorLayoutFragment;
import liubin.com.myapplication.fragments.CustomFragment;
import liubin.com.myapplication.fragments.DrawerLayoutFragment;
import liubin.com.myapplication.fragments.PictureListFragment;
import liubin.com.myapplication.fragments.kotlin.KotlinFragment;
import liubin.com.myapplication.fragments.mvp.MVPFragment;
import timber.log.Timber;

/**
 * DrawerLayout NavigationView CoordinatorLayout嵌套使用
 * 状态栏层级从下到上 依次是 CoordinatorLayout,NavigationView,系统状态栏
 */
public class MainActivity extends BaseActivity {

  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.tabs) TabLayout mTabLayout;
  @BindView(R.id.appbar) AppBarLayout mAppbar;
  @BindView(R.id.viewpager) ViewPager mViewpager;
  @BindView(R.id.fab) FloatingActionButton mFab;
  @BindView(R.id.main_content) CoordinatorLayout mCoordinatorLayout;
  @BindView(R.id.nav_view) NavigationView mNavigationView;
  @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

  @Override public int getContentResourceId() {
    return R.layout.activity_main;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);

    // 初始化状态栏,标题栏
    setupTopBar();
    // 初始化左侧导航栏
    setupDrawerContent(mNavigationView);

    // 这一句使得ToolBar可以通过onOptionsItemSelected,方法来处理点击事件.
    setSupportActionBar(mToolbar);

    // 初始化ViewPager
    setupViewPager(mViewpager, mTabLayout);

    // 初始化TabLayout
    setupTabLayout(mTabLayout, mViewpager);

    mFab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show();
        CustomerApi.test();
        User user = new User();
        user.setName("nnnn");
        user.setAge(333);
        String s = ApiClient.getGson().toJson(user);
        user = ApiClient.getGson().fromJson(s, TypeToken.get(User.class).getType());
        Timber.e(user.getName());
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

  /**
   * 初始化顶部栏
   */
  private void setupTopBar() {
    mToolbar.setNavigationIcon(R.drawable.ic_menu);
    // 状态栏透明,内容全屏
    setTransparentForWindow();
    // 注意区分版本进行处理
    //CoordinatorLayout#fitsSystemWindows:true只针对Api19+有效,Api21+可以不用设置
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      // 使得CoordinatorLayout与顶部空出状态栏高度
      mCoordinatorLayout.setPadding(0, this.getSystemBarConfig().getStatusBarHeight(), 0, 0);
      // 设置(覆盖在 CoordinatorLayout 上)状态栏颜色
      mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));
      // 设置覆盖在NavigationView抽屉上的状态栏颜色,这个颜色如果透明会和mDrawerLayout.setStatusBarBackgroundColor
      // 设置的颜色重叠,否则将覆盖mDrawerLayout.setStatusBarBackgroundColor设置的颜色
      // getWindow().setStatusBarColor(0x66888888);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      //mCoordinatorLayout.setFitsSystemWindows(true);
      //mCoordinatorLayout.setPadding(0, 0, 0, 0);
      // 状态栏颜色设置
      mCoordinatorLayout.setBackgroundColor(getResources().getColor(R.color.primary_dark));
      // 如果需要设置覆盖在NavigationView抽屉上的状态栏颜色,
      // 需要在android.R.id.content对应的ViewGroup中绘制一个和状态栏一样高的矩形
      /*View statusBarView = new View(this);
      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
              getSystemBarConfig().getStatusBarHeight());
      statusBarView.setLayoutParams(params);
      statusBarView.setBackgroundColor(0x66888888);
      ((ViewGroup) findViewById(android.R.id.content)).addView(statusBarView);*/
    }
    // Api21+这一句是关键,布局文件里面设置这个属性为true,代码里面需要设置这个属性为false
    mDrawerLayout.setFitsSystemWindows(false);
    // TODO: 状态栏会挡住抽屉内容,这个需要设置padding属性才能解决
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.sample_actions, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        mDrawerLayout.openDrawer(GravityCompat.START);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * 初始化ViewPager
   *
   * @param viewPager {@link ViewPager}
   * @param tabLayout {@link TabLayout}
   */
  private void setupViewPager(ViewPager viewPager, TabLayout tabLayout) {
    // 设置Adapter
    MainActivity.Adapter adapter = new MainActivity.Adapter(getSupportFragmentManager());
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
   * 初始化左侧导航栏
   *
   * @param navigationView {@link NavigationView}
   */
  private void setupDrawerContent(NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

              case R.id.nav_home: {//ListFragment 基本使用
                ActivityUtils.startActivity(MainActivity.this, BasicFragment.class, null, -1);
                break;
              }
              case R.id.nav_custom: {//ProgressFragment 或 ListFragment 各种视图自定义
                ActivityUtils.startActivity(MainActivity.this, CustomFragment.class, null, -1);
                break;
              }
              case R.id.nav_kotlin: {//kotlin 使用
                ActivityUtils.startActivity(MainActivity.this, KotlinFragment.class, null, -1);
                break;
              }
              case R.id.nav_mvp: {//MVP 模式使用
                ActivityUtils.startActivity(MainActivity.this, MVPFragment.class, null, -1);
                break;
              }
              case R.id.nav_friends: {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
                break;
              }

              //---------------------------------- 沉浸式状态栏 ----------------------------------//
              case R.id.nav_collapsing_toolbar_layout: {//CollapsingToolbarLayout 沉浸式状态栏
                Bundle bundle = new Bundle();
                bundle.putString(CollapsingToolbarLayoutFragment.EXTRA_NAME,
                    "CollapsingToolbarLayout沉浸式");
                bundle.putInt(CollapsingToolbarLayoutFragment.EXTRA_ICON, R.drawable.cheese_1);
                ActivityUtils.startActivity(MainActivity.this,
                    CollapsingToolbarLayoutFragment.class, bundle, -1);
                break;
              }
              case R.id.nav_coordinator_layout: {//CoordinatorLayout 沉浸式状态栏
                ActivityUtils.startActivity(MainActivity.this, CoordinatorLayoutFragment.class,
                    null, -1);
                break;
              }
              case R.id.nav_drawer_layout: {//DrawerLayout 沉浸式状态栏
                ActivityUtils.startActivity(MainActivity.this, DrawerLayoutFragment.class, null,
                    -1);
                break;
              }

              //---------------------------------- 其他使用 ----------------------------------//
              case R.id.nav_fullscreen: {//全屏的 Activity
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FullscreenActivity.class);
                startActivity(intent);
                break;
              }
              case R.id.nav_picture: {//相册图片读取
                ActivityUtils.startActivity(MainActivity.this, PictureListFragment.class, null, -1);
                break;
              }
            }
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
          }
        });
  }

  /**
   * ViewPage适配器
   */
  public static class Adapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public Adapter(FragmentManager fm) {
      super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
      mFragments.add(fragment);
      mFragmentTitles.add(title);
    }

    @Override public Fragment getItem(int position) {
      return mFragments.get(position);
    }

    @Override public int getCount() {
      return mFragments.size();
    }

    @Override public CharSequence getPageTitle(int position) {
      return mFragmentTitles.get(position);
    }
  }
}
