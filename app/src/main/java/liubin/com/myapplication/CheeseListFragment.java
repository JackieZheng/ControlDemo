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

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.mylibrary.base.BaseFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import liubin.com.myapplication.api.CustomerApi;
import liubin.com.myapplication.bean.StringData;
import timber.log.Timber;

public class CheeseListFragment extends BaseFragment
    implements BaseQuickAdapter.RequestLoadMoreListener {

  @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
  @BindView(R.id.swip) SwipeRefreshLayout mSwipeRefreshLayout;
  Unbinder mUnBinder;

  public boolean isRefresh;
  private StringAdapter stringAdapter;
  private final List<String> mData = new ArrayList<>();

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    onLoadMoreRequested();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cheese_list, container, false);
    mUnBinder = ButterKnife.bind(this, view);

    mSwipeRefreshLayout.setColorSchemeResources(//
        android.R.color.holo_blue_bright,//
        android.R.color.holo_green_light,//
        android.R.color.holo_orange_light,//
        android.R.color.holo_red_light);

    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

      @Override public void onRefresh() {
        isRefresh = true;
        stringAdapter.setEnableLoadMore(false);
        onLoadMoreRequested();
      }
    });
    setupRecyclerView(mRecyclerView);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mSwipeRefreshLayout.removeAllViews();
    stringAdapter = null;
    mUnBinder.unbind();
  }

  private void setupRecyclerView(RecyclerView recyclerView) {

    recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    stringAdapter = new StringAdapter(R.layout.list_item, mData, this);
    recyclerView.setAdapter(stringAdapter);
    stringAdapter.setOnLoadMoreListener(this, mRecyclerView);
    stringAdapter.setEmptyView(R.layout.custom_progress_layout);
  }

  private List<String> getRandomSublist(String[] array, int amount) {
    ArrayList<String> list = new ArrayList<>(amount);
    Random random = new Random();
    while (list.size() < amount) {
      list.add(array[random.nextInt(array.length)]);
    }
    return list;
  }

  @Override public void onLoadMoreRequested() {
    CustomerApi.queryData(20)//
        .subscribeOn(Schedulers.io())// 指定之前的subscribe在io线程执行
        .doOnSubscribe(new Consumer<Disposable>() {
          @Override public void accept(Disposable disposable) throws Exception {
            mDisposables.add(disposable);
          }
        })//开始执行之前的准备工作
        .subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
        .observeOn(AndroidSchedulers.mainThread())//指定 后面的subscribe在io线程执行
        .subscribe(new Consumer<StringData>() {
          @Override public void accept(StringData data) throws Exception {
            List<String> datas = data.getData();
            if (!mIsViewCreated) {
              if (datas != null && datas.size() > 0) mData.addAll(datas);
              return;
            }
            if (data.getCode() != 0) {
              isRefresh = false;
              stringAdapter.loadMoreFail();
              mSwipeRefreshLayout.setRefreshing(false);
              Toast.makeText(getContext(), data.getMessage(), Toast.LENGTH_LONG).show();
              return;
            }

            if (isRefresh) {
              isRefresh = false;
              mData.clear();
              if (datas != null && datas.size() > 0) mData.addAll(datas);
              stringAdapter.setNewData(mData);
              stringAdapter.setEnableLoadMore(true);
              mSwipeRefreshLayout.setRefreshing(false);
              if (stringAdapter.getData().size() == 0) {
                stringAdapter.setEmptyView(R.layout.custom_empty_layout);
              }
              return;
            }

            if (datas != null && datas.size() > 0) mData.addAll(datas);
            if (datas != null && datas.size() == 20) {
              stringAdapter.addData(datas);
              stringAdapter.setEnableLoadMore(true);
              stringAdapter.loadMoreComplete();
            } else {
              stringAdapter.loadMoreEnd();
            }
            if (stringAdapter.getData().size() == 0) {
              stringAdapter.setEmptyView(R.layout.custom_empty_layout);
            }
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {
            stringAdapter.loadMoreFail();
            if (stringAdapter.getData().size() == 0) {
              stringAdapter.setEmptyView(R.layout.custom_network_error_layout);
            }
            if (isRefresh) {
              isRefresh = false;
              mSwipeRefreshLayout.setRefreshing(false);
            }
            Timber.e(throwable);
            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
          }
        });
  }

  public static class StringAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private final int mBackground;
    private final Fragment mFragment;

    public StringAdapter(@LayoutRes int layoutResId, @Nullable List<String> data,
        Fragment context) {
      super(layoutResId, data);
      this.mFragment = context;
      final TypedValue mTypedValue = new TypedValue();
      context.getContext()
          .getTheme()
          .resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
      mBackground = mTypedValue.resourceId;
    }

    @Override protected void convert(BaseViewHolder helper, String item) {
      helper.itemView.setBackgroundResource(mBackground);
      helper.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

        }
      });
      helper.setText(android.R.id.text1, item);
      ImageView imageView = helper.getView(R.id.avatar);
      Glide.with(mFragment).load(Cheeses.getRandomCheeseDrawable(helper.getOldPosition()))
          .bitmapTransform(new CropCircleTransformation(mContext))
          .into(imageView);
    }
  }
}
