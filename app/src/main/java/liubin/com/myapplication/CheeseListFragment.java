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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.mylibrary.base.BaseFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CheeseListFragment extends BaseFragment {

  @BindView(R.id.recyclerview) RecyclerView mRecyclerview;
  @BindView(R.id.swip) SwipeRefreshLayout mSwipeRefreshLayout;
  Unbinder unbinder;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cheese_list, container, false);
    unbinder = ButterKnife.bind(this, view);

    mSwipeRefreshLayout.setColorSchemeResources(//
        android.R.color.holo_blue_bright,//
        android.R.color.holo_green_light,//
        android.R.color.holo_orange_light,//
        android.R.color.holo_red_light);

    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        Observable.timer(4000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())// 指定之前的subscribe在io线程执行
            .doOnSubscribe(new Consumer<Disposable>() {
              @Override public void accept(Disposable disposable) throws Exception {
                mDisposables.add(disposable);
              }
            })//开始执行之前的准备工作
            .subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
            .observeOn(AndroidSchedulers.mainThread())//指定 后面的subscribe在io线程执行
            .subscribe(new Consumer<Long>() {
              @Override public void accept(Long aLong) throws Exception {
                if (isViewCreated) {
                  mSwipeRefreshLayout.setRefreshing(false);
                  mRecyclerview.getAdapter().notifyDataSetChanged();
                }
              }
            });
      }
    });
    setupRecyclerView(mRecyclerview);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mSwipeRefreshLayout.removeAllViews();
    unbinder.unbind();
  }

  private void setupRecyclerView(RecyclerView recyclerView) {
    recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    //recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),
    //  getRandomSublist(Cheeses.sCheeseStrings, 30)));
    recyclerView.setAdapter(
        new StringAdapter(R.layout.list_item, getRandomSublist(Cheeses.sCheeseStrings, 30), this));
  }

  private List<String> getRandomSublist(String[] array, int amount) {
    ArrayList<String> list = new ArrayList<>(amount);
    Random random = new Random();
    while (list.size() < amount) {
      list.add(array[random.nextInt(array.length)]);
    }
    return list;
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
      Glide.with(mFragment)
          .load(Cheeses.getRandomCheeseDrawable())
          .bitmapTransform(new CropCircleTransformation(mContext))
          .into(imageView);
    }
  }

  public static class SimpleStringRecyclerViewAdapter
      extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<String> mValues;

    public static class ViewHolder extends RecyclerView.ViewHolder {
      public String mBoundString;

      public final View mView;
      public final ImageView mImageView;
      public final TextView mTextView;

      public ViewHolder(View view) {
        super(view);
        mView = view;
        mImageView = (ImageView) view.findViewById(R.id.avatar);
        mTextView = (TextView) view.findViewById(android.R.id.text1);
      }

      @Override public String toString() {
        return super.toString() + " '" + mTextView.getText();
      }
    }

    public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
      context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
      mBackground = mTypedValue.resourceId;
      mValues = items;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
      view.setBackgroundResource(mBackground);
      return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {
      holder.mBoundString = mValues.get(position);
      holder.mTextView.setText(mValues.get(position));

      holder.mView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Context context = v.getContext();
          Intent intent = new Intent(context, CheeseDetailActivity.class);
          intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);

          context.startActivity(intent);
        }
      });

      Glide.with(holder.mImageView.getContext())
          .load(Cheeses.getRandomCheeseDrawable())
          .bitmapTransform(new CropCircleTransformation(holder.mImageView.getContext()))
          .into(holder.mImageView);
    }

    @Override public int getItemCount() {
      return mValues.size();
    }
  }
}
