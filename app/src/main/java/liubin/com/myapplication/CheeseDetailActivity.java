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

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.example.mylibrary.base.BaseActivity;
import com.r0adkll.slidr.Slidr;

public class CheeseDetailActivity extends BaseActivity {

  public static final String EXTRA_NAME = "cheese_name";
  @BindView(R.id.backdrop) ImageView mImageView;
  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
  @BindView(R.id.appbar) AppBarLayout mAppbar;
  @BindView(R.id.textView) TextView mTextView;
  @BindView(R.id.main_content) CoordinatorLayout mMainContent;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Slidr.attach(this);
    setContentView(R.layout.activity_detail);
    ButterKnife.bind(this);

    setTransparentForWindow();

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
          layoutParams.topMargin + getSystemBarConfig().getStatusBarHeight(),
          layoutParams.rightMargin, layoutParams.bottomMargin);
    }

    mToolbar.setTitle("基本测试");
    mToolbar.inflateMenu(R.menu.sample_actions);
    mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    //setSupportActionBar(mToolbar);
    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    //设置标题文字
    mCollapsingToolbar.setTitle(getIntent().getStringExtra(EXTRA_NAME));
    //状态栏颜色
    mCollapsingToolbar.setStatusBarScrimColor(getResources().getColor(R.color.primary));
    //折叠后标题文字颜色
    mCollapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
    //扩张时候的title颜色
    mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.primary));

    Glide.with(this).load(Cheeses.getRandomCheeseDrawable(0)).centerCrop().into(mImageView);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.sample_actions, menu);
    return true;
  }
}
